package ch.wisv.events.core.service.order;

import ch.wisv.events.core.exception.normal.EventsException;
import ch.wisv.events.core.exception.normal.OrderInvalidException;
import ch.wisv.events.core.exception.normal.OrderNotFoundException;
import ch.wisv.events.core.exception.normal.ProductNotFoundException;
import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderProduct;
import ch.wisv.events.core.model.order.OrderProductDto;
import ch.wisv.events.core.model.order.OrderStatus;
import static ch.wisv.events.core.model.order.OrderStatus.ANONYMOUS;
import static ch.wisv.events.core.model.order.OrderStatus.ASSIGNED;
import static ch.wisv.events.core.model.order.OrderStatus.CANCELLED;
import static ch.wisv.events.core.model.order.OrderStatus.ERROR;
import static ch.wisv.events.core.model.order.OrderStatus.EXPIRED;
import static ch.wisv.events.core.model.order.OrderStatus.PAID;
import static ch.wisv.events.core.model.order.OrderStatus.PENDING;
import static ch.wisv.events.core.model.order.OrderStatus.REJECTED;
import static ch.wisv.events.core.model.order.OrderStatus.RESERVATION;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.model.ticket.Ticket;
import ch.wisv.events.core.repository.OrderProductRepository;
import ch.wisv.events.core.repository.OrderRepository;
import ch.wisv.events.core.service.mail.MailService;
import ch.wisv.events.core.service.product.ProductService;
import ch.wisv.events.core.service.ticket.TicketService;
import com.google.common.collect.ImmutableList;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * OrderServiceImpl class.
 */
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    /** OrderRepository. */
    private final OrderRepository orderRepository;

    /** OrderProductRepository. */
    private final OrderProductRepository orderProductRepository;

    /** OrderValidationService. */
    private final OrderValidationService orderValidationService;

    /** ProductService. */
    private final ProductService productService;

    /** MailService. */
    private final MailService mailService;

    /** TicketService. */
    private final TicketService ticketService;

    /**
     * Constructor OrderServiceImpl creates a new OrderServiceImpl instance.
     *
     * @param orderRepository        of type OrderRepository
     * @param orderProductRepository of type OrderProductRepository
     * @param orderValidationService of type OrderValidationService
     * @param productService         of type ProductService
     * @param mailService            of type MailService
     * @param ticketService          of type TicketService
     */
    @Autowired
    public OrderServiceImpl(
            OrderRepository orderRepository, OrderProductRepository orderProductRepository,
            OrderValidationService orderValidationService, ProductService productService,
            MailService mailService, TicketService ticketService
    ) {
        this.orderRepository = orderRepository;
        this.orderProductRepository = orderProductRepository;
        this.orderValidationService = orderValidationService;
        this.productService = productService;
        this.mailService = mailService;
        this.ticketService = ticketService;
    }

    /**
     * Add Customer to an Order.
     *
     * @param order    of type Order
     * @param customer of type Customer
     */
    @Override
    public void addCustomerToOrder(Order order, Customer customer) throws EventsException {
        if (order.getStatus() != OrderStatus.ANONYMOUS) {
            throw new OrderInvalidException("This is not possible to add a Customer to an Order with status " + order.getStatus());
        }

        orderValidationService.assertOrderIsValidForCustomer(order, customer);
        order.setOwner(customer);

        this.update(order);
        this.updateOrderStatus(order, OrderStatus.ASSIGNED);
        log.info("Order " + order.getPublicReference() + ": Added customer " + customer.getName() + "(" + customer.getId() + ")");
    }

    /**
     * Create and save and Order.
     *
     * @param order of type Order
     */
    @Override
    public void create(Order order) throws OrderInvalidException {
        if (order.getOrderProducts() == null) {
            throw new OrderInvalidException("Order should contain a list of OrderProducts");
        }
        log.info("Order " + order.getPublicReference() + ": Order has been created!");

        order.getOrderProducts().forEach(orderProductRepository::saveAndFlush);
        order.updateOrderAmount();
        orderRepository.saveAndFlush(order);
    }

    /**
     * Create an Order form a OrderProductDto.
     *
     * @param orderProductDto of type OrderProductDto
     *
     * @return Order
     */
    @Override
    public Order createOrderByOrderProductDto(OrderProductDto orderProductDto) throws ProductNotFoundException {
        Order order = new Order();

        for (Map.Entry<String, Long> values : orderProductDto.getProducts().entrySet()) {
            if (values.getValue() > 0) {
                Product product = productService.getByKey(values.getKey());

                order.addOrderProduct(new OrderProduct(product, product.getCost(), values.getValue()));
            }
        }

        return order;
    }

    /**
     * Update an Order.
     *
     * @param order of type Order
     *
     * @throws OrderNotFoundException when Order is not found.
     * @throws OrderInvalidException  when Order is invalid to update.
     */
    @Override
    public void update(Order order) throws OrderNotFoundException, OrderInvalidException {
        if (order.getPublicReference() == null) {
            throw new OrderInvalidException("Order should contain a public reference before updating.");
        }

        Order old = this.getByReference(order.getPublicReference());

        old.setOwner(order.getOwner());
        old.setPaymentMethod(order.getPaymentMethod());
        old.setChPaymentsReference(order.getChPaymentsReference());
        old.updateOrderAmount();

        orderRepository.saveAndFlush(order);
        log.info("Order " + order.getPublicReference() + ": Order has been updated!");
    }

    /**
     * Update OrderStatus of an Order.
     *
     * @param order  of type Order
     * @param status of type OrderStatus
     */
    @Override
    public void updateOrderStatus(Order order, OrderStatus status) throws OrderInvalidException {
        OrderStatus prevStatus = order.getStatus();
        log.info("Order " + order.getPublicReference() + ": Update status from " + prevStatus + " to " + status);

        this.assertValidStatusChange(order, status);
        order.setStatus(status);

        switch (status) {
            case PAID:
                this.updateOrderStatusToPaid(order, prevStatus);
                break;
            case RESERVATION:
                this.updateOrderStatusToReservation(order);
                break;
            case REJECTED:
                this.updateOrderStatusToRejected(order, prevStatus);
                break;
            default:
                break;
        }

        orderRepository.saveAndFlush(order);
    }

    /**
     * Get all reservation by a Customer.
     *
     * @param customer of type Customer
     *
     * @return List of Order
     */
    @Override
    public List<Order> getReservationByOwner(Customer customer) {
        return orderRepository.findAllByOwnerAndStatusOrderByCreatedAt(customer, OrderStatus.RESERVATION);
    }

    /**
     * Get all order by a Customer.
     *
     * @param owner of type Customer
     *
     * @return List of Order
     */
    @Override
    public List<Order> getAllByOwner(Customer owner) {
        return orderRepository.findAllByOwnerOrderByCreatedAt(owner);
    }

    /**
     * Method getAllOrders returns the allOrders of this OrderService object.
     *
     * @return List of Orders.
     */
    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    /**
     * Get a list of all the Reservation Orders.
     *
     * @return List of Orders
     */
    @Override
    public List<Order> getAllReservations() {
        return orderRepository.findAllByStatus(OrderStatus.RESERVATION);
    }

    /**
     * Get all the paid Order.
     *
     * @return List of Orders
     */
    @Override
    public List<Order> getAllPaid() {
        return orderRepository.findAllByStatus(OrderStatus.PAID);
    }

    /**
     * Method getByReference returns Order with the given Reference.
     *
     * @param reference of type String
     *
     * @return Order
     */
    @Override
    public Order getByReference(String reference) throws OrderNotFoundException {
        return orderRepository.findOneByPublicReference(reference)
                .orElseThrow(() -> new OrderNotFoundException("reference " + reference));
    }

    /**
     * Get Order by ChPaymentsReference.
     *
     * @param chPaymentsReference of type String
     *
     * @return Order
     *
     * @throws OrderNotFoundException when Order is not found
     */
    @Override
    public Order getByChPaymentsReference(String chPaymentsReference) throws OrderNotFoundException {
        return orderRepository.findOneByChPaymentsReference(chPaymentsReference)
                .orElseThrow(() -> new OrderNotFoundException("CH Payments reference " + chPaymentsReference));
    }

    /**
     * Check if order contains CH only Product.
     *
     * @param order of type Order
     *
     * @return boolean
     */
    @Override
    public boolean containsChOnlyProduct(Order order) {
        return order.getOrderProducts().stream().anyMatch(orderProduct -> orderProduct.getProduct().isChOnly());
    }

    /**
     * Returns whether an order contains non-reservable products.
     */
    @Override
    public boolean containsOnlyReservable(Order order) {
        return order.getOrderProducts().stream().allMatch(orderProduct ->
                orderProduct.getProduct().isReservable()
        );
    }

    /**
     * Returns a list of orders based on product.
     */
    @Override
    public List<Order> getAllByProduct(Product product) {
        return orderProductRepository.findAllByProduct(product).stream()
            .map(orderRepository::findAllByOrderProducts)
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }

    /**
     * Delete an Order (use with caution!).
     *
     * @param order of type Order
     */
    @Override
    public void delete(Order order) {
        orderRepository.delete(order);
    }

    /**
     * Assert if the status change is valid.
     *
     * @param order  of type Order
     * @param status of type OrderStatus
     *
     * @throws OrderInvalidException when status change is invalid.
     */
    private void assertValidStatusChange(Order order, OrderStatus status) throws OrderInvalidException {
        HashMap<OrderStatus, List<OrderStatus>> allowedChanges = new HashMap<OrderStatus, List<OrderStatus>>() {
            {
                put(ANONYMOUS, ImmutableList.of(ASSIGNED, CANCELLED));
                put(ASSIGNED, ImmutableList.of(PENDING, CANCELLED, RESERVATION, PAID));
                put(CANCELLED, ImmutableList.of(ASSIGNED, CANCELLED));
                put(PENDING, ImmutableList.of(PAID, PENDING, ASSIGNED, ERROR, CANCELLED, EXPIRED));
                put(RESERVATION, ImmutableList.of(PAID, EXPIRED, REJECTED));
                put(PAID, ImmutableList.of(REJECTED));
                put(ERROR, ImmutableList.of());
                put(REJECTED, ImmutableList.of());
                put(EXPIRED, ImmutableList.of());
            }
        };

        if (!allowedChanges.get(order.getStatus()).contains(status)) {
            throw new OrderInvalidException("Not allowed to update status from " + order.getStatus() + " to " + status);
        }
    }

    /**
     * Update order status to PAID.
     *
     * @param order      of type Order
     * @param prevStatus of type OrderStatus
     */
    private void updateOrderStatusToPaid(Order order, OrderStatus prevStatus) {
        List<Ticket> tickets = ticketService.createByOrder(order);
        mailService.sendOrderConfirmation(order, tickets);

        order.setTicketCreated(true);
        order.setPaidAt(LocalDateTime.now());

        productService.increaseProductCount(order, false, false);
        if (prevStatus == OrderStatus.RESERVATION) {
            productService.increaseProductCount(order, true, true);
        }

        orderRepository.saveAndFlush(order);
        log.info("Order " + order.getPublicReference() + ": Status changed to PAID and tickets created!");
    }

    /**
     * Delete an order.
     *
     * @param order      of type Order
     * @param prevStatus of type OrderStatus
     */
    private void updateOrderStatusToRejected(Order order, OrderStatus prevStatus) {
        switch (prevStatus) {
            case PAID:
                productService.increaseProductCount(order, false, true);
                ticketService.deleteByOrder(order);
                break;
            case RESERVATION:
                productService.increaseProductCount(order, true, true);
                break;
            default:
        }

        orderRepository.saveAndFlush(order);
    }

    /**
     * Update order status to reservation.
     *
     * @param order of type Order
     */
    private void updateOrderStatusToReservation(Order order) {
        mailService.sendOrderReservation(order);
        productService.increaseProductCount(order, true, false);

        log.info("Order " + order.getPublicReference() + ": Status changed to RESERVATION!");
        orderRepository.saveAndFlush(order);
    }
}
