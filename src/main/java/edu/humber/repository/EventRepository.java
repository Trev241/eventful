package edu.humber.repository;

import edu.humber.model.Event;
import edu.humber.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByHost(User host);
}
