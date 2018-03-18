package ch.wisv.events.core.service.order;

import ch.wisv.events.core.exception.normal.EventNotFoundException;
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
import ch.wisv.events.core.model.order.OrderStatus;
import java.util.List;

public interface OrderService {

    /**
     * Method getAllOrders returns the allOrders of this OrderService object.
     *
     * @return the allOrders (type List<Order>) of this OrderService object.
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
    Order createOrderByOrderProductDTO(ch.wisv.events.core.model.order.OrderProductDto orderProductDto) throws ProductNotFoundException;

    /**
     * Update the Order status.
     *
     * @param order  of type Order
     * @param status of type OrderStatus
     */
    void updateOrderStatus(Order order, OrderStatus status) throws OrderInvalidException;

    /**
     * Method updateOrderStatusPaid
     *
     * @param order of type Order
     */
    void updateOrderStatusPaid(Order order) throws UnassignedOrderException, UndefinedPaymentMethodOrderException;

    /**
     * Temporary save an Order.
     *
     * @param order of type Order.
     */
    void tempSaveOrder(Order order);

    /**
     * Delete a temporary Order.
     *
     * @param order of type Order.
     */
    void deleteTempOrder(Order order);

    List<Order> getAllReservationOrderByCustomer(Customer customer);

    List<Order> getAllPaidOrderByCustomer(Customer customer);
}
