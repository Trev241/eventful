package edu.humber.dto;

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
public class TicketWithEventDto {
    private Ticket ticket;
    private Event event;
}
