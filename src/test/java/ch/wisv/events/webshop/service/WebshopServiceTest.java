package ch.wisv.events.webshop.service;

import ch.wisv.events.ServiceTest;
import ch.wisv.events.core.exception.normal.EventsException;
import ch.wisv.events.core.exception.normal.PaymentsStatusUnknown;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.service.customer.CustomerService;
import ch.wisv.events.core.service.order.OrderService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
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
public class WebshopServiceTest extends ServiceTest {

    @MockBean
    public PaymentsService paymentsService;

    @MockBean
    public CustomerService customerService;

    @MockBean
    public OrderService orderService;

    @Autowired
    public WebshopService webshopService;

    private Order order;

    @Before
    public void setUp() throws Exception {
        this.order = new Order();
    }

    @Test
    public void testUpdateOrderStatusWaiting() throws Exception {
        runUpdateOrderStatus("WAITING", OrderStatus.PENDING);
    }

    @Test
    public void testUpdateOrderStatusPaid() throws Exception {
        runUpdateOrderStatus("PAID", OrderStatus.PAID);
    }

    @Test
    public void testUpdateOrderStatusCancelled() throws Exception {
        runUpdateOrderStatus("CANCELLED", OrderStatus.CANCELLED);
    }

    private void runUpdateOrderStatus(String paymentsStatus, OrderStatus orderStatus) throws EventsException {
        String reference = UUID.randomUUID().toString();
        when(paymentsService.getPaymentsOrderStatus(reference)).thenReturn(paymentsStatus);
        doNothing().when(orderService).updateOrderStatusPaid(this.order);

        webshopService.updateOrderStatus(this.order, reference);

        verify(orderService, times(1)).updateOrderStatus(this.order, orderStatus);
    }

    @Test
    public void testUpdateOrderStatusException() throws Exception {
        thrown.expect(PaymentsStatusUnknown.class);
        String reference = UUID.randomUUID().toString();
        when(paymentsService.getPaymentsOrderStatus(reference)).thenReturn("STATUS_EXCEPTION");

        webshopService.updateOrderStatus(this.order, reference);
    }
}