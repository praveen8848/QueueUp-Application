package com.queueup.controller;

import com.queueup.dto.response.QueueCreatedResponse;
import com.queueup.dto.response.QueueStateResponse;
import com.queueup.dto.response.ServeNextResponse;
import com.queueup.dto.response.StatsResponse;
import com.queueup.model.Queue;
import com.queueup.model.TicketStatus;
import com.queueup.repository.TicketRepository;
import com.queueup.service.QueueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final QueueService queueService;
    private final TicketRepository ticketRepository;

    public AdminController(QueueService queueService, TicketRepository ticketRepository) {
        this.queueService = queueService;
        this.ticketRepository = ticketRepository;
    }

    // Helper method to validate admin access
    private Queue validateAdminAccess(String queueCode, String adminToken) {
        Queue queue = queueService.getQueueByCode(queueCode);

        // Check if admin token matches
        if (adminToken == null || !adminToken.equals(queue.getAdminToken())) {
            throw new RuntimeException("Unauthorized: Invalid admin token");
        }

        return queue;
    }

    // Create new queue (no auth needed - returns admin token)
    @PostMapping("/queues")
    public ResponseEntity<QueueCreatedResponse> createQueue() {
        QueueCreatedResponse response = queueService.createQueue();
        return ResponseEntity.ok(response);
    }

    // Serve next customer
    @PostMapping("/queues/{code}/serve")
    public ResponseEntity<ServeNextResponse> serveNext(
            @PathVariable String code,
            @RequestHeader(value = "X-Admin-Token", required = false) String adminToken,
            @RequestParam(value = "token", required = false) String tokenParam) {

        // Support both header and query param for admin token
        String token = adminToken != null ? adminToken : tokenParam;
        validateAdminAccess(code, token);

        ServeNextResponse response = queueService.serveNext(code);
        return ResponseEntity.ok(response);
    }

    // Pause queue
    @PostMapping("/queues/{code}/pause")
    public ResponseEntity<Map<String, String>> pauseQueue(
            @PathVariable String code,
            @RequestHeader(value = "X-Admin-Token", required = false) String adminToken,
            @RequestParam(value = "token", required = false) String tokenParam) {

        String token = adminToken != null ? adminToken : tokenParam;
        validateAdminAccess(code, token);

        queueService.pauseQueue(code);
        return ResponseEntity.ok(Map.of("message", "Queue paused successfully"));
    }

    // Resume queue
    @PostMapping("/queues/{code}/resume")
    public ResponseEntity<Map<String, String>> resumeQueue(
            @PathVariable String code,
            @RequestHeader(value = "X-Admin-Token", required = false) String adminToken,
            @RequestParam(value = "token", required = false) String tokenParam) {

        String token = adminToken != null ? adminToken : tokenParam;
        validateAdminAccess(code, token);

        queueService.resumeQueue(code);
        return ResponseEntity.ok(Map.of("message", "Queue resumed successfully"));
    }

    // Close queue
    @PostMapping("/queues/{code}/close")
    public ResponseEntity<Map<String, String>> closeQueue(
            @PathVariable String code,
            @RequestHeader(value = "X-Admin-Token", required = false) String adminToken,
            @RequestParam(value = "token", required = false) String tokenParam) {

        String token = adminToken != null ? adminToken : tokenParam;
        validateAdminAccess(code, token);

        queueService.closeQueue(code);
        return ResponseEntity.ok(Map.of("message", "Queue closed successfully"));
    }

    // Get queue details
    @GetMapping("/queues/{code}")
    public ResponseEntity<QueueStateResponse> getQueueDetail(
            @PathVariable String code,
            @RequestHeader(value = "X-Admin-Token", required = false) String adminToken,
            @RequestParam(value = "token", required = false) String tokenParam) {

        String token = adminToken != null ? adminToken : tokenParam;
        validateAdminAccess(code, token);

        QueueStateResponse response = queueService.getQueueState(code);
        return ResponseEntity.ok(response);
    }

    // Get stats
    @GetMapping("/stats")
    public ResponseEntity<StatsResponse> getStats(
            @RequestHeader(value = "X-Admin-Token", required = false) String adminToken,
            @RequestParam(value = "token", required = false) String tokenParam) {

        String token = adminToken != null ? adminToken : tokenParam;
        Queue queue = queueService.getQueueByAdminToken(token);

        long servedToday = ticketRepository.countByQueueAndStatus(queue, TicketStatus.SERVED);

        return ResponseEntity.ok(new StatsResponse(1, servedToday, 5.0));
    }
    @GetMapping("/queues/{code}/metrics")
    public ResponseEntity<Map<String, Object>> getQueueMetrics(
            @PathVariable String code,
            @RequestHeader(value = "X-Admin-Token", required = false) String adminToken,
            @RequestParam(value = "token", required = false) String tokenParam) {

        String token = adminToken != null ? adminToken : tokenParam;
        validateAdminAccess(code, token);

        Map<String, Object> metrics = queueService.getQueueMetrics(code);
        return ResponseEntity.ok(metrics);
    }
    // Add to AdminController.java

    @PostMapping("/queues/{code}/call")
    public ResponseEntity<ServeNextResponse> callNextToCounter(
            @PathVariable String code,
            @RequestParam(defaultValue = "1") int minutes,
            @RequestHeader(value = "X-Admin-Token", required = false) String adminToken,
            @RequestParam(value = "token", required = false) String tokenParam) {

        String token = adminToken != null ? adminToken : tokenParam;
        validateAdminAccess(code, token);

        ServeNextResponse response = queueService.callNextToCounter(code, minutes);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/queues/{code}/noshow/{ticketId}")
    public ResponseEntity<Map<String, String>> markNoShow(
            @PathVariable String code,
            @PathVariable String ticketId,  // CHANGE TO STRING
            @RequestHeader(value = "X-Admin-Token", required = false) String adminToken,
            @RequestParam(value = "token", required = false) String tokenParam) {

        String token = adminToken != null ? adminToken : tokenParam;
        validateAdminAccess(code, token);

        // Handle "undefined" string
        if (ticketId == null || ticketId.equals("undefined") || ticketId.equals("null")) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid ticket ID. Please refresh and try again."));
        }

        try {
            Long id = Long.valueOf(ticketId);
            queueService.markTicketAsNoShow(code, id);
            return ResponseEntity.ok(Map.of("message", "Customer marked as no-show"));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid ticket ID format: " + ticketId));
        }
    }
}