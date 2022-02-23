package ch.wisv.events.core.service.order;

import ch.wisv.events.core.exception.normal.EventsException;
import ch.wisv.events.core.exception.normal.OrderInvalidException;
import ch.wisv.events.core.exception.normal.OrderNotFoundException;
import ch.wisv.events.core.exception.normal.ProductNotFoundException;
import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderProductDto;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.model.product.Product;

import java.util.List;

/**
 * OrderService interface.
 */
public interface OrderService {

    /**
     * Add a Customer to an Order.
     *
     * @param order    of type Order
     * @param customer of type Customer
     * @throws EventsException when Customer can not be added
     */
    void addCustomerToOrder(Order order, Customer customer) throws EventsException;

    /**
     * Method create creates and order.
     *
     * @param order of type Order
     * @throws EventsException when something is wrong with the Order
     */
    void create(Order order) throws EventsException;

    /**
     * Create an Order form a OrderProductDto.
     *
     * @param orderProductDto of type OrderProductDto
     * @return Order order
     * @throws ProductNotFoundException when the a Product in the OrderProductDto is not found
     */
    Order createOrderByOrderProductDto(OrderProductDto orderProductDto) throws ProductNotFoundException;

    /**
     * Update an existing order.
     *
     * @param order of type Order
     * @throws OrderNotFoundException when Order is not found
     * @throws OrderInvalidException  when the updates make the Order invalid
     */
    void update(Order order) throws OrderNotFoundException, OrderInvalidException;

    /**
     * Update the Order status.
     *
     * @param order  of type Order
     * @param status of type OrderStatus
     * @throws EventsException when the status update will put the Order in an invalid state
     */
    void updateOrderStatus(Order order, OrderStatus status) throws EventsException;

    /**
     * Get all order by a Customer.
     *
     * @param customer of type Customer
     * @return List of Order
     */
    List<Order> getReservationByOwner(Customer customer);

    /**
     * Get all order by a Customer.
     *
     * @param owner of type Customer
     * @return List of Order
     */
    List<Order> getAllByOwner(Customer owner);

    /**
     * Method getAllOrders returns the allOrders of this OrderService object.
     *
     * @return List of Orders
     */
    List<Order> getAllOrders();

    /**
     * Get a list of all the Reservation Orders.
     *
     * @return List of Orders
     */
    List<Order> getAllReservations();

    /**
     * Get all the paid Order.
     *
     * @return List of Orders
     */
    List<Order> getAllPaid();

    /**
     * Get all Orders by product.
     *
     * @param product of type Product
     * @return List of Orders
     */
    List<Order> getAllByProduct(Product product);

    /**
     * Method getByReference returns Order with the given Reference.
     *
     * @param reference of type String
     * @return Order by reference
     * @throws OrderNotFoundException when Order is not found
     */
    Order getByReference(String reference) throws OrderNotFoundException;

    /**
     * Get Order by ChPaymentsReference.
     *
     * @param chPaymentsReference of type String
     * @return Order
     * @throws OrderNotFoundException when Order is not found
     */
    Order getByChPaymentsReference(String chPaymentsReference) throws OrderNotFoundException;

    /**
     * Check if order contains CH only Product.
     *
     * @param order of type Order
     * @return boolean
     */
    boolean containsChOnlyProduct(Order order);

    /**
     * Check if order contains only reservable products.
     *
     * @param order of type Order
     * @return boolean
     */
    boolean containsOnlyReservable(Order order);

    /**
     * Delete an Order (use with caution!).
     *
     * @param order of type Order
     */
    void delete(Order order);

    Order saveAndFlush(Order order);

}
