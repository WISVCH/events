package ch.wisv.events.event.service;

import ch.wisv.events.dashboard.request.*;
import ch.wisv.events.event.model.Event;
import ch.wisv.events.event.model.EventOptions;
import ch.wisv.events.event.model.Product;
import ch.wisv.events.event.repository.EventRepository;
import ch.wisv.events.event.repository.ProductRepository;
import ch.wisv.events.exception.ProductInUseException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Created by svenp on 11-10-2016.
 */
@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    private final ProductRepository productRepository;

    public EventServiceImpl(EventRepository eventRepository, ProductRepository productRepository) {
        this.eventRepository = eventRepository;
        this.productRepository = productRepository;
    }

    @Override
    public Collection<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    public Collection<Event> getUpcomingEvents() {
        return eventRepository.findByEndAfter(LocalDateTime.now());
    }

    @Override
    public Event getEventById(Long id) {
        return eventRepository.findById(id);
    }

    @Override
    public void addEvent(EventRequest eventRequest) {
        Event event = EventRequestFactory.create(eventRequest);

        eventRepository.saveAndFlush(event);
    }

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

    @Override
    public Event getEventByKey(String key) {
        Optional<Event> eventOptional = eventRepository.findByKey(key);
        if (eventOptional.isPresent()) {
            return eventOptional.get();
        }
        return null;
    }

    @Override
    public void deleteProductFromEvent(Long eventId, Long productId) {
        Event event = eventRepository.findOne(eventId);

        Product product = productRepository.findOne(productId);
        event.getProducts().remove(product);
        eventRepository.save(event);
    }

    @Override
    public void updateEvent(EventRequest eventRequest) {
        Event event = eventRepository.findById(eventRequest.getId());
        event = EventRequestFactory.update(event, eventRequest);

        eventRepository.save(event);
    }

    @Override
    public void deleteEvent(Event event) {
        eventRepository.delete(event);
    }

    @Override
    public void updateEventOptions(EventOptionsRequest request) {
        Event event = this.getEventByKey(request.getKey());
        EventOptions options = EventOptionRequestFactory.create(request);

        event.setOptions(options);

        eventRepository.save(event);
    }

}
