package edu.humber.repository;

import edu.humber.model.Ticket;
import edu.humber.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByBuyer(User buyer);
}
