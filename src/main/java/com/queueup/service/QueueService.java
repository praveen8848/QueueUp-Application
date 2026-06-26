package com.queueup.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.queueup.dto.response.JoinQueueResponse;
import com.queueup.dto.response.QueueCreatedResponse;
import com.queueup.dto.response.QueueStateResponse;
import com.queueup.dto.response.ServeNextResponse;
import com.queueup.dto.response.TicketResponse;
import com.queueup.exception.NoWaitingTicketException;
import com.queueup.exception.QueueClosedException;
import com.queueup.exception.QueueNotFoundException;
import com.queueup.model.Queue;
import com.queueup.model.QueueStatus;
import com.queueup.model.Ticket;
import com.queueup.model.TicketStatus;
import com.queueup.repository.QueueRepository;
import com.queueup.repository.TicketRepository;
import com.queueup.util.CodeGenerator;
import com.queueup.websocket.WebSocketSessionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class QueueService {

    private final QueueRepository queueRepository;
    private final TicketRepository ticketRepository;
    private final TicketService ticketService;
    private final WaitTimeService waitTimeService;
    private final WebSocketSessionManager wsManager;
    private final ObjectMapper objectMapper;

    public QueueService(QueueRepository queueRepository,
                        TicketRepository ticketRepository,
                        TicketService ticketService,
                        WaitTimeService waitTimeService,
                        WebSocketSessionManager wsManager,
                        ObjectMapper objectMapper) {
        this.queueRepository = queueRepository;
        this.ticketRepository = ticketRepository;
        this.ticketService = ticketService;
        this.waitTimeService = waitTimeService;
        this.wsManager = wsManager;
        this.objectMapper = objectMapper;
    }

    // ========== QUEUE CREATION ==========

    // Replace the createQueue method in QueueService.java
    public QueueCreatedResponse createQueue() {
        String code = CodeGenerator.generate();

        while (queueRepository.findByCode(code).isPresent()) {
            code = CodeGenerator.generate();
        }

        Queue queue = Queue.create(code);
        queue = queueRepository.save(queue);

        return new QueueCreatedResponse(
                queue.getCode(),
                queue.getAdminToken(),  // Return admin token
                "Queue created successfully"
        );
    }

    // ========== GET QUEUE ==========

    public Queue getQueueByCode(String code) {
        return queueRepository.findByCode(code)
                .orElseThrow(() -> new QueueNotFoundException(code));
    }

    public QueueStateResponse getQueueState(String code) {
        Queue queue = getQueueByCode(code);
        return buildQueueState(queue, null);
    }

    // ========== JOIN QUEUE ==========

    public JoinQueueResponse joinQueue(String code, String name) {
        Queue queue = getQueueByCode(code);

        // Check if queue can accept new entries
        if (!queue.canJoin()) {
            if (queue.isClosed()) {
                throw new QueueClosedException(code);
            } else {
                throw new IllegalStateException("Queue is not accepting new entries");
            }
        }

        // Create ticket
        Ticket ticket = ticketService.createTicket(queue, name);

        // Update queue activity
        queue.updateActivity();
        queueRepository.save(queue);

        // Broadcast update to all waiting clients
        broadcastUpdate(code, null);

        // Calculate wait time
        int totalWaiting = (int) ticketRepository.countByQueueAndStatus(queue, TicketStatus.WAITING);
        int estimatedWait = waitTimeService.estimateWaitMinutes(queue, ticket.getPosition());

        return new JoinQueueResponse(
                ticket.getId(),
                ticket.getToken(),
                ticket.getPosition(),
                totalWaiting,
                estimatedWait
        );
    }

    // ========== SERVE NEXT CUSTOMER ==========

    public ServeNextResponse serveNext(String code) {
        Queue queue = getQueueByCode(code);

        // Validate queue state
        if (queue.isClosed()) {
            throw new IllegalStateException("Cannot serve in a closed queue");
        }

        if (queue.isPaused()) {
            throw new IllegalStateException("Cannot serve while queue is paused. Resume first.");
        }

        // Find the next waiting ticket
        Ticket nextTicket = ticketRepository
                .findFirstByQueueAndStatusOrderByPositionAsc(queue, TicketStatus.WAITING)
                .orElseThrow(() -> new NoWaitingTicketException(code));

        // Mark as served
        nextTicket.markAsServed();
        ticketRepository.save(nextTicket);

        // Update queue activity
        queue.updateActivity();
        queueRepository.save(queue);

        // Recalculate positions for remaining waiting tickets
        ticketService.recalculatePositions(queue);

        // Broadcast update
        broadcastUpdate(code, null);

        return new ServeNextResponse(
                nextTicket.getToken(),
                nextTicket.getName(),
                "Served successfully"
        );
    }

    // ========== PAUSE & RESUME ==========

    public void pauseQueue(String code) {
        Queue queue = getQueueByCode(code);

        if (queue.isClosed()) {
            throw new IllegalStateException("Cannot pause a closed queue");
        }

        queue.pause();
        queue.updateActivity();
        queueRepository.save(queue);

        broadcastUpdate(code, null);
    }

    public void resumeQueue(String code) {
        Queue queue = getQueueByCode(code);

        if (queue.isClosed()) {
            throw new IllegalStateException("Cannot resume a closed queue");
        }

        queue.resume();
        queue.updateActivity();
        queueRepository.save(queue);

        broadcastUpdate(code, null);
    }

    // ========== CLOSE QUEUE ==========

    public void closeQueue(String code) {
        Queue queue = getQueueByCode(code);

        // Cancel all waiting tickets
        List<Ticket> waitingTickets = ticketRepository
                .findByQueueAndStatusOrderByPositionAsc(queue, TicketStatus.WAITING);

        for (Ticket ticket : waitingTickets) {
            ticket.cancel();
            ticketRepository.save(ticket);
        }

        queue.close();
        queue.updateActivity();
        queueRepository.save(queue);

        broadcastUpdate(code, null);
    }

    // ========== REMOVE NO-SHOW ==========

    public void removeNoShow(String code, Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        ticket.markAsNoShow();
        ticketRepository.save(ticket);

        ticketService.recalculatePositions(ticket.getQueue());

        broadcastUpdate(code, null);
    }

    // ========== GET ALL ACTIVE QUEUES ==========

    public List<Queue> getAllActiveQueues() {
        return queueRepository.findByStatusIn(
                List.of(QueueStatus.OPEN, QueueStatus.PAUSED)
        );
    }

    // ========== QUEUE STATE BUILDING ==========


    public QueueStateResponse buildQueueState(Queue queue, String requestorToken) {
        // Get all waiting tickets
        List<Ticket> waitingTickets = ticketRepository
                .findByQueueAndStatusOrderByPositionAsc(queue, TicketStatus.WAITING);

        // Build ticket response list
        List<TicketResponse> ticketResponses = waitingTickets.stream()
                .map(t -> {
                    int wait = waitTimeService.estimateWaitMinutes(queue, t.getPosition());
                    TicketResponse tr = new TicketResponse(
                            t.getToken(),
                            t.getName(),
                            t.getPosition(),
                            t.getJoinedAt(),
                            wait
                    );
                    tr.setId(t.getId());  // Set the ID AFTER creating the object
                    return tr;            // Return the object
                })
                .collect(Collectors.toList());

        // Build response
        QueueStateResponse response = new QueueStateResponse();
        response.setQueueCode(queue.getCode());
        response.setStatus(queue.getStatus());
        response.setTotalWaiting(waitingTickets.size());
        response.setTickets(ticketResponses);

        // Add metrics
        long totalServed = ticketRepository.countByQueueAndStatus(queue, TicketStatus.SERVED);
        long totalNoShows = ticketRepository.countByQueueAndStatus(queue, TicketStatus.NO_SHOW);
        response.setTotalServed(totalServed);
        response.setTotalNoShows(totalNoShows);
        response.setAverageWaitMinutes(waitTimeService.getAverageWaitSeconds(queue) / 60.0);

        // If requestor's token provided, add personal info
        if (requestorToken != null) {
            // First check if this token was marked as SERVED
            Optional<Ticket> servedTicket = ticketRepository.findByQueueAndToken(queue, requestorToken);

            if (servedTicket.isPresent() && servedTicket.get().getStatus() == TicketStatus.SERVED) {
                // Customer was served
                response.setYourToken(requestorToken);
                response.setYourStatus("SERVED");
                response.setYourPosition(null);
                response.setEstimatedWaitMinutes(null);
                response.setEstimatedWaitSeconds(null);
                response.setNoShowMessage("Thank you! You have been served. ✅");
            } else if (servedTicket.isPresent() && servedTicket.get().getStatus() == TicketStatus.NO_SHOW) {
                // Customer was no-show
                response.setYourToken(requestorToken);
                response.setYourStatus("NO_SHOW");
                response.setRejoinCode(queue.getCode());
                response.setNoShowMessage("You missed your turn! Please rejoin the queue.");
            } else {
                // Normal waiting customer
                waitingTickets.stream()
                        .filter(t -> t.getToken().equals(requestorToken))
                        .findFirst()
                        .ifPresent(t -> {
                            response.setYourPosition(t.getPosition());
                            response.setYourToken(t.getToken());

                            int waitMins = waitTimeService.estimateWaitMinutes(queue, t.getPosition());
                            int waitSecs = waitTimeService.estimateWaitSeconds(queue, t.getPosition());
                            response.setEstimatedWaitMinutes(waitMins);
                            response.setEstimatedWaitSeconds(waitSecs);
                            response.setEstimatedWait(waitMins);

                            // Check counter call
                            if (t.getPosition() == 1 && t.getCounterDeadline() != null && !t.isCounterCallExpired()) {
                                response.setYourStatus("CALLED_TO_COUNTER");
                                response.setEstimatedWaitSeconds((int) t.getCounterSecondsRemaining());
                                response.setEstimatedWaitMinutes((int) Math.ceil(t.getCounterSecondsRemaining() / 60.0));
                            } else if (t.getPosition() == 1 && queue.isOpen()) {
                                response.setYourStatus("IT_IS_YOUR_TURN");
                                t.markAsCalled();
                                ticketRepository.save(t);
                            } else if (queue.isPaused()) {
                                response.setYourStatus("QUEUE_PAUSED");
                            } else {
                                response.setYourStatus("WAITING");
                            }
                        });
            }
        }

        return response;
    }

    // ========== WEBSOCKET BROADCAST ==========

    public void broadcastUpdate(String queueCode, String targetToken) {
        try {
            Queue queue = getQueueByCode(queueCode);
            QueueStateResponse state = buildQueueState(queue, targetToken);
            String message = objectMapper.writeValueAsString(state);
            wsManager.broadcast(queueCode, message);
        } catch (Exception e) {
            // Log error but don't fail the operation
            System.err.println("Failed to broadcast update: " + e.getMessage());
        }
    }

    public Queue getQueueByAdminToken(String adminToken) {
        return queueRepository.findByAdminToken(adminToken)
                .orElseThrow(() -> new QueueNotFoundException("Invalid admin token: " + adminToken));
    }

    public Map<String, Object> getQueueMetrics(String code) {
        Queue queue = getQueueByCode(code);

        // Count served tickets
        long totalServed = ticketRepository.countByQueueAndStatus(queue, TicketStatus.SERVED);

        // Count no-shows
        long totalNoShows = ticketRepository.countByQueueAndStatus(queue, TicketStatus.NO_SHOW);

        // Count currently waiting
        long currentlyWaiting = ticketRepository.countByQueueAndStatus(queue, TicketStatus.WAITING);

        // Calculate average wait time from recent served tickets
        List<Ticket> recentServed = ticketRepository
                .findTop10ByQueueAndStatusOrderByServedAtDesc(queue, TicketStatus.SERVED);

        double avgWaitMinutes = 0;
        int servedCount = 0;

        for (Ticket ticket : recentServed) {
            if (ticket.getServedAt() != null && ticket.getJoinedAt() != null) {
                long seconds = ChronoUnit.SECONDS.between(ticket.getJoinedAt(), ticket.getServedAt());
                if (seconds < 3600) { // Ignore outliers > 1 hour
                    avgWaitMinutes += seconds / 60.0;
                    servedCount++;
                }
            }
        }

        if (servedCount > 0) {
            avgWaitMinutes = avgWaitMinutes / servedCount;
        }

        // Round to 1 decimal
        avgWaitMinutes = Math.round(avgWaitMinutes * 10.0) / 10.0;

        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalServed", totalServed);
        metrics.put("totalNoShows", totalNoShows);
        metrics.put("currentlyWaiting", currentlyWaiting);
        metrics.put("averageWaitMinutes", avgWaitMinutes);
        metrics.put("queueStatus", queue.getStatus());
        metrics.put("queueCode", queue.getCode());
        metrics.put("createdAt", queue.getCreatedAt());

        return metrics;
    }

    public ServeNextResponse callNextToCounter(String code, int waitMinutes) {
        Queue queue = getQueueByCode(code);

        if (queue.isClosed()) {
            throw new IllegalStateException("Cannot call in a closed queue");
        }

        if (queue.isPaused()) {
            throw new IllegalStateException("Cannot call while queue is paused");
        }

        // Find the next waiting ticket
        Ticket nextTicket = ticketRepository
                .findFirstByQueueAndStatusOrderByPositionAsc(queue, TicketStatus.WAITING)
                .orElseThrow(() -> new NoWaitingTicketException(code));

        // Mark as called to counter with deadline
        nextTicket.markAsCalledToCounter(waitMinutes);
        nextTicket.markAsCalled();
        ticketRepository.save(nextTicket);

        // Update queue activity
        queue.updateActivity();
        queueRepository.save(queue);

        // Broadcast update
        broadcastUpdate(code, null);

        return new ServeNextResponse(
                nextTicket.getToken(),
                nextTicket.getName(),
                "Customer called to counter. " + waitMinutes + " minute(s) to arrive."
        );
    }

    // Add this method to check and handle expired counter calls
    public void checkExpiredCounterCalls() {
        List<Queue> activeQueues = queueRepository.findByStatusIn(
                List.of(QueueStatus.OPEN)
        );

        for (Queue queue : activeQueues) {
            Optional<Ticket> firstInLine = ticketRepository
                    .findFirstByQueueAndStatusOrderByPositionAsc(queue, TicketStatus.WAITING);

            if (firstInLine.isPresent()) {
                Ticket ticket = firstInLine.get();

                // Check if they were called to counter and deadline passed
                if (ticket.getCounterDeadline() != null && ticket.isCounterCallExpired()) {
                    // Mark as no-show
                    ticket.markAsNoShow();
                    ticketRepository.save(ticket);

                    // Recalculate positions
                    ticketService.recalculatePositions(queue);

                    // Broadcast update
                    broadcastUpdate(queue.getCode(), null);

                    System.out.println("Auto-no-show for expired counter call: " + ticket.getToken());
                }
            }
        }
    }
    // Add this method to QueueService.java

    public void markTicketAsNoShow(String code, Long ticketId) {
        Queue queue = getQueueByCode(code);

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        // Verify ticket belongs to this queue
        if (!ticket.getQueue().getId().equals(queue.getId())) {
            throw new RuntimeException("Ticket does not belong to this queue");
        }

        ticket.markAsNoShow();
        ticketRepository.save(ticket);

        // Recalculate positions
        ticketService.recalculatePositions(queue);

        // Update queue activity
        queue.updateActivity();
        queueRepository.save(queue);

        // Broadcast update
        broadcastUpdate(code, null);
    }
}