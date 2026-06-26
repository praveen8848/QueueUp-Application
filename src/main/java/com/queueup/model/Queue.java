package com.queueup.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "queues")
public class Queue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 6)
    private String code;

    @Column(name = "admin_token", unique = true, length = 36)
    private String adminToken;  // NEW: UUID for admin access

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private QueueStatus status = QueueStatus.OPEN;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "last_activity", nullable = false)
    private LocalDateTime lastActivity = LocalDateTime.now();

    @OneToMany(mappedBy = "queue", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("position ASC")
    private List<Ticket> tickets = new ArrayList<>();

    // Constructors
    public Queue() {}

    public static Queue create(String code) {
        Queue queue = new Queue();
        queue.setCode(code);
        queue.setAdminToken(UUID.randomUUID().toString());  // Generate unique admin token
        queue.setStatus(QueueStatus.OPEN);
        queue.setCreatedAt(LocalDateTime.now());
        queue.setLastActivity(LocalDateTime.now());
        return queue;
    }

    // Business methods
    public boolean isOpen() {
        return status == QueueStatus.OPEN;
    }

    public boolean isPaused() {
        return status == QueueStatus.PAUSED;
    }

    public boolean isClosed() {
        return status == QueueStatus.CLOSED;
    }

    public boolean canJoin() {
        return status == QueueStatus.OPEN || status == QueueStatus.PAUSED;
    }

    public void pause() {
        if (this.status == QueueStatus.OPEN) {
            this.status = QueueStatus.PAUSED;
        }
    }

    public void resume() {
        if (this.status == QueueStatus.PAUSED) {
            this.status = QueueStatus.OPEN;
        }
    }

    public void close() {
        this.status = QueueStatus.CLOSED;
    }

    public void updateActivity() {
        this.lastActivity = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getAdminToken() { return adminToken; }
    public void setAdminToken(String adminToken) { this.adminToken = adminToken; }

    public QueueStatus getStatus() { return status; }
    public void setStatus(QueueStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getLastActivity() { return lastActivity; }
    public void setLastActivity(LocalDateTime lastActivity) { this.lastActivity = lastActivity; }

    public List<Ticket> getTickets() { return tickets; }
    public void setTickets(List<Ticket> tickets) { this.tickets = tickets; }
}