package ch.wisv.events.sales.service;

import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.service.event.EventService;
import ch.wisv.events.core.service.order.OrderService;
import ch.wisv.events.utils.LdapGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SalesServiceImpl class.
 */
@Service
public class SalesServiceImpl implements SalesService {

    /**
     * EventService.
     */
    private final EventService eventService;

    /**
     * OrderService.
     */
    private final OrderService orderService;

    /**
     * Default constructor.
     *
     * @param eventService of type EventService.
     * @param orderService of type OrderService.
     */
    @Autowired
    public SalesServiceImpl(EventService eventService, OrderService orderService) {
        this.eventService = eventService;
        this.orderService = orderService;
    }

    /**
     * Get all Event which can be sold by the current user.
     *
     * @return List of Events
     */
    @Override
    public List<Event> getAllGrantedEventByCustomer(Customer customer) {
        if (customer.getLdapGroups().contains(LdapGroup.BESTUUR) || customer.getLdapGroups().contains(LdapGroup.BEHEER)) {
            return eventService.getUpcoming();
        } else {
            return eventService.getUpcoming().stream()
                .filter(events -> customer.getLdapGroups().contains(events.getOrganizedBy()))
                .collect(Collectors.toList());
        }
    }

    /**
     * Get all Product which can be sold by the current user.
     *
     * @return List of Products
     */
    @Override
    public List<Product> getAllGrantedProductByCustomer(Customer customer) {
        List<Event> events = this.getAllGrantedEventByCustomer(customer);

        return events.stream().flatMap(event -> event.getProducts().stream())
            .filter(product -> LocalDateTime.now().isAfter(product.getSellStart()) && LocalDateTime.now().isBefore(product.getSellEnd()))
            .collect(Collectors.toList());
    }

    /**
     * Get all orders associated with the given event.
     *
     * @param event of type Event
     * @return List of Orders
     */
    @Override
    public List<Order> getAllOrdersByEvent(Event event) {
        List<Order> ordersAssociatedWithEvent = new ArrayList<>();

        event.getProducts().stream().forEach(product -> ordersAssociatedWithEvent.addAll(orderService.getAllByProduct(product)));

        return ordersAssociatedWithEvent;
    }
}
