package ch.wisv.events.core.service.order;

import ch.wisv.events.core.exception.normal.OrderExceedCustomerLimitException;
import ch.wisv.events.core.exception.normal.OrderExceedEventLimitException;
import ch.wisv.events.core.exception.normal.OrderExceedProductLimitException;
import ch.wisv.events.core.exception.normal.OrderInvalidException;
import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.order.Order;

/**
 * OrderValidationService interface.
 */
public interface OrderValidationService {

    /**
     * Assert if an Order is valid.
     *
     * @param order of type Order when the Order is invalid
     *
     * @throws OrderInvalidException            when Order is invalid
     * @throws OrderExceedEventLimitException   when Event limit will be exceeded
     * @throws OrderExceedProductLimitException when Product limit will be exceeded
     */
    void assertOrderIsValid(Order order) throws OrderInvalidException, OrderExceedEventLimitException, OrderExceedProductLimitException;

    /**
     * Assert if an Order is valid of a given Customer.
     *
     * @param order    of type Order
     * @param customer of type Customer
     *
     * @throws OrderInvalidException             when the Order is invalid for the Customer
     * @throws OrderExceedCustomerLimitException when Customer limit will be exceeded
     */
    void assertOrderIsValidForCustomer(Order order, Customer customer) throws OrderInvalidException, OrderExceedCustomerLimitException;

    /**
     * Assert if an Order is valid to go to the payment process.
     *
     * @param order of type Order
     *
     * @throws OrderInvalidException when order is invalid.
     */
    void assertOrderIsValidForIdealPayment(Order order) throws OrderInvalidException;
}
