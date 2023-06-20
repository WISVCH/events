package ch.wisv.events;

import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderProduct;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.model.order.PaymentMethod;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.model.webhook.Webhook;
import ch.wisv.events.core.model.webhook.WebhookTask;
import ch.wisv.events.core.model.webhook.WebhookTaskStatus;
import ch.wisv.events.core.model.webhook.WebhookTrigger;
import ch.wisv.events.core.repository.*;
import ch.wisv.events.core.service.event.EventService;
import ch.wisv.events.core.service.order.OrderService;
import ch.wisv.events.core.service.ticket.TicketService;
import ch.wisv.events.core.service.webhook.WebhookService;
import ch.wisv.events.utils.LdapGroup;
import com.google.common.collect.ImmutableList;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.RandomStringUtils;
import org.aspectj.weaver.ast.Or;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

/**
 * ControllerTest class.
 */
@Transactional
public abstract class ControllerTest {

    /**
     * Testing utils.
     */
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /**
     * Services.
     */
    @Autowired
    protected EventService eventService;

    @Autowired
    protected OrderService orderService;

    @Autowired
    protected WebhookService webhookService;

    @Autowired
    protected TicketService ticketService;

    /**
     * Repositories.
     */
    @Autowired
    protected EventRepository eventRepository;

    @Autowired
    protected ProductRepository productRepository;

    @Autowired
    protected OrderRepository orderRepository;

    @Autowired
    protected CustomerRepository customerRepository;

    @Autowired
    protected OrderProductRepository orderProductRepository;

    @Autowired
    protected WebhookRepository webhookRepository;

    @Autowired
    protected WebhookTaskRepository webhookTaskRepository;

    /**
     * Other.
     */
    @Autowired
    protected WebApplicationContext wac;

    protected MockMvc mockMvc;

    /**
     * Setup and tear down.
     */
    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @After
    public void tearDown() {
        this.mockMvc = null;

        // Clear repository
        orderRepository.deleteAll();
        eventRepository.deleteAll();
        productRepository.deleteAll();
        customerRepository.deleteAll();
        orderProductRepository.deleteAll();
        webhookTaskRepository.deleteAll();
        webhookRepository.deleteAll();
    }

    protected Event createEvent() {
        Event event = new Event();
        event.setTitle(RandomStringUtils.random(12));
        event.setKey(UUID.randomUUID().toString());
        event.setStart(LocalDateTime.now().plusMonths(1));
        event.setEnding(LocalDateTime.now().plusMonths(1).plusHours(1));
        event.setTarget(100);

        eventRepository.saveAndFlush(event);

        return event;
    }

    protected Customer createCustomer() {
        Customer customer = new Customer();
        customer.setSub("WISVCH.1234");
        customer.setName("Test user");
        customer.setEmail("test@test.com");
        customer.setRfidToken("RF123456");

        customerRepository.saveAndFlush(customer);

        return customer;
    }

    protected Product createProduct() {
        Product product = new Product();
        product.setTitle("Product product");
        product.setCost(1.d);
        product.setSellStart(LocalDateTime.now());
        product.setProducts(new ArrayList<>());
        product.setMaxSoldPerCustomer(1);
        productRepository.saveAndFlush(product);

        return product;
    }

    protected Order createPaymentOrder(OrderStatus orderStatus, String createdBy) {
        List<Product> products = new ArrayList<>();
        products.add(createProduct());

        return this.createOrder(createCustomer(), products, orderStatus, createdBy);
    }

    protected Order createOrder(Customer customer, List<Product> products, OrderStatus status, String createdBy) {
        Order order = new Order();
        order.setOwner(customer);
        order.setStatus(status);
        order.setCreatedBy(createdBy);
        order.setPaymentMethod(PaymentMethod.IDEAL);

        products.forEach(product -> {
            OrderProduct orderProduct = new OrderProduct(product, product.getCost(), 1L);
            orderProductRepository.saveAndFlush(orderProduct);
            order.addOrderProduct(orderProduct);
        });

        orderRepository.saveAndFlush(order);

        return order;
    }

    protected OrderProduct createOrderProduct(Product product) {
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setProduct(product);
        orderProduct.setPrice(10.0);
        orderProduct.setAmount((long) 10);

        orderProductRepository.saveAndFlush(orderProduct);

        return orderProduct;
    }

    protected Webhook createWebhook() {
        Webhook webhook = new Webhook();
        webhook.setActive(true);
        webhook.setLdapGroup(LdapGroup.CHBEHEER);
        webhook.setPayloadUrl("https://test.frl/");
        webhook.setSecret("secret");
        webhook.setWebhookTriggers(ImmutableList.of(WebhookTrigger.EVENT_CREATE_UPDATE));

        return webhook;
    }

    protected WebhookTask createWebhookTask() {
        WebhookTask webhookTask = new WebhookTask();
        webhookTask.setCreatedAt(LocalDateTime.now());
        webhookTask.setWebhookTaskStatus(WebhookTaskStatus.PENDING);
        webhookTask.setWebhook(this.createWebhook());

        return webhookTask;
    }
}