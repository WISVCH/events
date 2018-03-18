package ch.wisv.events.core.service.order;

import ch.wisv.events.core.exception.normal.OrderInvalidException;
import ch.wisv.events.core.exception.normal.OrderNotFoundException;
import ch.wisv.events.core.exception.normal.ProductInvalidException;
import ch.wisv.events.core.exception.normal.ProductNotFoundException;
import ch.wisv.events.core.exception.normal.UnassignedOrderException;
import ch.wisv.events.core.exception.normal.UndefinedPaymentMethodOrderException;
import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderProduct;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.model.ticket.Ticket;
import ch.wisv.events.core.repository.OrderProductRepository;
import ch.wisv.events.core.repository.OrderRepository;
import ch.wisv.events.core.service.mail.MailService;
import ch.wisv.events.core.service.product.ProductService;
import ch.wisv.events.core.service.ticket.TicketService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final OrderProductRepository orderProductRepository;

    private final ProductService productService;

    private final MailService mailService;

    private final TicketService ticketService;

    /**
     * Constructor OrderServiceImpl creates a new OrderServiceImpl instance.
     *
     * @param orderRepository        of type OrderRepository
     * @param orderProductRepository of type OrderProductRepository
     * @param productService         of type ProductService
     * @param mailService            of type MailService
     * @param ticketService          of type TicketService
     */
    @Autowired
    public OrderServiceImpl(
            OrderRepository orderRepository,
            OrderProductRepository orderProductRepository,
            ProductService productService,
            MailService mailService,
            TicketService ticketService
    ) {
        this.orderRepository = orderRepository;
        this.orderProductRepository = orderProductRepository;
        this.productService = productService;
        this.mailService = mailService;
        this.ticketService = ticketService;
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
     * Method getByReference returns Order with the given Reference.
     *
     * @param reference of type String
     *
     * @return Order
     */
    @Override
    public Order getByReference(String reference) throws OrderNotFoundException {
        return orderRepository.findOneByPublicReference(reference).orElseThrow(() -> new OrderNotFoundException("reference " + reference));
    }

    /**
     * Create an Order form a OrderProductDto.
     *
     * @param orderProductDto of type OrderProductDto
     *
     * @return Order
     */
    @Override
    public Order createOrderByOrderProductDto(ch.wisv.events.core.model.order.OrderProductDto orderProductDto) throws ProductNotFoundException {
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
     * Create and save and Order.
     *
     * @param order of type Order
     */
    @Override
    public void create(Order order) {
        order.getOrderProducts().forEach(orderProductRepository::saveAndFlush);
        order.updateOrderAmount();

        orderRepository.saveAndFlush(order);
    }

    @Override
    public void update(Order order) throws OrderNotFoundException, OrderInvalidException {
        if (order.getPublicReference() == null) {
            throw new OrderInvalidException("Order should contain a public reference");
        }

        Order old = this.getByReference(order.getPublicReference());
        old.setOwner(order.getOwner());
        old.setStatus(order.getStatus());
        old.setPaymentMethod(order.getPaymentMethod());

        orderRepository.saveAndFlush(order);
    }

    /**
     * Update OrderStatus of an Order.
     *
     * @param order  of type Order
     * @param status of type OrderStatus
     */
    @Override
    public void updateOrderStatus(Order order, OrderStatus status) throws OrderInvalidException {
        if (order.getId() == null) {
            throw new OrderInvalidException("Order should contain an ID before changing its status");
        }

        if (status == OrderStatus.PAID) {
            try {
                this.updateOrderStatusPaid(order);
            } catch (UnassignedOrderException | UndefinedPaymentMethodOrderException ignored) {
            }
        } else if (status == OrderStatus.RESERVATION) {
            this.updateOrderStatusReservation(order);
        } else {
            order.setStatus(status);
            orderRepository.saveAndFlush(order);
        }
    }

    private void updateOrderStatusReservation(Order order) {
        OrderStatus beforeStatus = order.getStatus();
        order.setStatus(OrderStatus.RESERVATION);

        if (beforeStatus != OrderStatus.RESERVATION) {
            mailService.sendOrderReservation(order);
            this.updateProductReservedCount(order);
        }

        orderRepository.saveAndFlush(order);
    }

    private void updateProductReservedCount(Order order) {
        order.getOrderProducts().forEach(orderProduct -> {
            orderProduct.getProduct().increaseReserved(orderProduct.getAmount().intValue());
            try {
                productService.update(orderProduct.getProduct());
            } catch (ProductNotFoundException | ProductInvalidException ignored) {
            }
        });
    }

    /**
     * Update order status to PAID.
     *
     * @param order of type Order
     */
    @Override
    public void updateOrderStatusPaid(Order order) throws UnassignedOrderException, UndefinedPaymentMethodOrderException {
        OrderStatus beforeStatus = order.getStatus();
        order.setStatus(OrderStatus.PAID);
        order.setPaidAt(LocalDateTime.now());

        List<Ticket> tickets = this.createTicketIfPaid(order);
        if (beforeStatus != OrderStatus.PAID) {
            mailService.sendOrderConfirmation(order, tickets);
            this.updateProductSoldCount(order);
        }

        orderRepository.saveAndFlush(order);
    }

    /**
     * Temporary save an Order.
     *
     * @param order of type Order.
     */
    @Override
    public void tempSaveOrder(Order order) {
        if (order.getStatus().equals(OrderStatus.RESERVATION)) {
            order.getOrderProducts().forEach(orderProductRepository::saveAndFlush);
            orderRepository.saveAndFlush(order);
        }
    }

    /**
     * Delete a temporary Order.
     *
     * @param order of type Order.
     */
    @Override
    public void deleteTempOrder(Order order) {
        if (order.getStatus().equals(OrderStatus.RESERVATION)) {
            orderRepository.delete(order);
        }
    }

    @Override
    public List<Order> getAllReservationOrderByCustomer(Customer customer) {
        return orderRepository.findAllByOwnerAndStatus(customer, OrderStatus.RESERVATION);
    }

    /**
     * Update product sold count.
     *
     * @param order of type Order
     */
    private void updateProductSoldCount(Order order) {
        order.getOrderProducts().forEach(orderProduct -> {
            orderProduct.getProduct().increaseSold(orderProduct.getAmount().intValue());
            try {
                productService.update(orderProduct.getProduct());
            } catch (ProductNotFoundException | ProductInvalidException ignored) {
            }
        });
    }

    /**
     * Create the ticket if an order has the status paid.
     *
     * @param order of type Order.
     *
     * @throws UnassignedOrderException when order is not assigned to a Customer.
     */
    private List<Ticket> createTicketIfPaid(Order order) throws UnassignedOrderException, UndefinedPaymentMethodOrderException {
        if (order.getStatus() == OrderStatus.ANONYMOUS || order.getOwner() == null) {
            throw new UnassignedOrderException();
        }

        if (order.getPaymentMethod() == null) {
            throw new UndefinedPaymentMethodOrderException();
        }

        if (order.getStatus() == OrderStatus.PAID) {
            return order.getOrderProducts()
                    .stream()
                    .map(orderProduct -> ticketService.createByOrderProduct(order, orderProduct))
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }
}
