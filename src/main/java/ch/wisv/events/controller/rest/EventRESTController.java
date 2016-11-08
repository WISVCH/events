package ch.wisv.events.controller.rest;

import ch.wisv.events.data.model.event.Event;
import ch.wisv.events.data.model.event.EventStatus;
import ch.wisv.events.data.model.product.Product;
import ch.wisv.events.response.event.EventsDefaultResponse;
import ch.wisv.events.service.event.EventService;
import ch.wisv.events.utils.ResponseEntityBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * EventRESTController.
 */
@RestController
@RequestMapping("/events")
public class EventRESTController {

    /**
     * EventService.
     */
    private final EventService eventService;

    /**
     * Default constructor.
     *
     * @param eventService EventService.
     */
    public EventRESTController(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Get all Events depending on the on the auth
     *
     * @param auth Authentication
     * @return ResponseEntity with all available events
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getAllEvents(Authentication auth) {
        Collection<Event> allEvents = eventService.getAllEvents();
        Collection<Event> events = allEvents.stream()
                                            .filter(event -> event.getOptions()
                                                                  .getPublished() == EventStatus.PUBLISHED)
                                            .collect(Collectors.toCollection(ArrayList::new));
        if (null == auth) {
            Collection<EventsDefaultResponse> response = new ArrayList<>();
            events.forEach(n -> response
                    .add(new EventsDefaultResponse(n.getKey(), n.getTitle(), n.getDescription(), n.getLocation(),
                            n.getImageURL(), n.getStart(), n.getEnd())));

            return ResponseEntityBuilder.createResponseEntity(HttpStatus.OK, "", response);
        }

        return ResponseEntityBuilder.createResponseEntity(HttpStatus.OK, "", events);
    }

    /**
     * Get request for all upcoming Events.
     *
     * @return list of all upcoming Events.
     */
    @RequestMapping(value = "/upcoming", method = RequestMethod.GET)
    public Collection<Event> getUpcomingEvents() {
        return eventService.getUpcomingEvents().stream()
                           .filter(event -> event.getOptions().getPublished() == EventStatus.PUBLISHED)
                           .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Get Event by id
     *
     * @param auth Authentication.
     * @param id   id of an Event
     * @return ResponseEntityBuilder with Event Objct
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getEventById(Authentication auth, @PathVariable Long id) {
        Event event = eventService.getEventById(id);
        if (auth == null) {
            EventsDefaultResponse response = new EventsDefaultResponse(event.getKey(), event.getTitle(),
                    event.getDescription(), event.getLocation(), event.getImageURL(), event.getStart(), event.getEnd());

            return ResponseEntityBuilder.createResponseEntity(HttpStatus.OK, "", response);
        }
        return ResponseEntityBuilder.createResponseEntity(HttpStatus.OK, "", event);
    }

    /**
     * Get Products of a certain Event.
     *
     * @param auth Authentication.
     * @param id   id of an Event
     * @return list of product by an Event.
     */
    @RequestMapping(value = "/{id}/products", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public Collection<Product> getProductByEvent(Authentication auth, @PathVariable Long id) {
        Event event = eventService.getEventById(id);

        return event.getProducts();
    }
}
