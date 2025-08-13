package edu.humber.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.humber.dto.EventTicketGroupDto;
import edu.humber.model.Event;
import edu.humber.model.Ticket;
import edu.humber.model.User;
import edu.humber.service.EventService;
import edu.humber.service.TicketService;
import edu.humber.service.UserService;

@Controller
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final UserService userService;
    private final EventService eventService;

    public TicketController(TicketService ticketService, UserService userService, EventService eventService) {
        this.ticketService = ticketService;
        this.userService = userService;
        this.eventService = eventService;
    }

    @GetMapping
    public String getMyTickets(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            Optional<User> currentUserOpt = userService.findByEmail(authentication.getName());
            if (currentUserOpt.isPresent()) {
                User currentUser = currentUserOpt.get();
                List<Ticket> tickets = ticketService.getTicketsByBuyer(currentUser.getId());
                
                // Group tickets by event ID
                Map<Long, List<Ticket>> ticketsByEventId = tickets.stream()
                    .collect(Collectors.groupingBy(Ticket::getEventId));
                
                // Create grouped event-ticket DTOs
                List<EventTicketGroupDto> eventGroups = ticketsByEventId.entrySet().stream()
                    .map(entry -> {
                        Long eventId = entry.getKey();
                        List<Ticket> eventTickets = entry.getValue();
                        
                        try {
                            Event event = eventService.getEventById(eventId);
                            return new EventTicketGroupDto(event, eventTickets);
                        } catch (Exception e) {
                            // If event is not found, create a placeholder
                            Event placeholderEvent = new Event();
                            placeholderEvent.setId(eventId);
                            placeholderEvent.setTitle("Event Not Found");
                            placeholderEvent.setDescription("This event may have been deleted");
                            placeholderEvent.setCancelled(true);
                            return new EventTicketGroupDto(placeholderEvent, eventTickets);
                        }
                    })
                    .sorted((g1, g2) -> g2.getLastPurchaseDate().compareTo(g1.getLastPurchaseDate())) // Sort by latest purchase
                    .toList();
                
                // Get statistics from database
                long activeTicketsCount = ticketService.getActiveTicketsCount(currentUser.getId());
                long cancelledTicketsCount = ticketService.getCancelledTicketsCount(currentUser.getId());
                double totalSpent = ticketService.getTotalSpent(currentUser.getId());
                
                model.addAttribute("eventGroups", eventGroups);
                model.addAttribute("currentUser", currentUser);
                model.addAttribute("activeTicketsCount", activeTicketsCount);
                model.addAttribute("cancelledTicketsCount", cancelledTicketsCount);
                model.addAttribute("totalSpent", totalSpent);
                return "tickets/my-tickets";
            }
        }
        return "redirect:/login";
    }

    @GetMapping("/book/{eventId}")
    public String showBookingPage(@PathVariable Long eventId, Model model, Authentication authentication) {
        try {
            // Get event details
            Event event = eventService.getEventById(eventId);
            model.addAttribute("event", event);
            
            // Check if user can book tickets
            if (authentication != null && authentication.isAuthenticated()) {
                Optional<User> currentUserOpt = userService.findByEmail(authentication.getName());
                if (currentUserOpt.isPresent()) {
                    User currentUser = currentUserOpt.get();
                    boolean canBook = ticketService.canPurchaseTicket(eventId, currentUser.getId());
                    model.addAttribute("canBook", canBook);
                    
                    // Calculate available tickets if capacity is set
                    if (event.getCapacity() > 0) {
                        long soldTickets = ticketService.countSoldTicketsForEvent(eventId);
                        long availableTickets = event.getCapacity() - soldTickets;
                        model.addAttribute("availableTickets", availableTickets);
                    }
                } else {
                    model.addAttribute("canBook", false);
                }
            } else {
                return "redirect:/login";
            }
            
            return "tickets/booking";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/events";
        }
    }

    @GetMapping("/buyer/{buyerId}")
    public List<Ticket> getTicketsByBuyer(@PathVariable Long buyerId) {
        return ticketService.getTicketsByBuyer(buyerId);
    }

    @PostMapping("/purchase/{eventId}")
    public String purchaseTicket(@PathVariable Long eventId,
                                 @RequestParam(defaultValue = "1") int quantity,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        try {
            if (authentication != null && authentication.isAuthenticated()) {
                Optional<User> currentUserOpt = userService.findByEmail(authentication.getName());
                if (currentUserOpt.isPresent()) {
                    User currentUser = currentUserOpt.get();
                    
                    // Purchase the tickets
                    List<Ticket> tickets = ticketService.purchaseTickets(eventId, currentUser.getId(), quantity);
                    
                    String message = String.format("Successfully booked %d ticket%s! Your ticket IDs: %s", 
                        tickets.size(), 
                        tickets.size() > 1 ? "s" : "",
                        tickets.stream().map(t -> t.getId().toString()).reduce((a, b) -> a + ", " + b).orElse(""));
                    
                    redirectAttributes.addFlashAttribute("message", message);
                    return "redirect:/tickets";
                } else {
                    redirectAttributes.addFlashAttribute("error", "User not found. Please log in again.");
                    return "redirect:/login";
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "Please log in to book tickets.");
                return "redirect:/login";
            }
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/tickets/book/" + eventId;
        }
    }

    @PostMapping("/cancel/{ticketId}")
    public String cancelTicket(@PathVariable Long ticketId,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        try {
            if (authentication != null && authentication.isAuthenticated()) {
                Optional<User> currentUserOpt = userService.findByEmail(authentication.getName());
                if (currentUserOpt.isPresent()) {
                    User currentUser = currentUserOpt.get();
                    ticketService.cancelTicket(ticketId, currentUser.getId());
                    redirectAttributes.addFlashAttribute("message", "Ticket cancelled successfully!");
                } else {
                    redirectAttributes.addFlashAttribute("error", "User not found.");
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "Please log in.");
                return "redirect:/login";
            }
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/tickets";
    }
}
