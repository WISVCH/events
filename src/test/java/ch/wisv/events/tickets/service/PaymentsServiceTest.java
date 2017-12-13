package ch.wisv.events.tickets.service;

import ch.wisv.events.ServiceTest;
import ch.wisv.events.core.exception.runtime.PaymentsConnectionException;
import ch.wisv.events.core.model.order.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderProduct;
import ch.wisv.events.core.model.product.Product;
import com.google.common.collect.ImmutableList;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Copyright (c) 2016  W.I.S.V. 'Christiaan Huygens'
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
public class PaymentsServiceTest extends ServiceTest {

    @Mock
    private HttpClient httpClient;

    private PaymentsService paymentsService;

    @Before
    public void setUp() throws Exception {
        this.paymentsService = new PaymentsServiceImpl(httpClient);
    }

    @Test
    public void testGetPaymentsOrderStatus() throws Exception {
        String paymentsReference = UUID.randomUUID().toString();

        HttpResponse httpResponse = this.createHttpResponse("{\"status\": \"PAID\"}");
        when(httpClient.execute(any(HttpGet.class))).thenReturn(httpResponse);

        assertEquals("PAID", paymentsService.getPaymentsOrderStatus(paymentsReference));
    }

    @Test
    public void testGetPaymentsOrderStatusRandomResponse() throws Exception {
        thrown.expect(PaymentsConnectionException.class);
        String paymentsReference = UUID.randomUUID().toString();

        HttpResponse httpResponse = this.createHttpResponse("{\"something\": \"PAID\"}");
        when(httpClient.execute(any(HttpGet.class))).thenReturn(httpResponse);

        assertEquals("PAID", paymentsService.getPaymentsOrderStatus(paymentsReference));
    }

    @Test
    public void testGetPaymentsOrderStatusIoException() throws Exception {
        thrown.expect(PaymentsConnectionException.class);
        String paymentsReference = UUID.randomUUID().toString();

        when(httpClient.execute(any(HttpPost.class))).thenThrow(new IOException());

        paymentsService.getPaymentsOrderStatus(paymentsReference);
    }

    @Test
    public void testGetPaymentsMollieUrl() throws Exception {
        Order order = new Order();
        order.setCustomer(new Customer("San Tanino", "sant@ch.tudelft.nl", "sant", ""));

        HttpResponse httpResponse = this.createHttpResponse("{\"url\": \"https://payments.local\"}");
        when(httpClient.execute(any(HttpPost.class))).thenReturn(httpResponse);

        assertEquals("https://payments.local", paymentsService.getPaymentsMollieUrl(order));

        verify(httpClient, times(1)).execute(any(HttpPost.class));
    }

    @Test
    public void testGetPaymentsMollieUrlNoUrl() throws Exception {
        thrown.expect(PaymentsConnectionException.class);
        Order order = new Order();
        order.setCustomer(new Customer("San Tanino", "sant@ch.tudelft.nl", "sant", ""));

        HttpResponse httpResponse = this.createHttpResponse("{\"message\": \"https://payments.local\"}");
        when(httpClient.execute(any(HttpPost.class))).thenReturn(httpResponse);

        paymentsService.getPaymentsMollieUrl(order);
    }

    @Test
    public void testGetPaymentsMollieUrlIoException() throws Exception {
        thrown.expect(PaymentsConnectionException.class);
        Order order = new Order();
        order.setCustomer(new Customer("San Tanino", "sant@ch.tudelft.nl", "sant", ""));

        when(httpClient.execute(any(HttpPost.class))).thenThrow(new IOException());

        paymentsService.getPaymentsMollieUrl(order);
    }

    @Test
    public void testCreatePaymentsOrderHttpPost() throws Exception {
        Order order = mock(Order.class);
        Customer customer = mock(Customer.class);
        Product product = mock(Product.class);
        OrderProduct orderProduct = new OrderProduct(product, 1.d, 2L);
        String key = "994153c6-9cca-48c8-9693-0b07dd19d141";
        String productKey = "ef4153c6-9693-48c8-48c8-0b07dd19d141";

        when(order.getCustomer()).thenReturn(customer);
        when(order.getPublicReference()).thenReturn(key);
        when(order.getOrderProducts()).thenReturn(ImmutableList.of(orderProduct));
        when(product.getKey()).thenReturn(productKey);
        when(customer.getName()).thenReturn("U il");
        when(customer.getEmail()).thenReturn("uil@hoo.hoo");

        HttpPost post = paymentsService.createPaymentsOrderHttpPost(order);

        String entity = "{\"productKeys\":[\"ef4153c6-9693-48c8-48c8-0b07dd19d141\",\"ef4153c6-9693-48c8-48c8-0b07dd19d141\"],\"name\":\"U il\"," +
                "\"mailConfirmation\":false,\"returnUrl\":\"null\\/status\\/994153c6-9cca-48c8-9693-0b07dd19d141\\/\",\"email\":\"uil@hoo.hoo\"}";

        assertEquals(entity, EntityUtils.toString(post.getEntity()));
    }

    private HttpResponse createHttpResponse(String response) throws Exception {
        HttpResponse httpResponse = new BasicHttpResponse(
                new BasicStatusLine(new ProtocolVersion("TCP", 1, 1), 200, "")
        );
        BasicHttpEntity entity = new BasicHttpEntity();
        entity.setContent(new ByteArrayInputStream(response.getBytes(StandardCharsets.UTF_8.name())));
        httpResponse.setEntity(entity);

        return httpResponse;
    }
}