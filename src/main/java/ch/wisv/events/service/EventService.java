package ch.wisv.events.service;

import ch.wisv.events.data.model.event.Event;
import ch.wisv.events.data.request.event.EventOptionsRequest;
import ch.wisv.events.data.request.event.EventProductRequest;
import ch.wisv.events.data.request.event.EventRequest;

import java.util.Collection;
import java.util.List;

/**
 * Created by svenp on 11-10-2016.
 */

public interface EventService {

    Collection<Event> getAllEvents();

    Collection<Event> getUpcomingEvents();

    Event getEventById(Long id);

    void addEvent(EventRequest eventRequest);

    void addProductToEvent(EventProductRequest eventProductRequest);

    Event getEventByKey(String key);

    void deleteProductFromEvent(Long eventId, Long productId);

    void updateEvent(EventRequest eventRequest);

    void deleteEvent(Event event);

    void updateEventOptions(EventOptionsRequest request);

    List<Event> getEventByProductKey(String key);

}
