package com.queueup.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.queueup.dto.response.QueueStateResponse;
import com.queueup.exception.QueueNotFoundException;
import com.queueup.service.QueueService;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;

@Component
public class QueueWebSocketHandler extends TextWebSocketHandler {

    private final WebSocketSessionManager sessionManager;
    private final QueueService queueService;
    private final ObjectMapper objectMapper;

    public QueueWebSocketHandler(WebSocketSessionManager sessionManager,
                                 QueueService queueService,
                                 ObjectMapper objectMapper) {
        this.sessionManager = sessionManager;
        this.queueService = queueService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String queueCode = extractQueueCode(session);

        if (queueCode == null) {
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        // Validate queue exists
        try {
            queueService.getQueueByCode(queueCode);
        } catch (QueueNotFoundException e) {
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        // Store session
        sessionManager.addSession(queueCode, session);

        // Send initial state immediately
        QueueStateResponse state = queueService.getQueueState(queueCode);
        String message = objectMapper.writeValueAsString(state);
        session.sendMessage(new TextMessage(message));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessionManager.removeSession(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Client can send their token to get personalized position
        String payload = message.getPayload();

        if (payload != null && !payload.isEmpty()) {
            try {
                com.fasterxml.jackson.databind.JsonNode node = objectMapper.readTree(payload);

                if (node.has("token")) {
                    String token = node.get("token").asText();
                    String queueCode = extractQueueCode(session);

                    // Store token in session attributes
                    session.getAttributes().put("token", token);

                    // Send personalized state
                    QueueStateResponse state = queueService.buildQueueState(
                            queueService.getQueueByCode(queueCode),
                            token
                    );
                    String response = objectMapper.writeValueAsString(state);
                    session.sendMessage(new TextMessage(response));
                }
            } catch (Exception e) {
                // Invalid JSON, ignore
                System.err.println("Invalid WebSocket message: " + payload);
            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        System.err.println("WebSocket transport error: " + exception.getMessage());
        sessionManager.removeSession(session);
    }

    private String extractQueueCode(WebSocketSession session) {
        URI uri = session.getUri();
        if (uri == null) return null;

        // Extract from path: /ws/queue/KX89B2
        String path = uri.getPath();
        String[] parts = path.split("/");

        if (parts.length >= 4 && "ws".equals(parts[1]) && "queue".equals(parts[2])) {
            return parts[3];
        }

        return null;
    }
}