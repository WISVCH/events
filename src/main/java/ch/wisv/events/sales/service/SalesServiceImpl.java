package ch.wisv.events.sales.service;

import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.service.event.EventService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SalesServiceImpl implements SalesService {

    /**
     * EventService.
     */
    private final EventService eventService;

    /**
     * Default constructor.
     *
     * @param eventService of type EventService.
     */
    @Autowired
    public SalesServiceImpl(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Get all Event which can be sold by the current user.
     *
     * @return List of Events
     */
    @Override
    public List<Event> getAllGrantedEventByCustomer(Customer customer) {
        if (customer.getLdapGroups().contains(ch.wisv.events.utils.LdapGroup.BESTUUR)) {
            return eventService.getUpcoming();
        } else {
            return eventService.getUpcoming().stream().filter(events -> customer.getLdapGroups().contains(events.getOrganizedBy())).collect(Collectors
                                                                                                                                                    .toList());
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

        return events.stream().flatMap(event -> event.getProducts().stream()).filter(product -> LocalDateTime.now()
                .isAfter(product.getSellStart()) && LocalDateTime.now().isBefore(product.getSellEnd())).collect(Collectors.toList());
    }
}
