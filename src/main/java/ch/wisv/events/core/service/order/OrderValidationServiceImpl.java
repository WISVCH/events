package ch.wisv.events.core.service.order;

import ch.wisv.events.core.exception.normal.*;
import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderProduct;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.service.event.EventService;
import ch.wisv.events.core.service.ticket.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
public class
OrderValidationServiceImpl implements OrderValidationService {

    private final OrderService orderService;

    private final TicketService ticketService;

    private final EventService eventService;

    /**
     * Default constructor
     *
     * @param orderService  of type Order Service
     * @param ticketService of type TicketSerivce
     * @param eventService  of type EventService
     */
    @Autowired
    public OrderValidationServiceImpl(OrderService orderService, TicketService ticketService, EventService eventService) {
        this.orderService = orderService;
        this.ticketService = ticketService;
        this.eventService = eventService;
    }


    /**
     * Assert if an Order is valid.
     *
     * @param order of type Order when the Order is invalid
     */
    @Override
    public void assertOrderIsValid(Order order) throws OrderInvalidException, OrderExceedEventLimitException, OrderExceedProductLimitException {
        this.assertDefaultOrderChecks(order);
        this.assertOrderNotExceedEventLimit(order);
        this.assertOrderNotExceedProductLimit(order);
    }

    /**
     * Assert the default Order checks
     *
     * @param order of type Order
     * @throws OrderInvalidException when Order is invalid.
     */
    private void assertDefaultOrderChecks(Order order) throws OrderInvalidException {
        if (order.getAmount() == null) {
            throw new OrderInvalidException("Order amount can not be null");
        }

        Double amountShouldBe = order.getOrderProducts().stream()
                .mapToDouble(orderProduct -> orderProduct.getPrice() * orderProduct.getAmount()).sum();

        if (!order.getAmount().equals(amountShouldBe)) {
            throw new OrderInvalidException("Order amount does not match");
        }

        if (order.getCreatedBy() == null || order.getCreatedBy().equals("")) {
            throw new OrderInvalidException("Order created by can not be null");
        }

        if (order.getOrderProducts().size() == 0) {
            throw new OrderInvalidException("Order should contain products");
        }
    }

    /**
     * Assert if the Product in the Order does not exceed the Product limit.
     *
     * @param order of type Order
     * @throws OrderExceedProductLimitException when Product limit will be exceed.
     */
    private void assertOrderNotExceedProductLimit(Order order) throws OrderExceedProductLimitException {
        for (OrderProduct orderProduct : order.getOrderProducts()) {
            Product product = orderProduct.getProduct();
            int ticketSold = product.getSold() + product.getReserved();

            if (product.getMaxSold() == null) {
                continue;
            }

            if (ticketSold + orderProduct.getAmount() > product.getMaxSold()) {
                throw new OrderExceedProductLimitException(product.getMaxSold() - ticketSold);
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
            try {
                Event event = eventService.getByProduct(orderProduct.getProduct());
                int ticketSold = event.getSold() + event.getReserved();

                if (event.getMaxSold() == null) {
                    continue;
                }

                if (ticketSold + orderProduct.getAmount() > event.getMaxSold()) {
                    throw new OrderExceedEventLimitException(event.getMaxSold() - ticketSold);
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
        List<Order> reservationOrders = orderService.getAllReservationOrderByCustomer(customer);

        for (OrderProduct orderProduct : order.getOrderProducts()) {
            Integer maxSoldPerCustomer = orderProduct.getProduct().getMaxSoldPerCustomer();

            if (maxSoldPerCustomer == null) {
                continue;
            }

            int ticketSold = ticketService.getAllByProductAndCustomer(orderProduct.getProduct(), customer).size();

            ticketSold += reservationOrders.stream()
                    .mapToInt(reservationOrder -> reservationOrder.getOrderProducts().stream()
                            .filter(product -> orderProduct.getProduct().equals(product.getProduct()))
                            .mapToInt(product -> product.getAmount().intValue())
                            .sum()
                    )
                    .sum();

            if (ticketSold + orderProduct.getAmount() > maxSoldPerCustomer) {
                throw new OrderExceedCustomerLimitException(maxSoldPerCustomer - ticketSold);
            }
        }
    }

    @Override
    public void assertOrderIsValidForPayment(Order order) throws OrderInvalidException {
        if (order.getOwner() == null) {
            throw new OrderInvalidException("Order should contain an Owner");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new OrderInvalidException("Invalid status of the Order");
        }

        this.assertDefaultOrderChecks(order);
    }
}
