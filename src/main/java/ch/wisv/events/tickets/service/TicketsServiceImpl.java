package ch.wisv.events.tickets.service;

import ch.wisv.connect.common.model.CHUserInfo;
import ch.wisv.events.core.exception.normal.CustomerInvalidException;
import ch.wisv.events.core.exception.normal.CustomerNotFoundException;
import ch.wisv.events.core.exception.normal.PaymentsStatusUnknown;
import ch.wisv.events.core.model.order.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.service.customer.CustomerService;
import org.mitre.openid.connect.model.OIDCAuthenticationToken;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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
@Service
public class TicketsServiceImpl implements TicketsService {

    /**
     * CustomerService customerService.
     */
    private final CustomerService customerService;

    /**
     * PaymentsService paymentsService.
     */
    private final PaymentsService paymentsService;

    /**
     * Constructor.
     *
     * @param customerService of type CustomerService
     * @param paymentsService of type PaymentsService
     */
    public TicketsServiceImpl(CustomerService customerService, PaymentsService paymentsService) {
        this.customerService = customerService;
        this.paymentsService = paymentsService;
    }

    /**
     * Get the Customer that is currently logged in.
     *
     * @return Customer
     */
    @Override
    public Customer getCurrentCustomer() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof OIDCAuthenticationToken) {
            OIDCAuthenticationToken oidcAuth = ((OIDCAuthenticationToken) auth);

            if (oidcAuth.getUserInfo() instanceof CHUserInfo) {
                CHUserInfo userInfo = (CHUserInfo) oidcAuth.getUserInfo();
                try {
                    return customerService.getByChUsernameOrEmail(userInfo.getLdapUsername(), userInfo.getEmail());
                } catch (CustomerNotFoundException e) {
                    try {
                        return customerService.createByChUserInfo(userInfo);
                    } catch (CustomerInvalidException ignored) {
                    }
                }
            }
        }

        throw new AccessDeniedException("Invalid authentication");
    }

    /**
     * Get a Mollie Url via the Payments API.
     *
     * @param order of type Order
     * @return String
     */
    @Override
    public String getPaymentsMollieUrl(Order order) {
        return paymentsService.getPaymentsMollieUrl(order);
    }

    /**
     * Update the status of the Order via the Payments API.
     *
     * @param order             of type Order
     * @param paymentsReference of type String
     * @return Order
     */
    @Override
    public Order updateOrderStatus(Order order, String paymentsReference) throws PaymentsStatusUnknown {
        String status = paymentsService.getPaymentsOrderStatus(paymentsReference);

        switch (status) {
            case "WAITING":
                order.setStatus(OrderStatus.WAITING);
                break;
            case "PAID":
                order.setStatus(OrderStatus.PAID_IDEAL);
                break;
            case "CANCELLED":
                order.setStatus(OrderStatus.CANCELLED);
                break;
            default:
                throw new PaymentsStatusUnknown(status);
        }

        return order;
    }
}
