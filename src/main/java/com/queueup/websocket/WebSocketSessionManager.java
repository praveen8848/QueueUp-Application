package com.queueup.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketSessionManager {

    // Map: queueCode -> Set of WebSocket sessions watching that queue
    private final ConcurrentHashMap<String, Set<WebSocketSession>> queueSessions = new ConcurrentHashMap<>();

    // Map: sessionId -> queueCode (for cleanup when disconnecting)
    private final ConcurrentHashMap<String, String> sessionToQueue = new ConcurrentHashMap<>();

    public void addSession(String queueCode, WebSocketSession session) {
        queueSessions.computeIfAbsent(queueCode, k -> ConcurrentHashMap.newKeySet())
                .add(session);
        sessionToQueue.put(session.getId(), queueCode);
        System.out.println("WebSocket connected to queue " + queueCode +
                " (Total: " + getActiveConnectionCount(queueCode) + ")");
    }

    public void removeSession(WebSocketSession session) {
        String queueCode = sessionToQueue.remove(session.getId());
        if (queueCode != null) {
            Set<WebSocketSession> sessions = queueSessions.get(queueCode);
            if (sessions != null) {
                sessions.remove(session);
                if (sessions.isEmpty()) {
                    queueSessions.remove(queueCode);
                }
            }
            System.out.println("WebSocket disconnected from queue " + queueCode);
        }
    }

    public void broadcast(String queueCode, String message) {
        Set<WebSocketSession> sessions = queueSessions.get(queueCode);
        if (sessions == null || sessions.isEmpty()) {
            return; // No one listening, skip broadcast
        }

        TextMessage textMessage = new TextMessage(message);
        int successCount = 0;

        for (WebSocketSession session : sessions) {
            try {
                if (session.isOpen()) {
                    synchronized (session) {
                        session.sendMessage(textMessage);
                    }
                    successCount++;
                } else {
                    removeSession(session);
                }
            } catch (IOException e) {
                System.err.println("Failed to send to session " + session.getId());
                removeSession(session);
            }
        }
    }

    public int getActiveConnectionCount(String queueCode) {
        Set<WebSocketSession> sessions = queueSessions.get(queueCode);
        return sessions != null ? sessions.size() : 0;
    }

    public int getTotalConnections() {
        return sessionToQueue.size();
    }
}