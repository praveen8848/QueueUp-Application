package com.queueup.repository;

import com.queueup.model.Queue;
import com.queueup.model.Ticket;
import com.queueup.model.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    // Find next ticket to serve (FIFO)
    Optional<Ticket> findFirstByQueueAndStatusOrderByPositionAsc(Queue queue, TicketStatus status);

    // Get all waiting tickets ordered by position
    List<Ticket> findByQueueAndStatusOrderByPositionAsc(Queue queue, TicketStatus status);

    // Get recent served tickets for wait time calculation
    List<Ticket> findTop10ByQueueAndStatusOrderByServedAtDesc(Queue queue, TicketStatus status);

    // Count by status
    long countByQueueAndStatus(Queue queue, TicketStatus status);

    // Find ticket by queue and token
    Optional<Ticket> findByQueueAndToken(Queue queue, String token);

    // Get max position for position calculation
    @Query("SELECT COALESCE(MAX(t.position), 0) FROM Ticket t WHERE t.queue.id = :queueId AND t.status = 'WAITING'")
    Integer findMaxPositionByQueueId(@Param("queueId") Long queueId);

    // Count total tickets (for token generation)
    long countByQueueId(Long queueId);

    // Shift positions after removal
    @Modifying
    @Query("UPDATE Ticket t SET t.position = t.position - 1 " +
            "WHERE t.queue.id = :queueId AND t.status = 'WAITING' AND t.position > :removedPosition")
    void shiftPositionsAfter(@Param("queueId") Long queueId, @Param("removedPosition") int removedPosition);

}