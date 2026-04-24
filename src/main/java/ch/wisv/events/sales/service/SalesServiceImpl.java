package ch.wisv.events.sales.service;

import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.event.EventStatus;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.repository.EventRepository;
import ch.wisv.events.core.service.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SalesServiceImpl class.
 */
@Service
public class SalesServiceImpl implements SalesService {

    /** EventRepository. */
    private final EventRepository eventRepository;

    /**
     * OrderService.
     */
    private final OrderService orderService;

    /** Event statuses visible in sales views. */
    private static final List<EventStatus> SALES_VISIBLE_STATUSES = List.of(
            EventStatus.PUBLISHED,
            EventStatus.NOT_PUBLISHED
    );

    /**
     * Default constructor.
     *
     * @param eventRepository of type EventRepository.
     * @param orderService of type OrderService.
     */
    @Autowired
    public SalesServiceImpl(EventRepository eventRepository, OrderService orderService) {
        this.eventRepository = eventRepository;
        this.orderService = orderService;
    }

    /**
     * Get all Event which can be sold by the current user.
     *
     * @return List of Events
     */
    @Override
    public List<Event> getAllGrantedEventByCustomer(Customer customer) {
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        if (this.currentUserHasAdminRole()) {
            return eventRepository.findAllSalesVisibleEvents(startOfToday, SALES_VISIBLE_STATUSES);
        }

        if (customer == null || customer.getLdapGroups() == null || customer.getLdapGroups().isEmpty()) {
            return Collections.emptyList();
        }

        return eventRepository.findAllSalesVisibleEventsByOrganizedByIn(
                startOfToday,
                SALES_VISIBLE_STATUSES,
                customer.getLdapGroups()
        );
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

    /**
     * Check if current authenticated user has admin role.
     *
     * @return true if current user has ROLE_ADMIN, false otherwise
     */
    private boolean currentUserHasAdminRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
    }
}
