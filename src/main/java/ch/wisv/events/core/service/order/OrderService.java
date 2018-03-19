package ch.wisv.events.core.service.order;

import ch.wisv.events.core.exception.normal.EventNotFoundException;
import ch.wisv.events.core.exception.normal.EventsException;
import ch.wisv.events.core.exception.normal.OrderExceedCustomerLimitException;
import ch.wisv.events.core.exception.normal.OrderExceedEventLimitException;
import ch.wisv.events.core.exception.normal.OrderExceedProductLimitException;
import ch.wisv.events.core.exception.normal.OrderInvalidException;
import ch.wisv.events.core.exception.normal.OrderNotFoundException;
import ch.wisv.events.core.exception.normal.ProductNotFoundException;
import ch.wisv.events.core.exception.normal.UnassignedOrderException;
import ch.wisv.events.core.exception.normal.UndefinedPaymentMethodOrderException;
import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderProductDto;
import ch.wisv.events.core.model.order.OrderStatus;
import java.util.List;

/**
 * OrderService interface.
 */
public interface OrderService {

    /**
     * Method getAllOrders returns the allOrders of this OrderService object.
     *
     * @return List of Orders
     */
    List<Order> getAllOrders();

    /**
     * Method getByReference returns Order with the given Reference.
     *
     * @param reference of type String
     *
     * @return Order
     */
    Order getByReference(String reference) throws OrderNotFoundException;

    /**
     * Method create creates and order.
     *
     * @param order of type Order
     */
    void create(Order order) throws OrderInvalidException, EventNotFoundException, OrderExceedEventLimitException, OrderExceedProductLimitException,
                                    OrderExceedCustomerLimitException;

    /**
     * Update an existing order.
     *
     * @param order of type Order
     */
    void update(Order order) throws OrderNotFoundException, OrderInvalidException;

    /**
     * Create an Order form a OrderProductDto.
     *
     * @param orderProductDto of type OrderProductDto
     *
     * @return Order
     */
    Order createOrderByOrderProductDto(OrderProductDto orderProductDto) throws ProductNotFoundException;

    /**
     * Update the Order status.
     *
     * @param order  of type Order
     * @param status of type OrderStatus
     */
    void updateOrderStatus(Order order, OrderStatus status) throws OrderInvalidException;

    /**
     * Update and the order status to paid.
     *
     * @param order of type Order
     */
    void updateOrderStatusPaid(Order order) throws EventsException;

    /**
     * Get all reservation orders by a Customer.
     *
     * @param customer of type Customer.
     *
     * @return List of Orders
     */
    List<Order> getAllReservationOrderByCustomer(Customer customer);
}
