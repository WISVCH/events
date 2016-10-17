package ch.wisv.events.event.controller;

import ch.wisv.events.event.model.Ticket;
import ch.wisv.events.event.model.TicketSearch;
import ch.wisv.events.event.repository.EventRepository;
import ch.wisv.events.event.repository.TicketRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by sven on 14/10/2016.
 */
@RestController
@RequestMapping(value = "/tickets")
public class TicketRESTController {

    private final TicketRepository ticketRepository;

    private final EventRepository eventRepository;

    public TicketRESTController(TicketRepository ticketRepository, EventRepository eventRepository) {
        this.ticketRepository = ticketRepository;
        this.eventRepository = eventRepository;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public Collection<Ticket> getAllTickets() {
        return this.ticketRepository.findAll();
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping(value = "/unused/search")
    public TicketSearch getSearchTickets(@RequestParam(value = "query", required = false) String query) {
        List<Ticket> ticketList = ticketRepository.findAll();
        TicketSearch search = new TicketSearch(query);

        if (query != null) {
            List<Ticket> filterTicket = ticketList.stream()
                                                  .filter(p -> eventRepository.findAllByTicketsId(p.getId()).size() < 1)
                                                  .filter(p -> p.getTitle().toLowerCase()
                                                                .contains(query.toLowerCase()))
                                                  .collect(Collectors.toCollection(ArrayList::new));
            for (Ticket ticket : filterTicket) {
                search.addItem(ticket.getTitle(), ticket.getId());
            }
        }

        return search;
    }
}
