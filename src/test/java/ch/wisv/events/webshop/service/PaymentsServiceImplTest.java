package ch.wisv.events.webshop.service;

import be.woutschoovaerts.mollie.Client;
import be.woutschoovaerts.mollie.data.common.Link;
import be.woutschoovaerts.mollie.data.payment.PaymentLinks;
import be.woutschoovaerts.mollie.data.payment.PaymentRequest;
import be.woutschoovaerts.mollie.data.payment.PaymentResponse;
import be.woutschoovaerts.mollie.handler.PaymentHandler;
import ch.wisv.events.ServiceTest;
import ch.wisv.events.core.exception.normal.PaymentsStatusUnknown;
import ch.wisv.events.core.exception.runtime.PaymentsConnectionException;
import ch.wisv.events.core.exception.runtime.PaymentsInvalidException;
import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderProduct;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.model.order.PaymentMethod;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.service.mail.MailService;
import ch.wisv.events.core.service.order.OrderService;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.json.simple.JSONObject;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.any;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.parameters.P;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * PaymentsServiceImpl test.
 */
public class PaymentsServiceImplTest extends ServiceTest {

    @Mock
    private OrderService orderService;

    @Mock
    private Client mollie;

    private PaymentsServiceImpl paymentsService;

    /**
     * SetUp method.
     */
    @Before
    public void setUp() {
        this.paymentsService = new PaymentsServiceImpl(orderService, mollie, mock(MailService.class));
    }

    @After
    public void tearDown() {
        this.paymentsService = null;
    }


    /**
     * Test get payments mollie url.
     *
     * @throws Exception when something goes wrong
     */
    @Test
    public void testGetMollieUrl() throws Exception {
        Order order = createPaymentOrder(OrderStatus.PENDING,"WISVCH.1234");

        PaymentResponse paymentResponse = new PaymentResponse();
        Link link = Link.builder().build();
        paymentResponse.setLinks(PaymentLinks.builder().checkout(link).build());
        paymentResponse.setId("thisisanid");
        PaymentHandler handler = mock(PaymentHandler.class);
        when(mollie.payments()).thenReturn(handler);
        when(handler.createPayment(Mockito.any())).thenReturn(paymentResponse);

        assertEquals(link.getHref(), paymentsService.getMollieUrl(order));
    }


    protected Customer createCustomer() {
        Customer customer = new Customer();
        customer.setSub("WISVCH.1234");
        customer.setName("Test user");
        customer.setEmail("test@test.com");
        customer.setRfidToken("RF123456");

        return customer;
    }

    protected Product createProduct() {
        Product product = new Product();
        product.setTitle("Product product");
        product.setCost(1.d);
        product.setSellStart(LocalDateTime.now());
        product.setProducts(new ArrayList<>());
        product.setMaxSoldPerCustomer(1);

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
            order.addOrderProduct(orderProduct);
        });

        return order;
    }
}