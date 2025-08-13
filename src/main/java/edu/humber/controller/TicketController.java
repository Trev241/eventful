package edu.humber.controller;

import edu.humber.model.Ticket;
import edu.humber.service.TicketService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping
    public List<Ticket> getAllTickets() {
        return ticketService.getAllTickets();
    }

    @GetMapping("/buyer/{buyerId}")
    public List<Ticket> getTicketsByBuyer(@PathVariable Long buyerId) {
        return ticketService.getTicketsByBuyer(buyerId);
    }

    @PostMapping("/purchase/{eventId}/buyer/{buyerId}")
    public Object purchaseTicket(@PathVariable Long eventId,
                                 @PathVariable Long buyerId,
                                 @RequestParam Double price,
                                 RedirectAttributes redirectAttributes ) {
        ticketService.purchaseTicket(eventId, buyerId, price);
        redirectAttributes.addFlashAttribute("message", "Ticket booked successfully!");
        return "redirect:/events";
    }
}
