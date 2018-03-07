package ch.wisv.events.core.service.order;

import ch.wisv.events.core.exception.normal.*;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderProduct;
import ch.wisv.events.core.model.order.OrderProductDTO;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.model.ticket.Ticket;
import ch.wisv.events.core.repository.OrderProductRepository;
import ch.wisv.events.core.repository.OrderRepository;
import ch.wisv.events.core.service.mail.MailService;
import ch.wisv.events.core.service.product.ProductService;
import ch.wisv.events.core.service.ticket.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final OrderProductRepository orderProductRepository;

    private final OrderValidationService orderValidationService;

    private final ProductService productService;

    private final MailService mailService;

    private final TicketService ticketService;

    /**
     * Constructor OrderServiceImpl creates a new OrderServiceImpl instance.
     *
     * @param orderRepository        of type OrderRepository
     * @param orderProductRepository of type OrderProductRepository
     * @param orderValidationService of type OrderValidationService
     * @param productService         of type ProductService
     * @param mailService            of type MailService
     * @param ticketService          of type TicketService
     */
    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository,
            OrderProductRepository orderProductRepository,
            OrderValidationService orderValidationService,
            ProductService productService,
            MailService mailService,
            TicketService ticketService
    ) {
        this.orderRepository = orderRepository;
        this.orderProductRepository = orderProductRepository;
        this.orderValidationService = orderValidationService;
        this.productService = productService;
        this.mailService = mailService;
        this.ticketService = ticketService;
    }

    /**
     * Method getAllOrders returns the allOrders of this OrderService object.
     *
     * @return the allOrders (type List<Order>) of this OrderService object.
     */
    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    /**
     * Method getByReference returns Order with the given Reference.
     *
     * @param reference of type String
     * @return Order
     */
    @Override
    public Order getByReference(String reference) throws OrderNotFoundException {
        return orderRepository.findOneByPublicReference(reference).orElseThrow(() ->
                new OrderNotFoundException("reference " + reference));
    }

    /**
     * Create an Order form a OrderProductDTO.
     *
     * @param orderProductDTO of type OrderProductDTO
     * @return Order
     */
    @Override
    public Order createOrderByOrderProductDTO(OrderProductDTO orderProductDTO) throws ProductNotFoundException {
        Order order = new Order();

        for (Map.Entry<String, Long> values : orderProductDTO.getProducts().entrySet()) {
            if (values.getValue() > 0) {
                Product product = productService.getByKey(values.getKey());
                order.addOrderProduct(new OrderProduct(product, product.getCost(), values.getValue()));
            }
        }

        return order;
    }

    /**
     * Create and save and Order.
     *
     * @param order of type Order
     */
    @Override
    public void create(Order order) throws OrderInvalidException, EventNotFoundException, OrderExceedEventLimitException, OrderExceedProductLimitException, OrderExceedCustomerLimitException {
        order.getOrderProducts().forEach(orderProductRepository::saveAndFlush);
        order.updateOrderAmount();

        orderValidationService.assertOrderIsValid(order);
        if (order.getOwner() != null) {
            orderValidationService.assertOrderIsValidForCustomer(order, order.getOwner());
        }

        orderRepository.saveAndFlush(order);
    }


    /**
     * Update OrderStatus of an Order.
     *
     * @param order  of type Order
     * @param status of type OrderStatus
     */
    @Override
    public void updateOrderStatus(Order order, OrderStatus status) throws OrderInvalidException {
        if (order.getId() == null) {
            throw new OrderInvalidException("Order should contain an ID before changing its status");
        }

        if (status == OrderStatus.PAID) {
            try {
                this.updateOrderStatusPaid(order);
            } catch (UnassignedOrderException | UndefinedPaymentMethodOrderException ignored) {
            }
        }
    }

    /**
     * Method updateOrderStatusPaid
     *
     * @param order of type Order
     */
    @Override
    public void updateOrderStatusPaid(Order order) throws UnassignedOrderException, UndefinedPaymentMethodOrderException {
        OrderStatus beforeStatus = order.getStatus();
        order.setStatus(OrderStatus.PAID);
        order.setPaidAt(LocalDateTime.now());

        List<Ticket> tickets = this.createTicketIfPaid(order);
        if (beforeStatus != OrderStatus.PAID && order.getStatus() == OrderStatus.PAID) {
            mailService.sendOrderConfirmation(order, tickets);
            this.updateProductSoldCount(order);
        }

        orderRepository.saveAndFlush(order);
    }

    /**
     * Temporary save an Order.
     *
     * @param order of type Order.
     */
    @Override
    public void tempSaveOrder(Order order) {
        if (order.getStatus().equals(OrderStatus.TEMP)) {
            order.getOrderProducts().forEach(orderProductRepository::saveAndFlush);
            orderRepository.saveAndFlush(order);
        }
    }

    /**
     * Delete a temporary Order.
     *
     * @param order of type Order.
     */
    @Override
    public void deleteTempOrder(Order order) {
        if (order.getStatus().equals(OrderStatus.TEMP)) {
            orderRepository.delete(order);
        }
    }

    /**
     * Update product sold count.
     *
     * @param order of type Order
     */
    private void updateProductSoldCount(Order order) {
        order.getOrderProducts().forEach(orderProduct -> {
            orderProduct.getProduct().increaseSold(orderProduct.getAmount().intValue());
            try {
                productService.update(orderProduct.getProduct());
            } catch (ProductNotFoundException | ProductInvalidException ignored) {
            }
        });
    }

    /**
     * Create the ticket if an order has the status paid.
     *
     * @param order of type Order.
     * @throws UnassignedOrderException when order is not assigned to a Customer.
     */
    private List<Ticket> createTicketIfPaid(Order order) throws UnassignedOrderException, UndefinedPaymentMethodOrderException {
        if (order.getStatus() == OrderStatus.ANONYMOUS || order.getOwner() == null) {
            throw new UnassignedOrderException();
        }

        if (order.getPaymentMethod() == null) {
            throw new UndefinedPaymentMethodOrderException();
        }

        if (order.getStatus() == OrderStatus.PAID) {
            return order.getOrderProducts().stream()
                    .map(orderProduct -> ticketService.createByOrderProduct(order, orderProduct))
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }
}
