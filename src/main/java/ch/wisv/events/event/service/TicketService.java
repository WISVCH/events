package ch.wisv.events.event.service;

import ch.wisv.events.event.model.Ticket;

import java.util.List;

/**
 * Created by sven on 14/10/2016.
 */
public interface TicketService {

    List<Ticket> getAllTickets();

    Ticket getTicketByKey(String ticketKey);
}
