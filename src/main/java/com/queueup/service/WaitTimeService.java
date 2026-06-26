package com.queueup.service;

import com.queueup.model.Queue;
import com.queueup.model.Ticket;
import com.queueup.model.TicketStatus;
import com.queueup.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class WaitTimeService {

    private final TicketRepository ticketRepository;

    public WaitTimeService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    // Get average wait time in seconds (more precise)
    public double getAverageWaitSeconds(Queue queue) {
        List<Ticket> recentServed = ticketRepository
                .findTop10ByQueueAndStatusOrderByServedAtDesc(queue, TicketStatus.SERVED);

        if (recentServed.isEmpty()) {
            return 300; // Default: 5 minutes
        }

        long totalSeconds = 0;
        int count = 0;

        for (Ticket ticket : recentServed) {
            if (ticket.getServedAt() != null && ticket.getJoinedAt() != null) {
                long seconds = ChronoUnit.SECONDS.between(
                        ticket.getJoinedAt(), ticket.getServedAt()
                );

                // Ignore outliers (> 1 hour)
                if (seconds < 3600) {
                    totalSeconds += seconds;
                    count++;
                }
            }
        }

        if (count == 0) {
            return 300; // Fallback: 5 minutes
        }

        return (double) totalSeconds / count;
    }

    // Estimate wait in minutes (for API responses)
    public int estimateWaitMinutes(Queue queue, int position) {
        double avgSeconds = getAverageWaitSeconds(queue);
        double estimatedSeconds = avgSeconds * position;
        return Math.max(1, (int) Math.ceil(estimatedSeconds / 60));
    }

    // Get precise estimated time (for countdown display)
    public int estimateWaitSeconds(Queue queue, int position) {
        double avgSeconds = getAverageWaitSeconds(queue);
        return (int) Math.ceil(avgSeconds * position);
    }
}