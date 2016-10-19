package ch.wisv.events.event.service;

import ch.wisv.events.dashboard.request.EventProductRequest;
import ch.wisv.events.dashboard.request.EventRequest;
import ch.wisv.events.event.model.Event;

import java.util.Collection;

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
}
