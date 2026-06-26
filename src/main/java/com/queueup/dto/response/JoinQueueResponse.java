package com.queueup.dto.response;

public class JoinQueueResponse {
    private Long ticketId;
    private String token;
    private int position;
    private int totalWaiting;
    private int estimatedWaitMinutes;

    public JoinQueueResponse() {}

    public JoinQueueResponse(Long ticketId, String token, int position,
                             int totalWaiting, int estimatedWaitMinutes) {
        this.ticketId = ticketId;
        this.token = token;
        this.position = position;
        this.totalWaiting = totalWaiting;
        this.estimatedWaitMinutes = estimatedWaitMinutes;
    }

    public Long getTicketId() { return ticketId; }
    public void setTicketId(Long ticketId) { this.ticketId = ticketId; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }

    public int getTotalWaiting() { return totalWaiting; }
    public void setTotalWaiting(int totalWaiting) { this.totalWaiting = totalWaiting; }

    public int getEstimatedWaitMinutes() { return estimatedWaitMinutes; }
    public void setEstimatedWaitMinutes(int estimatedWaitMinutes) {
        this.estimatedWaitMinutes = estimatedWaitMinutes;
    }
}