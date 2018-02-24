package ch.wisv.events.core.service.event;

import ch.wisv.events.core.exception.normal.EventInvalidException;
import ch.wisv.events.core.exception.normal.EventNotFoundException;
import ch.wisv.events.core.exception.normal.ProductInvalidException;
import ch.wisv.events.core.exception.normal.ProductNotFoundException;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.event.EventStatus;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.repository.EventRepository;
import ch.wisv.events.core.service.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
@Service
public class EventServiceImpl implements EventService {

    /**
     * EventRepository
     */
    private final EventRepository eventRepository;

    /**
     * ProductRepository
     */
    private final ProductService productService;

    /**
     * Constructor EventServiceImpl creates a new EventServiceImpl instance.
     *
     * @param eventRepository of type EventRepository
     * @param productService  of type ProductService
     */
    @Autowired
    public EventServiceImpl(EventRepository eventRepository, ProductService productService) {
        this.eventRepository = eventRepository;
        this.productService = productService;
    }

    /**
     * Get all Events
     *
     * @return Collection of Events
     */
    @Override
    public List<Event> getAll() {
        return eventRepository.findAll();
    }

    /**
     * Get all Events between a lowerbound and upperbound
     *
     * @param lowerbound of type LocalDateTime
     * @param upperbound of type LocalDateTime
     * @return List<Event>
     */
    @Override
    public List<Event> getAllBetween(LocalDateTime lowerbound, LocalDateTime upperbound) {
        return this.eventRepository.findAllByStartIsAfterAndStartIsBefore(lowerbound, upperbound);
    }

    /**
     * Get all upcoming Events
     *
     * @return Collection of Events
     */
    @Override
    public List<Event> getUpcoming() {
        return eventRepository.findByEndingAfter(LocalDateTime.now()).stream()
                .filter(x -> x.getPublished() == EventStatus.PUBLISHED)
                .collect(Collectors.toList());
    }

    /**
     * Get Event by key
     *
     * @param key key of an Event
     * @return Event
     */
    @Override
    public Event getByKey(String key) throws EventNotFoundException {
        Optional<Event> event = eventRepository.findByKey(key);

        return event.orElseThrow(() -> new EventNotFoundException("key " + key));
    }

    /**
     * Get all Events that are connected to the same Product
     *
     * @param product of type Product
     * @return List of Events
     */
    @Override
    public Event getByProduct(Product product) throws EventNotFoundException {
        Optional<Event> event = eventRepository.findByProductsContaining(product);

        return event.orElseThrow(() -> new EventNotFoundException("containing product #" + product.getId()));
    }

    /**
     * Method getPreviousEventsLastTwoWeeks returns the previousEventsLastTwoWeeks of this EventService object.
     *
     * @return the previousEventsLastTwoWeeks (type List<Event>) of this EventService object.
     */
    @Override
    public List<Event> getPreviousEventsLastTwoWeeks() {
        return this.eventRepository.findAllByEndingBetween(LocalDateTime.now().minusWeeks(2), LocalDateTime.now());
    }

    /**
     * Add a new Event by a EventRequest
     *
     * @param event Event
     */
    @Override
    public void create(Event event) throws EventInvalidException {
        this.assertIsValidEvent(event);
        this.updateLinkedProducts(event.getProducts(), true);

        eventRepository.saveAndFlush(event);
    }


    /**
     * Update event by Event
     *
     * @param event Event
     */
    @Override
    public void update(Event event) throws EventNotFoundException, EventInvalidException {
        Event update = this.getByKey(event.getKey());
        this.updateLinkedProducts(update.getProducts(), false);

        update.setTitle(event.getTitle());
        update.setDescription(event.getDescription());
        update.setImageURL(event.getImageURL());
        update.setLocation(event.getLocation());
        update.setStart(event.getStart());
        update.setEnding(event.getEnding());
        update.setMaxSold(event.getMaxSold());
        update.setSold(event.getSold());
        update.setProducts(event.getProducts());
        update.setPublished(event.getPublished());
        update.setOrganizedBy(event.getOrganizedBy());
        update.setShortDescription(event.getShortDescription());
        update.setCategories(event.getCategories());

        this.assertIsValidEvent(event);
        this.updateLinkedProducts(update.getProducts(), true);
        eventRepository.save(update);
    }

    /**
     * Delete an Event
     *
     * @param event Event
     */
    @Override
    public void delete(Event event) {
        eventRepository.delete(event);
    }

    /**
     * Update the linked status of Products
     *
     * @param products List of Products
     * @param linked   linked status
     */
    private void updateLinkedProducts(List<Product> products, boolean linked) {
        products.forEach(p -> {
            try {
                p.setLinked(linked);
                productService.update(p);
            } catch (ProductNotFoundException | ProductInvalidException ignored) {
            }
        });
    }

    /**
     * Method assertIsValidEvent ...
     *
     * @param event of type Event
     */
    private void assertIsValidEvent(Event event) throws EventInvalidException {
        if (event.getTitle() == null || event.getTitle().equals("")) {
            throw new EventInvalidException("Title is required, and therefore should be filled in!");
        }
        if (event.getShortDescription() == null || event.getTitle().equals("")) {
            throw new EventInvalidException("Short description is required, and therefore should be filled in!");
        }
        if (event.getDescription() == null || event.getTitle().equals("")) {
            throw new EventInvalidException("Description is required, and therefore should be filled in!");
        }
        if (event.getStart() == null) {
            throw new EventInvalidException("Starting time is required, and therefore should be filled in!");
        }
        if (event.getEnding() == null) {
            throw new EventInvalidException("Ending time is required, and therefore should be filled in!");
        }
        if (event.getStart().isAfter(event.getEnding())) {
            throw new EventInvalidException("Starting time should be before the ending time");
        }
        if (event.getTarget() == null || event.getTarget().equals(0)) {
            throw new EventInvalidException("Target is required, and therefore should be filled in!");
        }
        if (event.getMaxSold() != null && event.getTarget() > event.getMaxSold()) {
            throw new EventInvalidException("Limit should be greater or equal to the target!");
        }
        if (event.getProducts().stream().distinct().count() != event.getProducts().size()) {
            throw new EventInvalidException("It is not possible to add the same product twice or more!");
        }
    }
}
