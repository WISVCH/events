package ch.wisv.events.event.service;

import ch.wisv.events.event.model.Event;
import ch.wisv.events.event.model.Ticket;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by svenp on 11-10-2016.
 */
@Service
public class EventServiceImpl implements EventService {
    @Override
    public Collection<Event> getUpcomingEvents() {
        Collection<Event> events = new ArrayList<>();

        Event event = new Event();
        event.addTicket(new Ticket());
        events.add(event);

        return events;
    }
}
