package ch.wisv.events.event.controller;

import ch.wisv.events.event.model.Event;
import ch.wisv.events.event.model.Product;
import ch.wisv.events.event.service.EventService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Created by svenp on 11-10-2016.
 */
@RestController
@RequestMapping(value = "/events")
public class EventRESTController {

    private EventService eventService;

    public EventRESTController(EventService eventService) {
        this.eventService = eventService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public Collection<Event> getAllEvents() {
        return this.eventService.getAllEvents();
    }

    @RequestMapping(value = "/upcoming", method = RequestMethod.GET)
    public Collection<Event> getUpcomingEvents() {
        return this.eventService.getUpcomingEvents();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Event getEventById(@PathVariable Long id) {
        return this.eventService.getEventById(id);
    }

    @RequestMapping(value = "/{id}/tickets", method = RequestMethod.GET)
    public Collection<Product> getProductByEvent(@PathVariable Long id) {
        Event event = this.eventService.getEventById(id);

        return event.getProducts();
    }
}
