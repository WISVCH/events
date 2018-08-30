package ch.wisv.events.webshop.service;

import ch.wisv.events.ServiceTest;
import ch.wisv.events.core.exception.normal.PaymentsStatusUnknown;
import ch.wisv.events.core.exception.runtime.PaymentsConnectionException;
import ch.wisv.events.core.exception.runtime.PaymentsInvalidException;
import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderProduct;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.model.order.PaymentMethod;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.service.mail.MailService;
import ch.wisv.events.core.service.order.OrderService;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.util.UUID;
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
    private HttpClient httpClient;

    private PaymentsService paymentsService;

    /**
     * SetUp method.
     */
    @Before
    public void setUp() {
        this.paymentsService = new PaymentsServiceImpl(orderService, httpClient, mock(MailService.class));
    }

    @After
    public void tearDown() {
        this.paymentsService = null;
    }

    /**
     * Get payments order status.
     *
     * @throws Exception when something goes wrong
     */
    @Test
    public void testGetPaymentsOrderStatus() throws Exception {
        JSONObject object = new JSONObject();
        object.put("status", "EXPIRED");

        HttpResponse httpResponse = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "FINE!"));
        httpResponse.setEntity(new StringEntity(object.toJSONString(), "UTF8"));

        when(httpClient.execute(any(HttpGet.class))).thenReturn(httpResponse);

        assertEquals("EXPIRED", paymentsService.getPaymentsOrderStatus("123-456-789"));
    }

    /**
     * Get payments order status when order status is not set.
     *
     * @throws Exception when something goes wrong
     */
    @Test
    public void testGetPaymentsOrderStatusException() throws Exception {
        thrown.expect(PaymentsConnectionException.class);
        JSONObject object = new JSONObject();

        HttpResponse httpResponse = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "FINE!"));
        httpResponse.setEntity(new StringEntity(object.toJSONString(), "UTF8"));

        when(httpClient.execute(any(HttpGet.class))).thenReturn(httpResponse);

        paymentsService.getPaymentsOrderStatus("123-456-789");
    }

    /**
     * Test get payments mollie url.
     *
     * @throws Exception when something goes wrong
     */
    @Test
    public void testGetPaymentsMollieUrl() throws Exception {
        JSONObject object = new JSONObject();
        object.put("url", "https://ch.tudelft.nl/payments/some/url");
        object.put("publicReference", "123-345-567");

        Order order = this.createHttpResponseAndOrder(object, HttpStatus.SC_CREATED);

        doNothing().when(order).setChPaymentsReference(any(String.class));
        doNothing().when(orderService).update(any(Order.class));

        assertEquals("https://ch.tudelft.nl/payments/some/url", paymentsService.getPaymentsMollieUrl(order));
        verify(order, times(1)).setChPaymentsReference("123-345-567");
    }

    /**
     * Test get payments mollie url.
     *
     * @throws Exception when something goes wrong
     */
    @Test
    public void testGetPaymentsMollieUrlMissingUrl() throws Exception {
        thrown.expect(PaymentsConnectionException.class);
        thrown.expectMessage("Payment provider is not responding");

        JSONObject object = new JSONObject();
        object.put("publicReference", "123-345-567");

        Order order = this.createHttpResponseAndOrder(object, HttpStatus.SC_CREATED);

        doNothing().when(order).setChPaymentsReference(any(String.class));
        doNothing().when(orderService).update(any(Order.class));

        paymentsService.getPaymentsMollieUrl(order);
    }

    /**
     * Test get payments mollie url.
     *
     * @throws Exception when something goes wrong
     */
    @Test
    public void testGetPaymentsMollieUrlMissingPaymentsReference() throws Exception {
        thrown.expect(PaymentsConnectionException.class);
        thrown.expectMessage("Payment provider is not responding");

        JSONObject object = new JSONObject();
        object.put("url", "https://ch.tudelft.nl/payments/some/url");

        Order order = this.createHttpResponseAndOrder(object, HttpStatus.SC_CREATED);

        doNothing().when(order).setChPaymentsReference(any(String.class));
        doNothing().when(orderService).update(any(Order.class));

        paymentsService.getPaymentsMollieUrl(order);
    }

    /**
     * Test get payments mollie url.
     *
     * @throws Exception when something goes wrong
     */
    @Test
    public void testGetPaymentsMollieUrlPaymentsException() throws Exception {
        thrown.expect(PaymentsConnectionException.class);
        thrown.expectMessage("Payment provider is not responding");

        JSONObject object = new JSONObject();
        object.put("message", "No valid products in the order.");

        Order order = this.createHttpResponseAndOrder(object, HttpStatus.SC_BAD_REQUEST);
        when(httpClient.execute(any(HttpPost.class))).thenThrow(new IOException("Connection lost"));

        paymentsService.getPaymentsMollieUrl(order);
    }

    /**
     * Test get payments mollie url.
     *
     * @throws Exception when something goes wrong
     */
    @Test
    public void testGetPaymentsMollieUrlConnectionException() throws Exception {
        thrown.expect(PaymentsConnectionException.class);
        thrown.expectMessage("Payment provider is not responding");

        JSONObject object = new JSONObject();
        object.put("message", "No valid products in the order.");

        Order order = this.createHttpResponseAndOrder(object, HttpStatus.SC_BAD_REQUEST);

        paymentsService.getPaymentsMollieUrl(order);
    }

    /**
     * Map Payments status to OrderStatus.
     *
     * @throws Exception when something goes wrong
     */
    @Test
    public void testMapStatusToOrderStatusPending() throws Exception {
        assertEquals(OrderStatus.PENDING, paymentsService.mapStatusToOrderStatus("WAITING"));
    }

    @Test
    public void testMapStatusToOrderStatusPaid() throws Exception {
        assertEquals(OrderStatus.PAID, paymentsService.mapStatusToOrderStatus("PAID"));
    }

    @Test
    public void testMapStatusToOrderStatusCancelled() throws Exception {
        assertEquals(OrderStatus.CANCELLED, paymentsService.mapStatusToOrderStatus("CANCELLED"));
    }

    @Test
    public void testMapStatusToOrderStatusExpired() throws Exception {
        assertEquals(OrderStatus.EXPIRED, paymentsService.mapStatusToOrderStatus("EXPIRED"));
    }

    @Test
    public void testMapStatusToOrderStatusUnknown() throws Exception {
        thrown.expect(PaymentsStatusUnknown.class);
        paymentsService.mapStatusToOrderStatus("UNKNOWN");
    }

    /**
     * Create a HTTP Response mock and Order.
     *
     * @param object     of type JSONObject
     * @param httpStatus of type int
     *
     * @return Order
     *
     * @throws IOException when something goes wrong.
     */
    private Order createHttpResponseAndOrder(JSONObject object, int httpStatus) throws IOException {
        HttpResponse httpResponse = new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, httpStatus, "CREATED!"));
        httpResponse.setEntity(new StringEntity(object.toJSONString(), "UTF8"));

        when(httpClient.execute(any(HttpPost.class))).thenReturn(httpResponse);

        Order order = mock(Order.class);
        Customer customer = mock(Customer.class);

        OrderProduct orderProduct = mock(OrderProduct.class);
        Product product = mock(Product.class);
        when(product.getKey()).thenReturn(UUID.randomUUID().toString());
        when(orderProduct.getProduct()).thenReturn(product);
        when(orderProduct.getAmount()).thenReturn(1L);

        when(customer.getName()).thenReturn("Test");
        when(customer.getEmail()).thenReturn("test@test.com");
        when(order.getOwner()).thenReturn(customer);
        when(order.getPaymentMethod()).thenReturn(PaymentMethod.IDEAL);
        when(order.getPublicReference()).thenReturn(UUID.randomUUID().toString());
        when(order.getOrderProducts()).thenReturn(ImmutableList.of(orderProduct));

        return order;
    }
}