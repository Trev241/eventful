package edu.humber.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import edu.humber.model.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
    
    // Admin dashboard statistics
    long countByCancelledFalse();
    long countByCancelledTrue();
    
    @Query("SELECT COUNT(e) FROM Event e WHERE e.cancelled = false AND e.eventDate > :currentDate")
    long countActiveUpcomingEvents(LocalDateTime currentDate);
    
    @Query("SELECT COUNT(e) FROM Event e WHERE e.cancelled = false AND e.eventDate <= :currentDate")
    long countPastEvents(LocalDateTime currentDate);
}
