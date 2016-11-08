package ch.wisv.events.service.event;

import ch.wisv.events.data.factory.event.EventOptionRequestFactory;
import ch.wisv.events.data.factory.event.EventRequestFactory;
import ch.wisv.events.data.model.event.Event;
import ch.wisv.events.data.model.event.EventOptions;
import ch.wisv.events.data.model.product.Product;
import ch.wisv.events.data.request.event.EventOptionsRequest;
import ch.wisv.events.data.request.event.EventProductRequest;
import ch.wisv.events.data.request.event.EventRequest;
import ch.wisv.events.exception.ProductInUseException;
import ch.wisv.events.repository.event.EventRepository;
import ch.wisv.events.repository.product.ProductRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

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
    public Collection<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    /**
     * Get all upcoming Events
     *
     * @return Collection of Events
     */
    @Override
    public Collection<Event> getUpcomingEvents() {
        return eventRepository.findByEndAfter(LocalDateTime.now());
    }

    /**
     * Get event by id
     *
     * @param id id of an Event
     * @return Event
     */
    @Override
    public Event getEventById(Long id) {
        return eventRepository.findById(id);
    }

    /**
     * Save and flush new event create by EventRequest and the EventRequestFactory.
     *
     * @param eventRequest EventRequest
     */
    @Override
    public Event addEvent(EventRequest eventRequest) {
        Event event = EventRequestFactory.create(eventRequest);

        eventRepository.saveAndFlush(event);
        return event;
    }

    /**
     * Add Product to an Event
     *
     * @param eventProductRequest EventProductRequest
     * @throws ch.wisv.events.exception.ProductInUseException when product is already added to an other Event,
     *                                                        because of the OneToMany relation
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
     * Get an Event by key
     *
     * @param key key of an Event
     * @return Event or if not found null
     */
    @Override
    public Event getEventByKey(String key) {
        Optional<Event> eventOptional = eventRepository.findByKey(key);
        if (eventOptional.isPresent()) {
            return eventOptional.get();
        }
        return null;
    }

    /**
     * Delete Product from Event
     *
     * @param eventId   eventId
     * @param productId productId
     */
    @Override
    public void deleteProductFromEvent(Long eventId, Long productId) {
        Event event = eventRepository.findOne(eventId);

        Product product = productRepository.findOne(productId);
        event.getProducts().remove(product);
        eventRepository.save(event);
    }

    /**
     * Update an Event by EventRequest
     *
     * @param eventRequest EventRequest
     */
    @Override
    public void updateEvent(EventRequest eventRequest) {
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
    public void deleteEvent(Event event) {
        eventRepository.delete(event);
    }

    /**
     * Update the EventOptions of and Event
     *
     * @param request EventOptionsRequest
     */
    @Override
    public void updateEventOptions(EventOptionsRequest request) {
        Event event = this.getEventByKey(request.getKey());
        EventOptions options = EventOptionRequestFactory.create(request);

        event.setOptions(options);

        eventRepository.save(event);
    }

    /**
     * Get Event by Product key.
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
}
