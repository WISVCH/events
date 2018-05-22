package ch.wisv.events.core.service.order;

import ch.wisv.events.core.exception.normal.EventsException;
import ch.wisv.events.core.exception.normal.OrderInvalidException;
import ch.wisv.events.core.exception.normal.OrderNotFoundException;
import ch.wisv.events.core.exception.normal.ProductNotFoundException;
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
     *
     * @throws OrderNotFoundException when Order is not found
     */
    Order getByReference(String reference) throws OrderNotFoundException;

    /**
     * Method create creates and order.
     *
     * @param order of type Order
     *
     * @throws EventsException when something is wrong with the Order
     */
    void create(Order order) throws EventsException;

    /**
     * Update an existing order.
     *
     * @param order of type Order
     *
     * @throws OrderNotFoundException when Order is not found
     * @throws OrderInvalidException  when the updates make the Order invalid
     */
    void update(Order order) throws OrderNotFoundException, OrderInvalidException;

    /**
     * Create an Order form a OrderProductDto.
     *
     * @param orderProductDto of type OrderProductDto
     *
     * @return Order
     *
     * @throws ProductNotFoundException when the a Product in the OrderProductDto is not found
     */
    Order createOrderByOrderProductDto(OrderProductDto orderProductDto) throws ProductNotFoundException;

    /**
     * Update the Order status.
     *
     * @param order  of type Order
     * @param status of type OrderStatus
     *
     * @throws EventsException when the status update will put the Order in an invalid state
     */
    void updateOrderStatus(Order order, OrderStatus status) throws EventsException;

    /**
     * Add a Customer to an Order.
     *
     * @param order    of type Order
     * @param customer of type Customer
     */
    void addCustomerToOrder(Order order, Customer customer) throws EventsException;
}
