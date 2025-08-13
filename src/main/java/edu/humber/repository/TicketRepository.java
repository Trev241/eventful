package edu.humber.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import edu.humber.model.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByUserId(Long userId);
    List<Ticket> findByUserIdAndEventId(Long userId, Long eventId);
    long countByEventIdAndCancelledFalse(Long eventId);
    
    // Simple database queries for user ticket statistics
    long countByUserIdAndCancelledFalse(Long userId);
    long countByUserIdAndCancelledTrue(Long userId);
    
    // Admin dashboard statistics
    long countByCancelledFalse();
    long countByCancelledTrue();
    
    @Query("SELECT SUM(t.price) FROM Ticket t WHERE t.userId = :userId AND t.cancelled = false")
    Double sumPriceByUserIdAndCancelledFalse(@Param("userId") Long userId);
    
    @Query("SELECT SUM(t.price) FROM Ticket t WHERE t.cancelled = false")
    Double sumTotalRevenue();
}
