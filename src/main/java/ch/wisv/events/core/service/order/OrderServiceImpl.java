package ch.wisv.events.core.service.order;

import ch.wisv.events.core.exception.normal.EventNotFoundException;
import ch.wisv.events.core.exception.normal.OrderInvalidException;
import ch.wisv.events.core.exception.normal.OrderNotFoundException;
import ch.wisv.events.core.exception.normal.ProductNotFoundException;
import ch.wisv.events.core.exception.runtime.OrderCannotUpdateException;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.order.*;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.repository.OrderProductRepository;
import ch.wisv.events.core.repository.OrderRepository;
import ch.wisv.events.core.service.event.EventService;
import ch.wisv.events.core.service.mail.MailService;
import ch.wisv.events.core.service.product.ProductService;
import ch.wisv.events.core.service.product.SoldProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    /**
     * Field orderRepository
     */
    private final OrderRepository orderRepository;

    /**
     * Field orderProductRepository
     */
    private final OrderProductRepository orderProductRepository;

    /**
     * Field eventService
     */
    private final MailService mailService;

    /**
     * Field soldProductService
     */
    private final SoldProductService soldProductService;

    /**
     * Field productService
     */
    private final ProductService productService;

    /**
     * Field eventService
     */
    private final EventService eventService;

    /**
     * Constructor OrderServiceImpl creates a new OrderServiceImpl instance.
     *
     * @param orderRepository        of type OrderRepository
     * @param orderProductRepository of type OrderProductRepository
     * @param mailService            of type MailService
     * @param soldProductService     of type SoldProductService
     * @param productService         of type ProductService
     * @param eventService           of type EventService
     */
    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository,
            OrderProductRepository orderProductRepository,
            MailService mailService,
            SoldProductService soldProductService,
            ProductService productService,
            EventService eventService
    ) {
        this.orderRepository = orderRepository;
        this.orderProductRepository = orderProductRepository;
        this.mailService = mailService;
        this.soldProductService = soldProductService;
        this.productService = productService;
        this.eventService = eventService;
    }

    /**
     * Method getAllOrders returns the allOrders of this OrderService object.
     *
     * @return the allOrders (type List<Order>) of this OrderService object.
     */
    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll().stream().filter(x -> x.getCustomer() != null).collect(Collectors.toList());
    }

    /**
     * Method getByReference returns Order with the given Reference.
     *
     * @param reference of type String
     * @return Order
     */
    @Override
    public Order getByReference(String reference) throws OrderNotFoundException {
        return orderRepository.findByPublicReference(reference).orElseThrow(() ->
                new OrderNotFoundException("reference " + reference));
    }

    /**
     * Create and save and Order.
     *
     * @param order of type Order
     */
    @Override
    public void create(Order order) throws OrderInvalidException, EventNotFoundException {
        this.setOrderAmount(order);

        order.getOrderProducts().forEach(orderProductRepository::saveAndFlush);

        this.assertIsValid(order);
        this.assertEnoughProductsLeft(order);
        orderRepository.saveAndFlush(order);
    }

    /**
     * Update and exisiting
     *
     * @param order of type Order
     */
    @Override
    public void update(Order order) throws OrderInvalidException {
        try {
            Order update = this.getByReference(order.getPublicReference());
            update.setStatus(order.getStatus());
            update.setCustomer(order.getCustomer());
            update.setPaidDate(order.getPaidDate());

            this.assertIsValid(order);

            orderRepository.saveAndFlush(order);
        } catch (OrderNotFoundException e) {
            throw new OrderCannotUpdateException();
        }
    }

    /**
     * Method updateOrderStatus
     *
     * @param order  of type Order
     * @param status of type OrderStatus
     *               <p>
     */
    @Override
    public void updateOrderStatus(Order order, OrderStatus status) throws OrderInvalidException {
        OrderStatus oldOrderStatus = order.getStatus();
        order.setStatus(status);

        if (oldOrderStatus != status && status == OrderStatus.REFUNDED) {
            order.getOrderProducts().forEach(orderProduct -> orderProduct.getProduct().setSold(
                    orderProduct.getProduct().getSold() - 1
            ));

            soldProductService.delete(order);
        } else {
            if (!oldOrderStatus.isPaid() && status.isPaid()) {
                order.getOrderProducts().forEach(orderProduct -> orderProduct.getProduct().setSold(
                        orderProduct.getProduct().getSold() + 1
                ));
                order.setPaidDate(LocalDateTime.now());
                List<SoldProduct> soldProducts = soldProductService.create(order);

                mailService.sendOrderToCustomer(order, soldProducts);
            }
        }

        this.update(order);
    }

    /**
     * Create a Order by a OrderProductDTO.
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
     * Assert if the given Order is valid.
     *
     * @param order of type Order.
     */
    @Override
    public void assertIsValid(Order order) throws OrderInvalidException {
        if (order.getAmount() == null) {
            throw new OrderInvalidException("Order amount can not be null");
        }

        if (order.getAmount() < 0) {
            throw new OrderInvalidException("Order amount can not be negative");
        }

        if (order.getOrderProducts() == null || order.getOrderProducts().isEmpty()) {
            throw new OrderInvalidException("OrderProducts list can not be null or empty");
        }

        if (order.getCreationDate() == null) {
            throw new OrderInvalidException("Order creation date can not be null");
        }

        if (order.getCreatedBy() == null || order.getCreatedBy().equals("")) {
            throw new OrderInvalidException("Order created by can not be null");
        }
    }

    /**
     * Assert if this order is valid for a Customer.
     *
     * @param order of type Order.
     */
    @Override
    public void assertIsValidForCustomer(Order order) throws OrderInvalidException {
        for (OrderProduct orderProduct : order.getOrderProducts()) {
            List<SoldProduct> soldProductsCustomers = soldProductService.getAllByCustomerAndProduct(order.getCustomer(), orderProduct.getProduct());
            Integer limitProductOrder = orderProduct.getProduct().getMaxSoldPerCustomer() - soldProductsCustomers.size();

            if (orderProduct.getAmount() > limitProductOrder) {
                throw new OrderInvalidException(order.getCustomer().getName() + " can only buy " + limitProductOrder + " more of " + orderProduct
                        .getProduct().getTitle() + "(s) before reaching limit");
            }
        }
    }

    /**
     * Calculate and set the order amount.
     *
     * @param order of type Order.
     */
    private void setOrderAmount(Order order) {
        order.setAmount(
                order.getOrderProducts().stream()
                        .mapToDouble(orderProduct -> orderProduct.getProduct().getCost() * orderProduct.getAmount())
                        .sum()
        );
    }

    /**
     * Assert if there are enough products left for this Order.
     *
     * @param order of type Order
     */
    private void assertEnoughProductsLeft(Order order) throws OrderInvalidException, EventNotFoundException {
        for (OrderProduct orderProduct : order.getOrderProducts()) {
            Integer soldProductsCount = soldProductService.getByProduct(orderProduct.getProduct()).size();
            Event event = eventService.getEventByProduct(orderProduct.getProduct());
            int soldNow = orderProduct.getAmount().intValue();

            if (this.isSoldProductCountAboveMaxSold(event.getMaxSold(), soldProductsCount, soldNow)) {
                throw new OrderInvalidException("Only " + (event.getMaxSold() - soldProductsCount) + " items left of " + orderProduct.getProduct().getTitle());
            } else if (this.isSoldProductCountAboveMaxSold(orderProduct.getProduct().getMaxSold(), soldProductsCount, soldNow)) {
                throw new OrderInvalidException("Only " + (orderProduct.getProduct().getMaxSold() - soldProductsCount) + " items left of " + orderProduct.getProduct().getTitle());
            }
        }
    }

    /**
     * Is the SoldProduct count above the max sold of a product or event.
     *
     * @param maxSold           of type Integer
     * @param soldProductsCount of type Integer
     * @return boolean
     */
    private boolean isSoldProductCountAboveMaxSold(Integer maxSold, Integer soldProductsCount, Integer soldNow) {
        if (maxSold != null) {
            Integer productsLeftCount = maxSold - soldProductsCount;

            if (soldNow > productsLeftCount) {
                return true;
            }
        }

        return false;
    }
}
