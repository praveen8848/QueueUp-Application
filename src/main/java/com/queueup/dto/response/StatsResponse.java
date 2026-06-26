package com.queueup.dto.response;

public class StatsResponse {
    private long activeQueues;
    private long totalServedToday;
    private double averageWaitMinutes;

    public StatsResponse() {}

    public StatsResponse(long activeQueues, long totalServedToday, double averageWaitMinutes) {
        this.activeQueues = activeQueues;
        this.totalServedToday = totalServedToday;
        this.averageWaitMinutes = averageWaitMinutes;
    }

    public long getActiveQueues() { return activeQueues; }
    public void setActiveQueues(long activeQueues) { this.activeQueues = activeQueues; }

    public long getTotalServedToday() { return totalServedToday; }
    public void setTotalServedToday(long totalServedToday) { this.totalServedToday = totalServedToday; }

    public double getAverageWaitMinutes() { return averageWaitMinutes; }
    public void setAverageWaitMinutes(double averageWaitMinutes) {
        this.averageWaitMinutes = averageWaitMinutes;
    }
}