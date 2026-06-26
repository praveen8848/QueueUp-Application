package com.queueup.service;

import com.queueup.model.Queue;
import com.queueup.model.Ticket;
import com.queueup.model.TicketStatus;
import com.queueup.repository.TicketRepository;
import com.queueup.util.TokenGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TokenGenerator tokenGenerator;

    public TicketService(TicketRepository ticketRepository, TokenGenerator tokenGenerator) {
        this.ticketRepository = ticketRepository;
        this.tokenGenerator = tokenGenerator;
    }

    public Ticket createTicket(Queue queue, String name) {
        // Count total tickets ever created for this queue (for token generation)
        long totalTickets = ticketRepository.countByQueueId(queue.getId());
        String token = tokenGenerator.generate(totalTickets);

        // Get the next position (end of waiting line)
        Integer maxPosition = ticketRepository.findMaxPositionByQueueId(queue.getId());
        int newPosition = (maxPosition != null ? maxPosition : 0) + 1;

        // Create and save ticket
        Ticket ticket = Ticket.create(queue, name, token, newPosition);
        return ticketRepository.save(ticket);
    }

    public void recalculatePositions(Queue queue) {
        List<Ticket> waitingTickets = ticketRepository
                .findByQueueAndStatusOrderByPositionAsc(queue, TicketStatus.WAITING);

        for (int i = 0; i < waitingTickets.size(); i++) {
            Ticket ticket = waitingTickets.get(i);
            ticket.setPosition(i + 1);
            ticketRepository.save(ticket);
        }
    }
}