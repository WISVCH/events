package ch.wisv.events.event.service;

import ch.wisv.events.dashboard.request.AddTicketRequest;
import ch.wisv.events.event.model.Event;
import ch.wisv.events.dashboard.request.EventRequest;

import java.util.Collection;

/**
 * Created by svenp on 11-10-2016.
 */

public interface EventService {

    Collection<Event> getAllEvents();

    Collection<Event> getUpcomingEvents();

    Collection<Event> getEventById(Long id);

    void addEvent(EventRequest eventRequest);

    void addTicketToEvent(AddTicketRequest addTicketRequest);

    Event getEventByKey(String key);

}
