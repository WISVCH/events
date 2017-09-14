package ch.wisv.events.api.controller;

import ch.wisv.events.api.response.EventDefaultResponse;
import ch.wisv.events.api.response.ProductDefaultResponse;
import ch.wisv.events.core.exception.EventsModelNotFound;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.service.event.EventService;
import ch.wisv.events.utils.ResponseEntityBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * EventRESTController.
 */
@RestController
@RequestMapping("/api/v1/events")
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
     * @return ResponseEntity with all available events
     */
    @GetMapping(value = "")
    public ResponseEntity<?> getAllEvents() {
        return ResponseEntityBuilder.createResponseEntity(HttpStatus.OK, "", eventService.getAvailableEvents());
    }

    /**
     * Get request for all upcoming Events.
     *
     * @return list of all upcoming Events.
     */
    @GetMapping(value = "/upcoming")
    public ResponseEntity<?> getUpcomingEvents(Authentication auth) {
        return ResponseEntityBuilder.createResponseEntity(HttpStatus.OK, "",
                eventService.getUpcomingEvents());
    }

    /**
     * Get Event by id
     *
     * @param key key of an Event
     * @return ResponseEntityBuilder with Event Object
     */
    @RequestMapping(value = "/{key}", method = RequestMethod.GET)
    public ResponseEntity<?> getEventById(@PathVariable String key) {
        try {
            Event event = eventService.getByKey(key);

            return ResponseEntityBuilder.createResponseEntity(HttpStatus.OK, "", new EventDefaultResponse(event));
        } catch (EventsModelNotFound e) {
            return ResponseEntityBuilder.createResponseEntity(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * Get Products of a certain Event filtered by the selling starting Date and selling ending Date.
     *
     * @param key key of an Event
     * @return list of product by an Event.
     */
    @RequestMapping(value = "/{key}/products", method = RequestMethod.GET)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getProductByEvent(@PathVariable String key) {
        try {
            return ResponseEntityBuilder.createResponseEntity(HttpStatus.OK, "",
                    eventService.getByKey(key).getProducts().stream()
                            .filter(x -> x.getSellStart().isBefore(LocalDateTime.now()) && x.getSellEnd().isAfter(
                                    LocalDateTime.now())).map(ProductDefaultResponse::new));
        } catch (EventsModelNotFound e) {
            return ResponseEntityBuilder.createResponseEntity(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

}
