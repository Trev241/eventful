package edu.humber.service;

import edu.humber.repository.EventRepository;
import edu.humber.repository.TicketRepository;
import edu.humber.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AdminDashboardService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;

    public AdminDashboardService(EventRepository eventRepository, UserRepository userRepository, TicketRepository ticketRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
    }

    public long getTotalEvents() {
        return eventRepository.count();
    }

    public long getActiveEvents() {
        return eventRepository.countActiveUpcomingEvents(LocalDateTime.now());
    }

    public long getPastEvents() {
        return eventRepository.countPastEvents(LocalDateTime.now());
    }

    public long getCancelledEvents() {
        return eventRepository.countByCancelledTrue();
    }

    public long getTotalUsers() {
        return userRepository.count();
    }

    public long getAdminUsers() {
        return userRepository.countByRole("ROLE_ADMIN");
    }

    public long getRegularUsers() {
        return userRepository.countByRole("ROLE_USER");
    }

    public long getTotalTicketsSold() {
        return ticketRepository.count();
    }

    public long getActiveTickets() {
        return ticketRepository.countByCancelledFalse();
    }

    public long getCancelledTickets() {
        return ticketRepository.countByCancelledTrue();
    }

    public Double getTotalRevenue() {
        Double total = ticketRepository.sumTotalRevenue();
        return total != null ? total : 0.0;
    }
}
