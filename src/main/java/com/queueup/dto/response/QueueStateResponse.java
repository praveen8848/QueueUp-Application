package com.queueup.dto.response;

import com.queueup.model.QueueStatus;
import java.util.List;

public class QueueStateResponse {
    private String queueCode;
    private QueueStatus status;
    private int totalWaiting;
    private List<TicketResponse> tickets;

    // Personal info
    private String yourToken;
    private Integer yourPosition;
    private String yourStatus; // WAITING, IT_IS_YOUR_TURN, QUEUE_PAUSED, NO_SHOW
    private Integer estimatedWait; // <-- ADD THIS (legacy field)
    private Integer estimatedWaitMinutes; // NEW: Wait in minutes
    private Integer estimatedWaitSeconds; // NEW: Precise seconds for countdown
    private Double averageWaitMinutes; // NEW: Current average wait
    private Long totalServed; // NEW: Total served in this queue
    private Long totalNoShows; // NEW: Total no-shows

    // For no-show customers
    private String rejoinCode; // NEW: Code to rejoin
    private String noShowMessage; // NEW: Message for no-shows

    public QueueStateResponse() {}

    // Getters and Setters
    public String getQueueCode() { return queueCode; }
    public void setQueueCode(String queueCode) { this.queueCode = queueCode; }

    public QueueStatus getStatus() { return status; }
    public void setStatus(QueueStatus status) { this.status = status; }

    public int getTotalWaiting() { return totalWaiting; }
    public void setTotalWaiting(int totalWaiting) { this.totalWaiting = totalWaiting; }

    public List<TicketResponse> getTickets() { return tickets; }
    public void setTickets(List<TicketResponse> tickets) { this.tickets = tickets; }

    public String getYourToken() { return yourToken; }
    public void setYourToken(String yourToken) { this.yourToken = yourToken; }

    public Integer getYourPosition() { return yourPosition; }
    public void setYourPosition(Integer yourPosition) { this.yourPosition = yourPosition; }

    public String getYourStatus() { return yourStatus; }
    public void setYourStatus(String yourStatus) { this.yourStatus = yourStatus; }

    // Legacy estimatedWait field
    public Integer getEstimatedWait() { return estimatedWait; }
    public void setEstimatedWait(Integer estimatedWait) { this.estimatedWait = estimatedWait; }

    // New fields
    public Integer getEstimatedWaitMinutes() { return estimatedWaitMinutes; }
    public void setEstimatedWaitMinutes(Integer estimatedWaitMinutes) {
        this.estimatedWaitMinutes = estimatedWaitMinutes;
    }

    public Integer getEstimatedWaitSeconds() { return estimatedWaitSeconds; }
    public void setEstimatedWaitSeconds(Integer estimatedWaitSeconds) {
        this.estimatedWaitSeconds = estimatedWaitSeconds;
    }

    public Double getAverageWaitMinutes() { return averageWaitMinutes; }
    public void setAverageWaitMinutes(Double averageWaitMinutes) {
        this.averageWaitMinutes = averageWaitMinutes;
    }

    public Long getTotalServed() { return totalServed; }
    public void setTotalServed(Long totalServed) { this.totalServed = totalServed; }

    public Long getTotalNoShows() { return totalNoShows; }
    public void setTotalNoShows(Long totalNoShows) { this.totalNoShows = totalNoShows; }

    public String getRejoinCode() { return rejoinCode; }
    public void setRejoinCode(String rejoinCode) { this.rejoinCode = rejoinCode; }

    public String getNoShowMessage() { return noShowMessage; }
    public void setNoShowMessage(String noShowMessage) { this.noShowMessage = noShowMessage; }
}