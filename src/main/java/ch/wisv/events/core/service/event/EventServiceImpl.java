package ch.wisv.events.core.service.event;

import ch.wisv.events.core.exception.EventNotFound;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.event.EventStatus;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.repository.EventRepository;
import ch.wisv.events.core.service.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    /**
     * Get all upcoming Events
     *
     * @return Collection of Events
     */
    @Override
    public List<Event> getUpcomingEvents() {
        return eventRepository.findByEndingAfter(LocalDateTime.now()).stream().filter(x -> x.getOptions().getPublished()
                == EventStatus.PUBLISHED).collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Get all available events
     *
     * @return Collection of Events
     */
    @Override
    public List<Event> getAvailableEvents() {
        return eventRepository.findAll().stream().filter(x -> x.getOptions().getPublished() == EventStatus.PUBLISHED)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Add a new Event by a EventRequest
     *
     * @param event Event
     */
    @Override
    public void create(Event event) {
        this.updateLinkedProducts(event.getProducts(), true);
        
        eventRepository.saveAndFlush(event);
    }

    /**
     * Get Event by key
     *
     * @param key key of an Event
     * @return Event
     */
    @Override
    public Event getByKey(String key) {
        Optional<Event> eventOptional = eventRepository.findByKey(key);
        if (eventOptional.isPresent()) {
            return eventOptional.get();
        }
        throw new EventNotFound("Event with key " + key + " not found.");
    }

    /**
     * Delete a product from an Event
     *
     * @param eventId   eventId
     * @param productId productId
     */
    @Override
    public void deleteProductFromEvent(Integer eventId, Integer productId) {
        Event event = eventRepository.findOne(eventId);

        Product product = productService.getByID(productId);
        event.getProducts().remove(product);
        eventRepository.save(event);
    }

    /**
     * Update event by Event
     *
     * @param event Event
     */
    @Override
    public void update(Event event) {
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
        update.setOptions(event.getOptions());
        update.setProducts(event.getProducts());
        update.setShortDescription(event.getShortDescription());

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
     * Get all Events that are connected to the same Product
     *
     * @param key key of an Product
     * @return List of Events
     */
    @Override
    public List<Event> getEventByProductKey(String key) {
        List<Event> events = new ArrayList<>();
        this.getAllEvents().forEach(x -> x.getProducts().forEach(y -> {
            if (Objects.equals(y.getKey(), key)) {
                events.add(x);
            }
        }));

        return events;
    }

    /**
     * Method soldFivePrevious returns the fivePrevious of this EventService object.
     *
     * @return the fivePrevious (type List<Event>) of this EventService object.
     */
    @Override
    public List<Event> soldFivePrevious() {
        return eventRepository.findTop5ByEndingBeforeOrderByEndingDesc(LocalDateTime.now());
    }

    /**
     * Method soldFiveUpcoming returns the fiveUpcoming of this EventService object.
     *
     * @return the fiveUpcoming (type List<Event>) of this EventService object.
     */
    @Override
    public List<Event> soldFiveUpcoming() {
        return eventRepository.findTop5ByEndingAfterOrderByEnding(LocalDateTime.now());
    }

    /**
     * Update the linked status of Products
     *
     * @param products List of Products
     * @param linked   linked status
     */
    private void updateLinkedProducts(List<Product> products, boolean linked) {
        products.forEach(p -> {
            p.setLinked(linked);
            productService.update(p);
        });
    }

}
