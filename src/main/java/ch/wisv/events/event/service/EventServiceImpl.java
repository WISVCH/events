package ch.wisv.events.event.service;

import ch.wisv.events.dashboard.request.AddTicketRequest;
import ch.wisv.events.dashboard.request.EventRequest;
import ch.wisv.events.event.model.Event;
import ch.wisv.events.event.model.Ticket;
import ch.wisv.events.event.repository.EventRepository;
import ch.wisv.events.event.repository.TicketRepository;
import ch.wisv.events.exception.TicketInUseException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
        Event event = new Event(eventRequest.getTitle(),
                eventRequest.getDescription(),
                eventRequest.getLocation(),
                eventRequest.getLimit(),
                LocalDateTime.parse(eventRequest.getEventStart(), format),
                LocalDateTime.parse(eventRequest.getEventEnd()),
                eventRequest.getImage()
        );

        eventRepository.saveAndFlush(event);
    }

    @Override
    public void addTicketToEvent(AddTicketRequest addTicketRequest) {
        List<Event> eventList = eventRepository.findAllByTicketsId(addTicketRequest.getTicketID());
        if (eventList.size() > 0) {
            throw new TicketInUseException("This Ticket is already used for other Event");
        }

        Event event = eventRepository.findOne(addTicketRequest.getEventID());
        Ticket ticket = ticketRepository.findOne(addTicketRequest.getTicketID());

        event.addTicket(ticket);
        eventRepository.save(event);
    }

    @Override
    public Event getEventByKey(String key) {
        Optional<Event> eventOptional = eventRepository.findByKey(key);
        if (eventOptional.isPresent()) {
            return eventOptional.get();
        }
        return null;
    }

    @Override
    public void deleteTicketFromEvent(String eventKey, Long ticketId) {
        Optional<Event> eventOptional = eventRepository.findByKey(eventKey);
        if(eventOptional.isPresent()) {
            Event event = eventOptional.get();

            Ticket ticket = ticketRepository.findOne(ticketId);
            event.getTickets().remove(ticket);
            eventRepository.save(event);
        }
    }

}
