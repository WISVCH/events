package ch.wisv.events.event.service;

import ch.wisv.events.dashboard.request.AddTicketRequest;
import ch.wisv.events.dashboard.request.EventRequest;
import ch.wisv.events.event.model.Event;
import ch.wisv.events.event.model.Ticket;
import ch.wisv.events.event.repository.EventRepository;
import ch.wisv.events.event.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

/**
 * Created by svenp on 11-10-2016.
 */
@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    private final TicketRepository ticketRepository;

    public EventServiceImpl(EventRepository eventRepository, TicketRepository ticketRepository) {
        this.eventRepository = eventRepository;
        this.ticketRepository = ticketRepository;
    }


    @Override
    public Collection<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    public Collection<Event> getUpcomingEvents() {
        return eventRepository.findByEndAfter(LocalDateTime.now());
    }

    @Override
    public Collection<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    @Override
    public void addEvent(EventRequest eventRequest) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        Event event = new Event(eventRequest.getTitle());
        event.description(eventRequest.getDescription())
                .location(eventRequest.getLocation())
                .registrationLimit(eventRequest.getLimit())
                .start(LocalDateTime.parse(eventRequest.getEventStart(), format))
                .end(LocalDateTime.parse(eventRequest.getEventEnd()))
                .imageURL(eventRequest.getImage());

        eventRepository.saveAndFlush(event);
    }

    @Override
    public void addTicketToEvent(AddTicketRequest addTicketRequest) {
        Event event = eventRepository.findByKey(addTicketRequest.getEventKey());
        Ticket ticket = new Ticket();
        ticket.setTitle("Help");
        ticket.setTitle("Hello world!");
        ticket.setCost(10.0f);
        ticket.setMaxSold(100);

//        event.addTicket(ticketRepository.findByKey(addTicketRequest.getTicketKey()));

        event.addTicket(ticket);

        eventRepository.save(event);
    }

    @Override
    public Event getEventByKey(String key) {
        return eventRepository.findByKey(key);
    }

}
