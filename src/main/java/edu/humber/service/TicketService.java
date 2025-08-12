package edu.humber.service;

import edu.humber.model.Event;
import edu.humber.model.Ticket;
import edu.humber.model.User;
import edu.humber.repository.EventRepository;
import edu.humber.repository.TicketRepository;
import edu.humber.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new RuntimeException("Event not found"));
        User buyer = userRepository.findById(buyerId).orElseThrow(() -> new RuntimeException("Buyer not found"));

        Ticket ticket = Ticket.builder()
                .event(event)
                .buyer(buyer)
                .price(price)
                .build();

        // Save ticket
        Ticket savedTicket = ticketRepository.save(ticket);

        // In a real app: Notify the host
        User host = event.getHost();
        System.out.println("[NOTIFY] " + host.getEmail() + ": " + buyer.getName() +
                " has signed up for your event: " + event.getTitle());

        return savedTicket;
    }

    public List<Ticket> getTicketsByBuyer(Long buyerId) {
        User buyer = userRepository.findById(buyerId).orElseThrow(() -> new RuntimeException("Buyer not found"));
        return ticketRepository.findByBuyer(buyer);
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }
}
