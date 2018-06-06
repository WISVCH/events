package ch.wisv.events.webshop.service;

import ch.wisv.events.ServiceTest;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.service.mail.MailService;
import ch.wisv.events.core.service.order.OrderService;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.stubbing.Answer;

public class WebshopServiceImplTest extends ServiceTest {

    @Mock
    private PaymentsService paymentsService;

    @Mock
    private OrderService orderService;

    @Mock
    private MailService mailService;

    private WebshopService webshopService;

    @Before
    public void setUp() throws Exception {
        this.webshopService = new WebshopServiceImpl(paymentsService, orderService, mailService);
    }

    @Test
    public void testFetchOrderStatus() throws Exception {
        Order order = new Order();
        order.setStatus(OrderStatus.PENDING);

        String paymentsReference = UUID.randomUUID().toString();

        doAnswer((Answer<Void>) invocation -> {
                     order.setStatus(OrderStatus.PAID);
                     return null;
                 }
        ).when(orderService).updateOrderStatus(order, OrderStatus.PAID);
        when(paymentsService.getPaymentsOrderStatus(paymentsReference)).thenReturn("PAID");

        webshopService.fetchOrderStatus(order, paymentsReference);

        verify(orderService, times(1)).updateOrderStatus(order, OrderStatus.PAID);
    }

}