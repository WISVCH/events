package ch.wisv.events.event.service;

import ch.wisv.events.event.model.Event;

import java.util.Collection;

/**
 * Created by svenp on 11-10-2016.
 */

public interface EventService {

    Collection<Event> getUpcomingEvents();

}
