package ch.wisv.events.core.service.order;

import ch.wisv.events.core.exception.normal.*;
import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderProduct;
import ch.wisv.events.core.service.event.EventService;
import ch.wisv.events.core.service.product.ProductService;
import ch.wisv.events.core.service.ticket.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class OrderValidationServiceImpl implements OrderValidationService {

    private final TicketService ticketService;

    private final ProductService productService;

    private final EventService eventService;

    /**
     * Default constructor
     *
     * @param ticketService  of type TicketService
     * @param productService of type ProductService
     * @param eventService   of type EventService
     */
    @Autowired
    public OrderValidationServiceImpl(
            TicketService ticketService,
            ProductService productService,
            EventService eventService
    ) {
        this.ticketService = ticketService;
        this.productService = productService;
        this.eventService = eventService;
    }


    /**
     * Assert if an Order is valid.
     *
     * @param order of type Order when the Order is invalid
     */
    @Override
    public void assertOrderIsValid(Order order) throws OrderInvalidException, OrderExceedEventLimitException, OrderExceedProductLimitException {
        if (order.getAmount() == null) {
            throw new OrderInvalidException("Order amount can not be null");
        }

        Double amountShouldBe = order.getOrderProducts().stream()
                .mapToDouble(orderProduct -> orderProduct.getPrice() * orderProduct.getAmount()).sum();

        if (!order.getAmount().equals(amountShouldBe)) {
            throw new OrderInvalidException("Order amount does not match");
        }

        if (order.getOwner() == null) {
            throw new OrderInvalidException("Order owner can not be null");
        }

        if (order.getPaymentMethod() == null) {
            throw new OrderInvalidException("Order payment method can not be null");
        }

        if (order.getCreatedBy() == null || order.getCreatedBy().equals("")) {
            throw new OrderInvalidException("Order created by can not be null");
        }

        if (order.getOrderProducts().size() == 0) {
            throw new OrderInvalidException("Order should contain products");
        }

        this.assertOrderNotExceedEventLimit(order);
        this.assertOrderNotExceedProductLimit(order);
    }

    /**
     * Assert if the Product in the Order does not exceed the Product limit.
     *
     * @param order of type Order
     * @throws OrderExceedProductLimitException when Product limit will be exceed.
     */
    private void assertOrderNotExceedProductLimit(Order order) throws OrderExceedProductLimitException {
        for (OrderProduct orderProduct : order.getOrderProducts()) {
            int countTicketSold = ticketService.countByProduct(orderProduct.getProduct());
            Integer productMaxSold = orderProduct.getProduct().getMaxSold();

            if (productMaxSold == null) {
                continue;
            }

            if (countTicketSold + orderProduct.getAmount() > productMaxSold) {
                throw new OrderExceedProductLimitException(productMaxSold - countTicketSold);
            }
        }
    }

    /**
     * Assert if the Product in the Order do not exceed the Event limit.
     *
     * @param order of type Order
     * @throws OrderExceedEventLimitException when Event limit will be exceeded.
     */
    private void assertOrderNotExceedEventLimit(Order order) throws OrderExceedEventLimitException {
        for (OrderProduct orderProduct : order.getOrderProducts()) {
            int countTicketSold = ticketService.countByProduct(orderProduct.getProduct());

            try {
                Event event = eventService.getByProduct(orderProduct.getProduct());

                if (event.getMaxSold() == null) {
                    continue;
                }

                if (countTicketSold + orderProduct.getAmount() > event.getMaxSold()) {
                    throw new OrderExceedEventLimitException(event.getMaxSold() - countTicketSold);
                }
            } catch (EventNotFoundException ignored) {
            }
        }
    }

    /**
     * Assert if an Order is valid of a given Customer.
     *
     * @param order    of type Order
     * @param customer of type Customer
     * @throws OrderExceedCustomerLimitException when the customer limit is exceeded
     */
    @Override
    public void assertOrderIsValidForCustomer(Order order, Customer customer) throws OrderExceedCustomerLimitException {
        for (OrderProduct orderProduct : order.getOrderProducts()) {
            int customerTicketCount = ticketService.getAllByProductAndCustomer(orderProduct.getProduct(), customer).size();
            Integer customerProductLimit = orderProduct.getProduct().getMaxSoldPerCustomer();

            if (customerTicketCount + orderProduct.getAmount() > customerProductLimit) {
                throw new OrderExceedCustomerLimitException(customerProductLimit - customerTicketCount);
            }
        }
    }
}
