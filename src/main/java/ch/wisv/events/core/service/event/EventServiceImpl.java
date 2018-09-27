package ch.wisv.events.core.service.event;

import ch.wisv.events.core.exception.normal.EventInvalidException;
import ch.wisv.events.core.exception.normal.EventNotFoundException;
import ch.wisv.events.core.exception.normal.ProductInvalidException;
import ch.wisv.events.core.exception.normal.ProductNotFoundException;
import ch.wisv.events.core.model.document.Document;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.event.EventStatus;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.repository.EventRepository;
import ch.wisv.events.core.service.product.ProductService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * EventService implementation.
 */
@Service
public class EventServiceImpl implements EventService {

    /** EventRepository. */
    private final EventRepository eventRepository;

    /** ProductRepository. */
    private final ProductService productService;

    /** Image location. */
    @Value("${wisvch.events.image.path}")
    private String imageLocation;

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
     * Get all Events.
     *
     * @return Collection of Events
     */
    @Override
    public List<Event> getAll() {
        return eventRepository.findAll();
    }

    /**
     * Get all event between an lower and upper bound.
     *
     * @param lowerBound of type LocalDateTime
     * @param upperBound of type LocalDateTime
     *
     * @return List
     */
    @Override
    public List<Event> getAllBetween(LocalDateTime lowerBound, LocalDateTime upperBound) {
        return this.eventRepository.findAllByStartIsAfterAndStartIsBefore(lowerBound, upperBound);
    }

    /**
     * Get all upcoming Events.
     *
     * @return List
     */
    @Override
    public List<Event> getUpcoming() {
        return eventRepository.findByEndingAfter(LocalDateTime.now()).stream()
                .filter(x -> x.getPublished() == EventStatus.PUBLISHED)
                .collect(Collectors.toList());
    }

    /**
     * Get Event by key.
     *
     * @param key key of an Event
     *
     * @return Event
     */
    @Override
    public Event getByKey(String key) throws EventNotFoundException {
        Optional<Event> event = eventRepository.findByKey(key);

        return event.orElseThrow(() -> new EventNotFoundException("key " + key));
    }

    /**
     * Get all Events that are connected to the same Product.
     *
     * @param product of type Product
     *
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
     * @return List of Events
     */
    @Override
    public List<Event> getPreviousEventsLastTwoWeeks() {
        return this.eventRepository.findAllByEndingBetween(LocalDateTime.now().minusWeeks(2), LocalDateTime.now());
    }

    /**
     * Add a new Event by a EventRequest.
     *
     * @param event Event
     */
    @Override
    public void create(Event event) throws EventInvalidException {
        this.assertIsValidEvent(event);
        this.updateLinkedProducts(event, event.getProducts(), true);

        eventRepository.saveAndFlush(event);
    }

    /**
     * Update event by Event.
     *
     * @param event Event
     */
    @Override
    public void update(Event event) throws EventNotFoundException, EventInvalidException {
        Event update = this.getByKey(event.getKey());
        this.updateLinkedProducts(event, update.getProducts(), false);

        update.setTitle(event.getTitle());
        update.setShortDescription(event.getShortDescription());
        update.setDescription(event.getDescription());
        update.setLocation(event.getLocation());
        update.setStart(event.getStart());
        update.setEnding(event.getEnding());
        update.setMaxSold(event.getMaxSold());
        update.setProducts(event.getProducts());
        update.setPublished(event.getPublished());
        update.setOrganizedBy(event.getOrganizedBy());
        update.setCategories(event.getCategories());
        update.setChOnly(event.isChOnly());

        if (event.getImageUrl() != null) {
            update.setImageUrl(event.getImageUrl());
        }

        this.assertIsValidEvent(event);
        this.updateLinkedProducts(event, update.getProducts(), true);
        eventRepository.save(update);
    }

    /**
     * Delete an Event.
     *
     * @param event Event
     */
    @Override
    public void delete(Event event) {
        eventRepository.delete(event);
    }

    /**
     * Add document image to Event.
     *
     * @param event    of type Event
     * @param document of type Document
     */
    @Override
    public void addDocumentImage(Event event, Document document) {
        event.setImageUrl(this.imageLocation + document.getFileName() + ".png");
    }

    /**
     * Method assertIsValidEvent ...
     *
     * @param event of type Event
     *
     * @throws EventInvalidException when there is something missing in the event.
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

    /**
     * Update the linked status of Products.
     *
     * @param event    of type Event
     * @param products of type List
     * @param linked   of type boolean
     */
    private void updateLinkedProducts(Event event, List<Product> products, boolean linked) {
        products.forEach(p -> {
            try {
                p.setLinked(linked);
                p.setSellEnd((linked) ? event.getStart() : null);
                productService.update(p);
            } catch (ProductNotFoundException | ProductInvalidException ignored) {
            }
        });
    }
}
