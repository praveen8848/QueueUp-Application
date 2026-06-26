package com.queueup.scheduler;

import com.queueup.model.Queue;
import com.queueup.model.QueueStatus;
import com.queueup.model.Ticket;
import com.queueup.model.TicketStatus;
import com.queueup.repository.QueueRepository;
import com.queueup.repository.TicketRepository;
import com.queueup.service.QueueService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Component
public class QueueScheduler {

    private final QueueRepository queueRepository;
    private final TicketRepository ticketRepository;
    private final QueueService queueService;

    public QueueScheduler(QueueRepository queueRepository,
                          TicketRepository ticketRepository,
                          QueueService queueService) {
        this.queueRepository = queueRepository;
        this.ticketRepository = ticketRepository;
        this.queueService = queueService;
    }

    // Check for no-shows every 30 seconds
    @Scheduled(fixedRate = 30000)
    public void checkForNoShows() {
        List<Queue> activeQueues = queueRepository.findByStatusIn(
                List.of(QueueStatus.OPEN, QueueStatus.PAUSED)
        );

        for (Queue queue : activeQueues) {
            processNoShows(queue);
        }
    }

    private void processNoShows(Queue queue) {
        // Only process if queue is OPEN (not paused)
        if (queue.isPaused()) {
            return;
        }

        // Find the first person in line
        Optional<Ticket> firstInLine = ticketRepository
                .findFirstByQueueAndStatusOrderByPositionAsc(queue, TicketStatus.WAITING);

        if (firstInLine.isEmpty()) {
            return;
        }

        Ticket ticket = firstInLine.get();

        // Only check position 1 (first in line)
        if (ticket.getPosition() != 1) {
            return;
        }

        // If they haven't been called yet, call them first
        if (ticket.getCalledAt() == null) {
            ticket.markAsCalled();
            ticketRepository.save(ticket);
            return; // Give them time to respond
        }

        // Check if they've been waiting too long since being called
        LocalDateTime referenceTime = ticket.getCalledAt();
        long secondsSinceCalled = ChronoUnit.SECONDS.between(referenceTime, LocalDateTime.now());

        // If more than 2 minutes (120 seconds), mark as no-show
        if (secondsSinceCalled >= 120) {
            System.out.println("Auto-no-show: " + ticket.getToken() + " in queue " + queue.getCode());

            ticket.markAsNoShow();
            ticketRepository.save(ticket);

            // Recalculate positions for remaining waiting tickets
            recalculatePositions(queue);

            // Broadcast the update
            queueService.broadcastUpdate(queue.getCode(), null);
        }
    }

    // Check for idle queues every 5 minutes
    @Scheduled(fixedRate = 300000) // 5 minutes
    public void closeIdleQueues() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(4);

        List<Queue> idleQueues = queueRepository.findIdleQueues(
                QueueStatus.CLOSED,
                cutoff
        );

        for (Queue queue : idleQueues) {
            System.out.println("Auto-closing idle queue: " + queue.getCode());

            // Cancel all waiting tickets
            List<Ticket> waitingTickets = ticketRepository
                    .findByQueueAndStatusOrderByPositionAsc(queue, TicketStatus.WAITING);

            for (Ticket ticket : waitingTickets) {
                ticket.cancel();
                ticketRepository.save(ticket);
            }

            // Close the queue
            queue.close();
            queue.updateActivity();
            queueRepository.save(queue);

            // Broadcast final update
            queueService.broadcastUpdate(queue.getCode(), null);
        }
    }

    // Clean up old closed queues (optional - keeps database small)
    @Scheduled(cron = "0 0 3 * * ?") // Run at 3 AM daily
    public void cleanupOldQueues() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(7);
        List<Queue> oldQueues = queueRepository.findByStatus(QueueStatus.CLOSED);

        for (Queue queue : oldQueues) {
            if (queue.getLastActivity().isBefore(cutoff)) {
                // Tickets will be cascade deleted
                queueRepository.delete(queue);
                System.out.println("Cleaned up old queue: " + queue.getCode());
            }
        }
    }
    @Scheduled(fixedRate = 10000) // Every 10 seconds
    public void checkExpiredCounterCalls() {
        queueService.checkExpiredCounterCalls();
    }

    private void recalculatePositions(Queue queue) {
        List<Ticket> waitingTickets = ticketRepository
                .findByQueueAndStatusOrderByPositionAsc(queue, TicketStatus.WAITING);

        for (int i = 0; i < waitingTickets.size(); i++) {
            Ticket ticket = waitingTickets.get(i);
            ticket.setPosition(i + 1);
            ticketRepository.save(ticket);
        }
    }
}