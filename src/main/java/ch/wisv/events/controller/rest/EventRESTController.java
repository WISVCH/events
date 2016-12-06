package ch.wisv.events.controller.rest;

import ch.wisv.events.data.model.event.Event;
import ch.wisv.events.data.model.product.Search;
import ch.wisv.events.exception.EventNotFound;
import ch.wisv.events.response.event.EventDefaultResponse;
import ch.wisv.events.response.product.ProductDefaultResponse;
import ch.wisv.events.service.event.EventService;
import ch.wisv.events.utils.ResponseEntityBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

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
        return ResponseEntityBuilder.createResponseEntity(HttpStatus.OK, "",
                eventService.getAvailableEvents().stream().map(EventDefaultResponse::new));
    }

    /**
     * Get request for all upcoming Events.
     *
     * @return list of all upcoming Events.
     */
    @RequestMapping(value = "/upcoming", method = RequestMethod.GET)
    public ResponseEntity<?> getUpcomingEvents() {
        return ResponseEntityBuilder.createResponseEntity(HttpStatus.OK, "",
                eventService.getUpcomingEvents().stream().map(EventDefaultResponse::new));
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
        } catch (EventNotFound e) {
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
                    eventService.getEventByKey(key).getProducts().stream()
                                .filter(x -> x.getSellStart().isBefore(LocalDateTime.now()) && x.getSellEnd().isAfter(
                                        LocalDateTime.now())).map(ProductDefaultResponse::new));
        } catch (EventNotFound e) {
            return ResponseEntityBuilder.createResponseEntity(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /**
     * Get all unused products into search format.
     *
     * @param query query
     * @return Search Object
     */
    @GetMapping(value = "/search")
    @PreAuthorize("hasRole('ADMIN')")
    public Search getSearchEvents(@RequestParam(value = "query", required = false) String query) {
        List<Event> eventList = eventService.getAllEvents();
        Search search = new Search(query);

        String finalQuery = (query != null) ? query : "";
        eventList.stream()
                 .filter(p -> p.getTitle().toLowerCase().contains(finalQuery.toLowerCase()))
                 .forEach(x -> search.addItem(x.getTitle(), x.getId()));

        return search;
    }

}
