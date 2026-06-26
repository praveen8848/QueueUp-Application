package com.queueup.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "queue_id", nullable = false)
    private Queue queue;

    @Column(nullable = false, length = 5)
    private String token;

    @Column(length = 100)
    private String name = "Anonymous";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TicketStatus status = TicketStatus.WAITING;

    @Column(nullable = false)
    private Integer position = 0;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt = LocalDateTime.now();

    @Column(name = "called_at")
    private LocalDateTime calledAt;

    @Column(name = "served_at")
    private LocalDateTime servedAt;

    @Column(name = "counter_called_at")
    private LocalDateTime counterCalledAt;

    @Column(name = "counter_deadline")
    private LocalDateTime counterDeadline;

    // Constructors
    public Ticket() {}

    public static Ticket create(Queue queue, String name, String token, int position) {
        Ticket ticket = new Ticket();
        ticket.setQueue(queue);
        ticket.setName(name != null ? name : "Anonymous");
        ticket.setToken(token);
        ticket.setPosition(position);
        ticket.setStatus(TicketStatus.WAITING);
        ticket.setJoinedAt(LocalDateTime.now());
        return ticket;
    }

    // Business methods
    public boolean isWaiting() {
        return status == TicketStatus.WAITING;
    }

    public void markAsServed() {
        this.status = TicketStatus.SERVED;
        this.servedAt = LocalDateTime.now();
    }

    public void markAsNoShow() {
        this.status = TicketStatus.NO_SHOW;
    }

    public void markAsCalled() {
        this.calledAt = LocalDateTime.now();
    }

    public void cancel() {
        this.status = TicketStatus.CANCELLED;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Queue getQueue() { return queue; }
    public void setQueue(Queue queue) { this.queue = queue; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public TicketStatus getStatus() { return status; }
    public void setStatus(TicketStatus status) { this.status = status; }

    public Integer getPosition() { return position; }
    public void setPosition(Integer position) { this.position = position; }

    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }

    public LocalDateTime getCalledAt() { return calledAt; }
    public void setCalledAt(LocalDateTime calledAt) { this.calledAt = calledAt; }

    public LocalDateTime getServedAt() { return servedAt; }
    public void setServedAt(LocalDateTime servedAt) { this.servedAt = servedAt; }

    public void markAsCalledToCounter(int waitMinutes) {
        this.counterCalledAt = LocalDateTime.now();
        this.counterDeadline = LocalDateTime.now().plusMinutes(waitMinutes);
    }

    public boolean isCounterCallExpired() {
        return counterDeadline != null && LocalDateTime.now().isAfter(counterDeadline);
    }

    public long getCounterSecondsRemaining() {
        if (counterDeadline == null) return 0;
        return Math.max(0, ChronoUnit.SECONDS.between(LocalDateTime.now(), counterDeadline));
    }

    // Add getters and setters
    public LocalDateTime getCounterCalledAt() { return counterCalledAt; }
    public void setCounterCalledAt(LocalDateTime counterCalledAt) { this.counterCalledAt = counterCalledAt; }
    public LocalDateTime getCounterDeadline() { return counterDeadline; }
    public void setCounterDeadline(LocalDateTime counterDeadline) { this.counterDeadline = counterDeadline; }
}