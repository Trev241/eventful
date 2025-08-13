package edu.humber.controller;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.humber.model.Event;
import edu.humber.model.User;
import edu.humber.service.EventService;
import edu.humber.service.TicketService;
import edu.humber.service.UserService;

@Controller
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;
    private final UserService userService;
    private final TicketService ticketService;

    public EventController(EventService eventService, UserService userService, TicketService ticketService) {
        this.eventService = eventService;
        this.userService = userService;
        this.ticketService = ticketService;
    }

    @GetMapping
    public String getAllEvents(Model model, Authentication authentication) {
        model.addAttribute("events", eventService.getAllEvents());

        if (authentication != null && authentication.isAuthenticated()) {
            Optional<User> currentUserOpt = userService.findByEmail(authentication.getName());
            currentUserOpt.ifPresent(user -> model.addAttribute("currentUserId", user.getId()));
        }

        return "events/list";
    }

    @GetMapping("/{id}")
    public String getEventById(@PathVariable Long id, Model model, Authentication authentication) {
        Event event = eventService.getEventById(id);
        model.addAttribute("event", event);
        
        // Calculate capacity information
        if (event.getCapacity() > 0) {
            long soldTickets = ticketService.countSoldTicketsForEvent(id);
            long availableTickets = event.getCapacity() - soldTickets;
            model.addAttribute("soldTickets", soldTickets);
            model.addAttribute("availableTickets", availableTickets);
            model.addAttribute("hasCapacity", true);
        } else {
            model.addAttribute("hasCapacity", false);
        }
        
        // Check if user can book and if they already have tickets
        if (authentication != null && authentication.isAuthenticated()) {
            Optional<User> currentUserOpt = userService.findByEmail(authentication.getName());
            if (currentUserOpt.isPresent()) {
                User currentUser = currentUserOpt.get();
                boolean canBook = ticketService.canPurchaseTicket(id, currentUser.getId());
                model.addAttribute("canBook", canBook);
                model.addAttribute("currentUser", currentUser);
                
                // Check if user already has tickets for this event
                boolean hasTickets = !ticketService.getTicketsByBuyer(currentUser.getId()).isEmpty() &&
                    ticketService.getTicketsByBuyer(currentUser.getId()).stream()
                        .anyMatch(ticket -> ticket.getEventId().equals(id));
                model.addAttribute("userHasTickets", hasTickets);
            }
        }
        
        return "events/detail";
    }
}
