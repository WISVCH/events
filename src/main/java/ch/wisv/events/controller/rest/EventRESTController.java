package ch.wisv.events.controller.rest;

import ch.wisv.events.data.model.event.Event;
import ch.wisv.events.data.model.event.EventStatus;
import ch.wisv.events.data.model.product.Product;
import ch.wisv.events.response.EventsDefaultResponse;
import ch.wisv.events.service.EventService;
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
 * Copyright (c) 2016  W.I.S.V. 'Christiaan Huygens'
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
@RestController
@RequestMapping("/events")
public class EventRESTController {

    private final EventService eventService;

    public EventRESTController(EventService eventService) {
        this.eventService = eventService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getAllEvents(Authentication auth) {
        Collection<Event> allEvents = eventService.getAllEvents();
        Collection<Event> events = allEvents.stream()
                                            .filter(event -> event.getOptions()
                                                                  .getPublished() == EventStatus.PUBLISHED)
                                            .collect(Collectors.toCollection(ArrayList::new));
        if (null == auth) {
            Collection<EventsDefaultResponse> response = new ArrayList<>();
            events.forEach(n -> response.add(new EventsDefaultResponse(n.getKey(), n.getTitle(), n.getDescription(), n.getLocation(),
                    n.getImageURL(), n.getStart(), n.getEnd())));

            return ResponseEntityBuilder.createResponseEntity(HttpStatus.OK, "", response);
        }

        return ResponseEntityBuilder.createResponseEntity(HttpStatus.OK, "", events);
    }

    @RequestMapping(value = "/upcoming", method = RequestMethod.GET)
    public Collection<Event> getUpcomingEvents() {
        return eventService.getUpcomingEvents().stream()
                           .filter(event -> event.getOptions().getPublished() == EventStatus.PUBLISHED)
                           .collect(Collectors.toCollection(ArrayList::new));
    }

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

    @RequestMapping(value = "/{id}/tickets", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public Collection<Product> getProductByEvent(Authentication auth, @PathVariable Long id) {
        Event event = eventService.getEventById(id);

        return event.getProducts();
    }
}
