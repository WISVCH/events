package ch.wisv.events.event;

import ch.wisv.events.event.model.Event;
import ch.wisv.events.event.service.EventService;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;

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

    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public Collection<Event> getEventById(@PathVariable Long id) {
        return this.eventService.getEventById(id);
    }
}
