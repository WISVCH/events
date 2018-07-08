package ch.wisv.events.webshop.service;

import ch.wisv.events.ServiceTest;
import ch.wisv.events.core.exception.normal.EventsException;
import ch.wisv.events.core.exception.normal.PaymentsStatusUnknown;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.service.order.OrderService;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

public class WebshopServiceTest extends ServiceTest {

    @MockBean
    private OrderService orderService;

    @Autowired
    private WebshopService webshopService;

    private Order order;

    @Before
    public void setUp() throws Exception {
        this.order = new Order();
    }

    @Test
    public void testUpdateOrderStatusWaiting() throws Exception {
        runUpdateOrderStatus("123-345-561", OrderStatus.PENDING);
    }

    @Test
    public void testUpdateOrderStatusPaid() throws Exception {
        runUpdateOrderStatus("123-345-562", OrderStatus.PAID);
    }

    @Test
    public void testUpdateOrderStatusCancelled() throws Exception {
        runUpdateOrderStatus("123-345-563", OrderStatus.CANCELLED);
    }

    @Test
    public void testUpdateOrderStatusException() throws Exception {
        thrown.expect(PaymentsStatusUnknown.class);

        webshopService.updateOrderStatus(this.order, "123-345-564");
    }

    private void runUpdateOrderStatus(String reference, OrderStatus orderStatus) throws EventsException {
        doNothing().when(orderService).updateOrderStatus(this.order, orderStatus);

        webshopService.updateOrderStatus(this.order, reference);

        verify(orderService, times(1)).updateOrderStatus(this.order, orderStatus);
    }
}