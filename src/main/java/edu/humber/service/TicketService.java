package edu.humber.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    public Ticket purchaseTicket(Long eventId, Long buyerId) {
        // Validate event exists
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        // Validate user exists
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate event is not cancelled
        if (event.isCancelled()) {
            throw new RuntimeException("Cannot book tickets for cancelled events");
        }

        // Validate event is not in the past
        if (event.getEventDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Cannot book tickets for past events");
        }

        // Check capacity if set
        if (event.getCapacity() > 0) {
            long soldTickets = countSoldTicketsForEvent(eventId);
            if (soldTickets >= event.getCapacity()) {
                throw new RuntimeException("Event is sold out");
            }
        }

        // Check if user already has tickets for this event
        List<Ticket> existingTickets = ticketRepository.findByUserIdAndEventId(buyerId, eventId);
        if (!existingTickets.isEmpty()) {
            throw new RuntimeException("You have already booked tickets for this event");
        }

        // Create and save the ticket
        Ticket ticket = Ticket.builder()
                .eventId(eventId)
                .userId(buyerId)
                .price(event.getPrice()) // Use current event price
                .purchaseDate(LocalDateTime.now())
                .cancelled(false)
                .build();

        return ticketRepository.save(ticket);
    }

    public List<Ticket> purchaseTickets(Long eventId, Long buyerId, int quantity) {
        if (quantity <= 0) {
            throw new RuntimeException("Quantity must be greater than 0");
        }

        // Validate event exists
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        // Validate user exists
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate event is not cancelled
        if (event.isCancelled()) {
            throw new RuntimeException("Cannot book tickets for cancelled events");
        }

        // Validate event is not in the past
        if (event.getEventDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Cannot book tickets for past events");
        }

        // Check capacity if set
        if (event.getCapacity() > 0) {
            long soldTickets = countSoldTicketsForEvent(eventId);
            if (soldTickets + quantity > event.getCapacity()) {
                long available = event.getCapacity() - soldTickets;
                throw new RuntimeException(String.format("Not enough tickets available. Only %d tickets remaining.", available));
            }
        }

        // Check if user already has tickets for this event
        List<Ticket> existingTickets = ticketRepository.findByUserIdAndEventId(buyerId, eventId);
        if (!existingTickets.isEmpty()) {
            throw new RuntimeException("You have already booked tickets for this event");
        }

        // Create and save multiple tickets
        List<Ticket> tickets = new ArrayList<>();
        for (int i = 0; i < quantity; i++) {
            Ticket ticket = Ticket.builder()
                    .eventId(eventId)
                    .userId(buyerId)
                    .price(event.getPrice())
                    .purchaseDate(LocalDateTime.now())
                    .cancelled(false)
                    .build();
            tickets.add(ticketRepository.save(ticket));
        }

        return tickets;
    }

    public List<Ticket> getTicketsByBuyer(Long buyerId) {
        userRepository.findById(buyerId).orElseThrow(() -> new RuntimeException("User not found"));
        return ticketRepository.findByUserId(buyerId);
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public long countSoldTicketsForEvent(Long eventId) {
        return ticketRepository.countByEventIdAndCancelledFalse(eventId);
    }

    public boolean canPurchaseTicket(Long eventId, Long userId) {
        try {
            Event event = eventRepository.findById(eventId).orElse(null);
            if (event == null || event.isCancelled() || event.getEventDate().isBefore(LocalDateTime.now())) {
                return false;
            }

            // Check capacity
            if (event.getCapacity() > 0) {
                long soldTickets = countSoldTicketsForEvent(eventId);
                if (soldTickets >= event.getCapacity()) {
                    return false;
                }
            }

            // Check if user already has a ticket
            List<Ticket> existingTickets = ticketRepository.findByUserIdAndEventId(userId, eventId);
            return existingTickets.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public void cancelTicket(Long ticketId, Long userId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        
        if (!ticket.getUserId().equals(userId)) {
            throw new RuntimeException("You can only cancel your own tickets");
        }

        if (ticket.isCancelled()) {
            throw new RuntimeException("Ticket is already cancelled");
        }

        ticket.setCancelled(true);
        ticketRepository.save(ticket);
    }

    // Simple database queries for statistics
    public long getActiveTicketsCount(Long userId) {
        return ticketRepository.countByUserIdAndCancelledFalse(userId);
    }

    public long getCancelledTicketsCount(Long userId) {
        return ticketRepository.countByUserIdAndCancelledTrue(userId);
    }

    public double getTotalSpent(Long userId) {
        Double total = ticketRepository.sumPriceByUserIdAndCancelledFalse(userId);
        return total != null ? total : 0.0;
    }
}
