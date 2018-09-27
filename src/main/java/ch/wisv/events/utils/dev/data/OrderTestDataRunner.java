package ch.wisv.events.utils.dev.data;

import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.order.OrderProduct;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.model.order.PaymentMethod;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.model.ticket.Ticket;
import ch.wisv.events.core.model.ticket.TicketStatus;
import ch.wisv.events.core.repository.CustomerRepository;
import ch.wisv.events.core.repository.OrderProductRepository;
import ch.wisv.events.core.repository.OrderRepository;
import ch.wisv.events.core.repository.ProductRepository;
import ch.wisv.events.core.repository.TicketRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.simple.JSONObject;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * OrderTestDataRunner.
 */
@Component
@Profile("dev")
@Order(value = 6)
public class OrderTestDataRunner extends TestDataRunner {

    /** Ticket Unique code length. */
    private static final int TICKET_UNIQUE_CODE_LENGTH = 6;

    /** OrderRepository. */
    private final OrderRepository orderRepository;

    /** OrderProductRepository. */
    private final OrderProductRepository orderProductRepository;

    /** CustomerRepository. */
    private final CustomerRepository customerRepository;

    /** ProductRepository. */
    private final ProductRepository productRepository;

    /** TicketRepository. */
    private final TicketRepository ticketRepository;

    /**
     * Constructor EventTestDataRunner creates a new EventTestDataRunner instance.
     *
     * @param orderRepository        of type OrderRepository
     * @param orderProductRepository of type OrderProductRepository
     * @param customerRepository     of type CustomerRepository
     * @param productRepository      of type ProductRepository
     * @param ticketRepository       of type TicketRepository
     */
    public OrderTestDataRunner(
            OrderRepository orderRepository,
            OrderProductRepository orderProductRepository,
            CustomerRepository customerRepository,
            ProductRepository productRepository,
            TicketRepository ticketRepository
    ) {
        this.orderRepository = orderRepository;
        this.orderProductRepository = orderProductRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.ticketRepository = ticketRepository;

        this.setJsonFileName("orders.json");
    }

    /**
     * Method createProduct ...
     *
     * @param jsonObject of type JSONObject
     *
     * @return Product
     */
    private ch.wisv.events.core.model.order.Order createOrder(JSONObject jsonObject) {
        String customerRfid = (String) jsonObject.get("customerRfid");
        Optional<Customer> customer = this.customerRepository.findByRfidToken(customerRfid);

        if (customer.isPresent()) {
            ch.wisv.events.core.model.order.Order order = new ch.wisv.events.core.model.order.Order();
            order.setOwner(customer.get());

            List<Product> allProduct = this.productRepository.findAll();

            OrderProduct orderProduct = new OrderProduct();
            Product product = allProduct.get(df.getNumberBetween(0, allProduct.size()));
            orderProduct.setProduct(product);
            orderProduct.setAmount(1L);
            orderProduct.setPrice(product.getCost());

            orderProductRepository.saveAndFlush(orderProduct);

            order.addOrderProduct(orderProduct);
            order.getOrderProducts().forEach(x -> {
                x.getProduct().increaseSold(1);

                this.productRepository.saveAndFlush(x.getProduct());
            });
            order.setStatus(OrderStatus.valueOf((String) jsonObject.get("orderStatus")));
            order.setPaymentMethod(PaymentMethod.valueOf((String) jsonObject.get("paymentMethod")));
            order.setTicketCreated(true);
            order.setAmount(order.getOrderProducts().stream().mapToDouble(x -> x.getAmount() * x.getPrice()).sum());

            return order;
        }

        return new ch.wisv.events.core.model.order.Order();
    }

    /**
     * Method loop.
     *
     * @param jsonObject of type JSONObject
     */
    @Override
    protected void loop(JSONObject jsonObject) {
        ch.wisv.events.core.model.order.Order order = this.createOrder(jsonObject);

        this.orderRepository.saveAndFlush(order);

        if (order.getStatus() == OrderStatus.PAID) {
            order.getOrderProducts().forEach(orderProduct -> {
                Ticket ticket = new Ticket(
                        order.getOwner(),
                        orderProduct.getProduct(),
                        RandomStringUtils.randomNumeric(TICKET_UNIQUE_CODE_LENGTH)
                );
                ticket.setKey(UUID.randomUUID().toString());

                if (df.getNumberBetween(0, 2) == 0) {
                    ticket.setStatus(TicketStatus.OPEN);
                } else {
                    ticket.setStatus(TicketStatus.SCANNED);
                }

                this.ticketRepository.saveAndFlush(ticket);
            });
        }
    }
}
