package ch.wisv.events.controller.rest;

import ch.wisv.events.data.model.event.Event;
import ch.wisv.events.response.event.EventDefaultResponse;
import ch.wisv.events.response.product.ProductDefaultResponse;
import ch.wisv.events.service.event.EventService;
import ch.wisv.events.utils.ResponseEntityBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

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
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getAllEvents() {
        Collection<EventDefaultResponse> response = new ArrayList<>();
        this.eventService.getAllEvents().forEach(x -> response.add(new EventDefaultResponse(x)));

        return ResponseEntityBuilder.createResponseEntity(HttpStatus.OK, "", response);
    }

    /**
     * Get request for all upcoming Events.
     *
     * @return list of all upcoming Events.
     */
    @RequestMapping(value = "/upcoming", method = RequestMethod.GET)
    public ResponseEntity<?> getUpcomingEvents() {
        Collection<EventDefaultResponse> response = new ArrayList<>();
        this.eventService.getUpcomingEvents().forEach(x -> response.add(new EventDefaultResponse(x)));

        return ResponseEntityBuilder.createResponseEntity(HttpStatus.OK, "", response);
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
            Event event = eventService.getEventByKey(key);

            return ResponseEntityBuilder.createResponseEntity(HttpStatus.OK, "", new EventDefaultResponse(event));
        } catch (Exception e) {
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
        Collection<ProductDefaultResponse> response = new ArrayList<>();
        try {
            this.eventService.getEventByKey(key).getProducts().stream()
                             .filter(x -> x.getSellStart().isBefore(LocalDateTime.now()) &&
                                     x.getSellEnd().isAfter(LocalDateTime.now()))
                             .forEach(x -> response.add(new ProductDefaultResponse(x)));

            return ResponseEntityBuilder.createResponseEntity(HttpStatus.OK, "", response);
        } catch (Exception e) {
            return ResponseEntityBuilder.createResponseEntity(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
