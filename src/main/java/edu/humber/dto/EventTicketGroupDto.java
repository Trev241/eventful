package edu.humber.dto;

import java.time.LocalDateTime;
import java.util.List;

import edu.humber.model.Event;
import edu.humber.model.Ticket;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventTicketGroupDto {
    private Event event;
    private List<Ticket> tickets;
    private int totalQuantity;
    private double totalPrice;
    private int activeQuantity;
    private int cancelledQuantity;
    private LocalDateTime firstPurchaseDate;
    private LocalDateTime lastPurchaseDate;
    
    public EventTicketGroupDto(Event event, List<Ticket> tickets) {
        this.event = event;
        this.tickets = tickets;
        this.totalQuantity = tickets.size();
        this.totalPrice = tickets.stream()
            .filter(t -> t.getPrice() != null)
            .mapToDouble(Ticket::getPrice)
            .sum();
        this.activeQuantity = (int) tickets.stream().filter(t -> !t.isCancelled()).count();
        this.cancelledQuantity = (int) tickets.stream().filter(Ticket::isCancelled).count();
        this.firstPurchaseDate = tickets.stream()
            .map(Ticket::getPurchaseDate)
            .filter(date -> date != null)
            .min(LocalDateTime::compareTo)
            .orElse(LocalDateTime.now());
        this.lastPurchaseDate = tickets.stream()
            .map(Ticket::getPurchaseDate)
            .filter(date -> date != null)
            .max(LocalDateTime::compareTo)
            .orElse(LocalDateTime.now());
    }
}
