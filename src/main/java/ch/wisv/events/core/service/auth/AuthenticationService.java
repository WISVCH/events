package ch.wisv.events.core.service.auth;

import ch.wisv.events.core.model.customer.Customer;

public interface AuthenticationService {

    /**
     * Get the customer that is currently logged in.
     *
     * @return Customer
     */
    Customer getCurrentCustomer();
}
