package ch.wisv.events.sales.service;

import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.product.Product;
import java.util.List;

public interface SalesService {

    /**
     * Get all Event which can be sold by the current user.
     *
     * @return List of Events
     */
    List<Event> getAllGrantedEventByCustomer(Customer customer);

    /**
     * Get all Product which can be sold by the current user.
     *
     * @return List of Products
     */
    List<Product> getAllGrantedProductByCustomer(Customer customer);
}
