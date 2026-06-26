package com.queueup.repository;

import com.queueup.model.Queue;
import com.queueup.model.QueueStatus;
import com.queueup.model.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface QueueRepository extends JpaRepository<Queue, Long> {

    Optional<Queue> findByCode(String code);

    Optional<Queue> findByAdminToken(String adminToken);  // NEW

    List<Queue> findByStatus(QueueStatus status);

    List<Queue> findByStatusIn(List<QueueStatus> statuses);

    @Query("SELECT q FROM Queue q WHERE q.status != :status AND q.lastActivity < :cutoff")
    List<Queue> findIdleQueues(@Param("status") QueueStatus status,
                               @Param("cutoff") LocalDateTime cutoff);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.queue.id = :queueId AND t.status = :status")
    long countTicketsByStatus(@Param("queueId") Long queueId,
                              @Param("status") TicketStatus status);

    @Modifying
    @Query("UPDATE Queue q SET q.lastActivity = :now WHERE q.id = :id")
    void updateLastActivity(@Param("id") Long id, @Param("now") LocalDateTime now);
}