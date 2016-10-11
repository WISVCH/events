package ch.wisv.events.event;

import ch.wisv.events.event.model.Event;
import ch.wisv.events.event.service.EventService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

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

    @RequestMapping(value = "/upcoming", method = RequestMethod.GET)
    public Collection<Event> getUpcomingEvents() {
        return this.eventService.getUpcomingEvents();
    }
}
