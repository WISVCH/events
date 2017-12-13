package ch.wisv.events.tickets.service;

import ch.wisv.connect.common.model.CHUserInfo;
import ch.wisv.events.ServiceTest;
import ch.wisv.events.core.exception.normal.CustomerInvalidException;
import ch.wisv.events.core.exception.normal.CustomerNotFoundException;
import ch.wisv.events.core.exception.normal.OrderInvalidException;
import ch.wisv.events.core.exception.normal.PaymentsStatusUnknown;
import ch.wisv.events.core.exception.runtime.PaymentsConnectionException;
import ch.wisv.events.core.model.order.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.service.customer.CustomerService;
import ch.wisv.events.core.service.order.OrderService;
import org.junit.Before;
import org.junit.Test;
import org.mitre.openid.connect.model.OIDCAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
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
public class TicketsServiceTest extends ServiceTest {

    @MockBean
    public PaymentsService paymentsService;

    @MockBean
    public CustomerService customerService;

    @MockBean
    public OrderService orderService;

    @Autowired
    public TicketsService ticketsService;

    private Order order;

    @Before
    public void setUp() throws Exception {
        this.order = new Order();
    }

    @Test
    public void testGetCustomerAuthFailure() throws Exception {
        thrown.expect(AccessDeniedException.class);
        SecurityContextHolder.getContext().setAuthentication(null);

        ticketsService.getCurrentCustomer();
    }

    @Test
    public void testGetCurrentCustomerCreateExistingUser() throws Exception {
        CHUserInfo userInfo = new CHUserInfo();
        userInfo.setName("San Taious");
        userInfo.setEmail("sant@ch.tudelft.nl");
        userInfo.setLdapUsername("sant");
        SecurityContextHolder.getContext().setAuthentication(new OIDCAuthenticationToken("", "", userInfo, new ArrayList<>(), null, "", ""));

        when(customerService.getByChUsernameOrEmail(userInfo.getLdapUsername(), userInfo.getEmail())).thenReturn(new Customer(userInfo.getName(),
                userInfo.getEmail(), userInfo.getLdapUsername(), ""));

        Customer customer = ticketsService.getCurrentCustomer();

        assertEquals(userInfo.getName(), customer.getName());
        assertEquals(userInfo.getEmail(), customer.getEmail());
        assertEquals(userInfo.getLdapUsername(), customer.getChUsername());
    }

    @Test
    public void testGetCurrentCustomerCreateNewUser() throws Exception {
        CHUserInfo userInfo = new CHUserInfo();
        userInfo.setName("San Taious");
        userInfo.setEmail("sant@ch.tudelft.nl");
        userInfo.setLdapUsername("sant");
        SecurityContextHolder.getContext().setAuthentication(new OIDCAuthenticationToken("", "", userInfo, new ArrayList<>(), null, "", ""));

        when(customerService.getByChUsernameOrEmail(userInfo.getLdapUsername(), userInfo.getEmail())).thenThrow(new CustomerNotFoundException(""));
        when(customerService.createByChUserInfo(userInfo)).thenReturn(new Customer(userInfo.getName(), userInfo.getEmail(), userInfo
                .getLdapUsername(), ""));

        Customer customer = ticketsService.getCurrentCustomer();

        assertEquals(userInfo.getName(), customer.getName());
        assertEquals(userInfo.getEmail(), customer.getEmail());
        assertEquals(userInfo.getLdapUsername(), customer.getChUsername());
    }

    @Test
    public void testGetCurrentCustomerCreateNewInvalidUser() throws Exception {
        thrown.expect(AccessDeniedException.class);

        CHUserInfo userInfo = new CHUserInfo();
        userInfo.setName("San Taious");
        userInfo.setEmail("sant@ch.tudelft.nl");
        userInfo.setLdapUsername("sant");
        SecurityContextHolder.getContext().setAuthentication(new OIDCAuthenticationToken("", "", userInfo, new ArrayList<>(), null, "", ""));

        when(customerService.getByChUsernameOrEmail(userInfo.getLdapUsername(), userInfo.getEmail())).thenThrow(new CustomerNotFoundException(""));
        when(customerService.createByChUserInfo(userInfo)).thenThrow(new CustomerInvalidException(""));

        ticketsService.getCurrentCustomer();
    }

    @Test
    public void testGetPaymentsMollieUrl() throws Exception {
        String molliePaymentsUrl = "https://mollie.com/payment/2804/supermooi/";
        when(paymentsService.getPaymentsMollieUrl(this.order)).thenReturn(molliePaymentsUrl);

        assertEquals(molliePaymentsUrl, ticketsService.getPaymentsMollieUrl(this.order));
    }

    @Test
    public void testGetPaymentsMollieUrlPaymentsConnectionException() throws Exception {
        thrown.expect(PaymentsConnectionException.class);
        when(paymentsService.getPaymentsMollieUrl(this.order)).thenThrow(new PaymentsConnectionException());

        ticketsService.getPaymentsMollieUrl(this.order);
    }

    @Test
    public void testUpdateOrderStatusWaiting() throws Exception {
        runUpdateOrderStatus("WAITING", OrderStatus.WAITING);
    }

    @Test
    public void testUpdateOrderStatusPaid() throws Exception {
        runUpdateOrderStatus("PAID", OrderStatus.PAID_IDEAL);
    }

    @Test
    public void testUpdateOrderStatusCancelled() throws Exception {
        runUpdateOrderStatus("CANCELLED", OrderStatus.CANCELLED);
    }

    private void runUpdateOrderStatus(String paymentsStatus, OrderStatus orderStatus) throws PaymentsStatusUnknown, OrderInvalidException {
        String reference = UUID.randomUUID().toString();
        when(paymentsService.getPaymentsOrderStatus(reference)).thenReturn(paymentsStatus);
        doNothing().when(orderService).updateOrderStatus(this.order, orderStatus);

        ticketsService.updateOrderStatus(this.order, reference);

        verify(orderService, times(1)).updateOrderStatus(this.order, orderStatus);
    }

    @Test
    public void testUpdateOrderStatusException() throws Exception {
        thrown.expect(PaymentsStatusUnknown.class);
        String reference = UUID.randomUUID().toString();
        when(paymentsService.getPaymentsOrderStatus(reference)).thenReturn("STATUS_EXCEPTION");

        ticketsService.updateOrderStatus(this.order, reference);
    }
}