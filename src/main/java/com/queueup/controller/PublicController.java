package com.queueup.controller;

import com.queueup.dto.request.JoinQueueRequest;
import com.queueup.dto.response.JoinQueueResponse;
import com.queueup.dto.response.QueueStateResponse;
import com.queueup.exception.RateLimitExceededException;
import com.queueup.service.QueueService;
import com.queueup.service.RateLimitService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PublicController {

    private final QueueService queueService;
    private final RateLimitService rateLimitService;

    public PublicController(QueueService queueService, RateLimitService rateLimitService) {
        this.queueService = queueService;
        this.rateLimitService = rateLimitService;
    }

    // Join a queue
    @PostMapping("/queues/{code}/join")
    public ResponseEntity<JoinQueueResponse> joinQueue(
            @PathVariable String code,
            @RequestBody JoinQueueRequest request,
            HttpServletRequest httpRequest) {

        // Rate limiting by IP
        String clientIp = getClientIp(httpRequest);
        if (!rateLimitService.isAllowed(clientIp)) {
            throw new RateLimitExceededException();
        }

        JoinQueueResponse response = queueService.joinQueue(
                code,
                request.getName()
        );

        return ResponseEntity.ok(response);
    }

    // Get queue status (with optional token for personalized info)
    @GetMapping("/queues/{code}/status")
    public ResponseEntity<QueueStateResponse> getQueueStatus(
            @PathVariable String code,
            @RequestParam(required = false) String token) {

        QueueStateResponse response;

        if (token != null) {
            response = queueService.buildQueueState(
                    queueService.getQueueByCode(code),
                    token
            );
        } else {
            response = queueService.getQueueState(code);
        }

        return ResponseEntity.ok(response);
    }

    // Health check endpoint
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }

    // Helper to get client IP
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}