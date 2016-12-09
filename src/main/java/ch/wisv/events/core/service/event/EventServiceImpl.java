package ch.wisv.events.core.service.event;

import ch.wisv.events.core.data.factory.event.EventOptionRequestFactory;
import ch.wisv.events.core.data.factory.event.EventRequestFactory;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.event.EventOptions;
import ch.wisv.events.core.model.event.EventStatus;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.api.request.EventOptionsRequest;
import ch.wisv.events.api.request.EventProductRequest;
import ch.wisv.events.api.request.EventRequest;
import ch.wisv.events.core.exception.EventNotFound;
import ch.wisv.events.core.exception.ProductInUseException;
import ch.wisv.events.core.repository.EventRepository;
import ch.wisv.events.core.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * EventServiceImpl.
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
    private final ProductRepository productRepository;

    /**
     * Default constructor
     *
     * @param eventRepository   EventRepository
     * @param productRepository ProductRepository
     */
    public EventServiceImpl(EventRepository eventRepository, ProductRepository productRepository) {
        this.eventRepository = eventRepository;
        this.productRepository = productRepository;
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
        return eventRepository.findByEndAfter(LocalDateTime.now()).stream().filter(x -> x.getOptions().getPublished()
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
     * @param eventRequest EventRequest
     */
    @Override
    public Event add(EventRequest eventRequest) {
        Event event = EventRequestFactory.create(eventRequest);

        eventRepository.saveAndFlush(event);
        return event;
    }

    /**
     * Add a product to an Event
     *
     * @param eventProductRequest EventProductRequest
     */
    @Override
    public void addProductToEvent(EventProductRequest eventProductRequest) {
        List<Event> eventList = eventRepository.findAllByProductsId(eventProductRequest.getProductID());
        if (eventList.size() > 0) {
            throw new ProductInUseException("This Product is already used for other Event");
        }

        Event event = eventRepository.findOne(eventProductRequest.getEventID());
        Product product = productRepository.findOne(eventProductRequest.getProductID());

        event.addProduct(product);
        eventRepository.save(event);
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
     *  @param eventId   eventId
     * @param productId productId
     */
    @Override
    public void deleteProductFromEvent(Integer eventId, Integer productId) {
        Event event = eventRepository.findOne(eventId);

        Product product = productRepository.findOne(productId);
        event.getProducts().remove(product);
        eventRepository.save(event);
    }

    /**
     * Update an Event by an EventRequest
     *
     * @param eventRequest EventRequest
     */
    @Override
    public void update(EventRequest eventRequest) {
        Event event = eventRepository.findById(eventRequest.getId());
        event = EventRequestFactory.update(event, eventRequest);

        eventRepository.save(event);
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
     * Update the EventOptions of an Event
     *
     * @param request EventOptionsRequest
     */
    @Override
    public void updateEventOptions(EventOptionsRequest request) {
        Event event = this.getByKey(request.getKey());
        EventOptions options = EventOptionRequestFactory.create(request);

        event.setOptions(options);

        eventRepository.save(event);
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
        getAllEvents().forEach(x -> x.getProducts().forEach(y -> {
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
        List<Event> events = eventRepository.findTop5ByEndBeforeOrderByEndDesc(LocalDateTime.now());
        events.forEach(x -> x.getProducts().forEach(y -> x.setSold(x.getSold() + y.getSold())));

        return events;
    }

    /**
     * Method soldFiveUpcoming returns the fiveUpcoming of this EventService object.
     *
     * @return the fiveUpcoming (type List<Event>) of this EventService object.
     */
    @Override
    public List<Event> soldFiveUpcoming() {
        List<Event> events = eventRepository.findTop5ByEndAfterOrderByEnd(LocalDateTime.now());
        events.forEach(x -> x.getProducts().forEach(y -> x.setSold(x.getSold() + y.getSold())));

        return events;
    }
}
