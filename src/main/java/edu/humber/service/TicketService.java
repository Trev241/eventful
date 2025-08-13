package edu.humber.service;

import java.util.List;

import org.springframework.stereotype.Service;

import edu.humber.model.Event;
import edu.humber.model.Ticket;
import edu.humber.model.User;
import edu.humber.repository.EventRepository;
import edu.humber.repository.TicketRepository;
import edu.humber.repository.UserRepository;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public TicketService(TicketRepository ticketRepository, EventRepository eventRepository, UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    public Ticket purchaseTicket(Long eventId, Long buyerId, Double price) {
        eventRepository.findById(eventId).orElseThrow(() -> new RuntimeException("Event not found"));
        userRepository.findById(buyerId).orElseThrow(() -> new RuntimeException("Buyer not found"));

        Ticket ticket = Ticket.builder()
                .eventId(eventId)
                .userId(buyerId)
                .price(price)
                .build();

        return ticketRepository.save(ticket);
    }

    public List<Ticket> getTicketsByBuyer(Long buyerId) {
        userRepository.findById(buyerId).orElseThrow(() -> new RuntimeException("Buyer not found"));
        return ticketRepository.findByUserId(buyerId);
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }
}
