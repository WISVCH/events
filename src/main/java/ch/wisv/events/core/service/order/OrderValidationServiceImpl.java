package ch.wisv.events.core.service.order;

import ch.wisv.events.core.exception.normal.EventNotFoundException;
import ch.wisv.events.core.exception.normal.OrderExceedCustomerLimitException;
import ch.wisv.events.core.exception.normal.OrderExceedEventLimitException;
import ch.wisv.events.core.exception.normal.OrderExceedProductLimitException;
import ch.wisv.events.core.exception.normal.OrderInvalidException;
import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderProduct;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.repository.OrderRepository;
import ch.wisv.events.core.service.event.EventService;
import ch.wisv.events.core.service.product.ProductService;
import ch.wisv.events.core.service.ticket.TicketService;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * OrderValidationServiceImpl class.
 */
@Service
public class OrderValidationServiceImpl implements OrderValidationService {

    /** OrderService. */
    private final OrderRepository orderRepository;

    /** TicketService. */
    private final TicketService ticketService;

    /** ProductService. */
    private final ProductService productService;

    /** EventService. */
    private final EventService eventService;

    /**
     * Possible administration costs value
     */
    @Value("${administrationCosts}")
    private double administrationCosts;

    /**
     * OrderValidationServiceImpl constructor.
     *
     * @param orderRepository of type OrderRepository
     * @param ticketService   of type TicketService
     * @param eventService    of type EventService
     */
    @Autowired
    public OrderValidationServiceImpl(OrderRepository orderRepository, TicketService ticketService, ProductService productService, EventService eventService) {
        this.orderRepository = orderRepository;
        this.ticketService = ticketService;
        this.productService = productService;
        this.eventService = eventService;
    }

    /**
     * Assert if an Order is valid.
     *
     * @param order of type Order when the Order is invalid
     */
    @Override
    public void assertOrderIsValid(Order order) throws OrderInvalidException, OrderExceedEventLimitException, OrderExceedProductLimitException {
        this.assertDefaultOrderChecks(order);
        this.assertOrderNotExceedEventLimit(order);
        this.assertOrderNotExceedProductLimit(order);
        this.assertProductsInSellInterval(order);
    }

    /**
     * Assert if an Order is valid of a given Customer.
     *
     * @param order    of type Order
     * @param customer of type Customer
     *
     * @throws OrderExceedCustomerLimitException when the customer limit is exceeded
     */
    @Override
    public void assertOrderIsValidForCustomer(Order order, Customer customer) throws OrderExceedCustomerLimitException {
        List<Order> reservationOrders = orderRepository.findAllByOwnerAndStatus(customer, OrderStatus.RESERVATION);

        for (OrderProduct orderProduct : order.getOrderProducts()) {
            Integer maxSoldPerCustomer = orderProduct.getProduct().getMaxSoldPerCustomer();

            if (maxSoldPerCustomer == null) {
                continue;
            }

            List<Product> relatedProducts = productService.getRelatedProducts(orderProduct.getProduct());

            int ticketSold = ticketService.getAllByProductsAndCustomer(relatedProducts, customer).size();

            ticketSold += reservationOrders.stream()
                    .mapToInt(reservationOrder -> reservationOrder.getOrderProducts().stream()
                            .filter(reservationOrderProduct -> relatedProducts.contains(reservationOrderProduct.getProduct()))
                            .mapToInt(reservationOrderProduct -> reservationOrderProduct.getAmount().intValue())
                            .sum()
                    ).sum();

            int tryingToOrder = order.getOrderProducts().stream()
                    .filter(relatedOrderProduct -> relatedProducts.contains(relatedOrderProduct.getProduct()))
                    .mapToInt(relatedOrderProduct -> relatedOrderProduct.getAmount().intValue())
                    .sum();

            if (ticketSold + tryingToOrder > maxSoldPerCustomer) {
                throw new OrderExceedCustomerLimitException(maxSoldPerCustomer - ticketSold);
            }
        }
    }

    /**
     * Assert if an Order is valid to go to the payment process.
     *
     * @param order of type Order
     */
    @Override
    public void assertOrderIsValidForPayment(Order order) throws OrderInvalidException {
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new OrderInvalidException("Invalid status of the Order");
        }

        this.assertDefaultOrderChecks(order);
    }

    /**
     * Assert the default Order checks.
     *
     * @param order of type Order
     *
     * @throws OrderInvalidException when Order is invalid.
     */
    private void assertDefaultOrderChecks(Order order) throws OrderInvalidException {
        if (order.getAmount() == null) {
            throw new OrderInvalidException("Order amount can not be null");
        }

        Double administrationCostShouldBe = order.getOrderProducts().stream()
                .mapToDouble(orderProduct -> orderProduct.getPrice() * orderProduct.getAmount())
                .anyMatch(c -> c > 0.0) ? administrationCosts : 0.0;

        if (!order.getAdministrationCosts().equals(administrationCostShouldBe)) {
            throw new OrderInvalidException("Order administration costs does not match");
        }

        Double amountShouldBe = order.getOrderProducts()
                .stream()
                .mapToDouble(orderProduct -> orderProduct.getPrice() * orderProduct.getAmount())
                .sum() + order.getAdministrationCosts();

        if (!order.getAmount().equals(amountShouldBe)) {
            throw new OrderInvalidException("Order amount does not match");
        }

        Double vatShouldBe = Math.round(order.getOrderProducts()
                .stream()
                .mapToDouble(orderProduct -> orderProduct.getVat() * orderProduct.getAmount())
                .sum() * 100.0) / 100.0;

        if (!order.getVat().equals(vatShouldBe)) {
            throw new OrderInvalidException("Order vat does not match");
        }

        if (order.getCreatedBy() == null || order.getCreatedBy().equals("")) {
            throw new OrderInvalidException("Order created by can not be null");
        }

        if (order.getOrderProducts().size() == 0) {
            throw new OrderInvalidException("Order should contain products");
        }
    }

    /**
     * Assert if the Product in the Order do not exceed the Event limit.
     *
     * @param order of type Order
     *
     * @throws OrderExceedEventLimitException when Event limit will be exceeded.
     */
    private void assertOrderNotExceedEventLimit(Order order) throws OrderExceedEventLimitException {
        for (OrderProduct orderProduct : order.getOrderProducts()) {
            try {
                Event event = eventService.getByProduct(orderProduct.getProduct());
                int ticketSold = event.getSold() + event.getReserved();

                if (event.getMaxSold() == null) {
                    continue;
                }

                if (ticketSold + orderProduct.getAmount() > event.getMaxSold()) {
                    throw new OrderExceedEventLimitException(event.getMaxSold() - ticketSold);
                }
            } catch (EventNotFoundException ignored) {
            }
        }
    }

    /**
     * Assert if the Product in the Order does not exceed the Product limit.
     *
     * @param order of type Order
     *
     * @throws OrderExceedProductLimitException when Product limit will be exceed.
     */
    private void assertOrderNotExceedProductLimit(Order order) throws OrderExceedProductLimitException {
        for (OrderProduct orderProduct : order.getOrderProducts()) {
            Product product = orderProduct.getProduct();
            int ticketSold = product.getSold() + product.getReserved();

            if (product.getMaxSold() == null) {
                continue;
            }

            if (ticketSold + orderProduct.getAmount() > product.getMaxSold()) {
                throw new OrderExceedProductLimitException(product.getMaxSold() - ticketSold);
            }
        }
    }

    /**
     * Assert is products in Order in the sell interval of each product.
     *
     * @param order of type Order
     *
     * @throws OrderInvalidException when a product is no longer for sale
     */
    private void assertProductsInSellInterval(Order order) throws OrderInvalidException {
        for (OrderProduct orderProduct : order.getOrderProducts()) {
            Product product = orderProduct.getProduct();

            if (product.getSellStart() != null && product.getSellStart().isAfter(LocalDateTime.now())) {
                throw new OrderInvalidException(product.getTitle() + " is not yet for sale");
            }

            if (product.getSellEnd() != null && product.getSellEnd().isBefore(LocalDateTime.now())) {
                throw new OrderInvalidException(product.getTitle() + " is no longer for sale");
            }
        }
    }
}
