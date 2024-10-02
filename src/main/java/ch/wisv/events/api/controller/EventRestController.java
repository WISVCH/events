package ch.wisv.events.api.controller;

import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.service.event.EventService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * EventRestController class.
 */
@RestController
@RequestMapping("/api/v1/event")
public class EventRestController {
    /** EventService. */
    private final EventService eventService;

    /**
     * EventRestController.
     *
     * @param eventService of type EventService
     */
    public EventRestController(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Get published, upcoming events.
     * This is used by the website to promote them.
     * @return an array with the upcoming events.
     */
    @ResponseBody
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Object>> getUpcoming() {
        List<Object> events = new ArrayList<Object>();

        for (Event event : this.eventService.getUpcoming()) {
            events.add(this.stripEvent(event));
        }

        return new ResponseEntity<List<Object>>(events, HttpStatus.OK);
    }

    /**
     * Strip event information which should not be public.
     * @param event the event to strip the information from.
     * @return a hashmap with public information.
     */
    private HashMap<String, Object> stripEvent(Event event) {
        HashMap<String, Object> e = new HashMap<String, Object>();

        e.put("id", event.getId());
        e.put("key", event.getKey());
        e.put("title", event.getTitle());
        e.put("description", event.getDescription());
        e.put("shortDescription", event.getShortDescription());
        e.put("externalUrl", event.getExternalProductUrl());
        e.put("location", event.getLocation());
        e.put("categories", event.getCategories());
        e.put("start", event.getStart());
        e.put("end", event.getEnding());
        e.put("soldOut", event.isSoldOut());

        List<Object> products = new ArrayList<Object>();
        for(Product product : event.getProducts()) {
            products.add(this.stripProduct(product));
        }

        e.put("products", products);

        return e;
    }

    /**
     * Strip product information which should not be public.
     * @param product the product to strip the information from.
     * @return a hashmap with public information.
     */
    private HashMap<String, Object> stripProduct(Product product) {
        HashMap<String, Object> p = new HashMap<String, Object>();

        p.put("id", product.getId());
        p.put("key", product.getKey());
        p.put("title", product.getTitle());
        p.put("description", product.getDescription());
        p.put("cost", product.getCost());
        p.put("chOnly", product.isChOnly());
        p.put("soldOut", product.isSoldOut());

        return p;
    }
}
