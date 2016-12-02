package ch.wisv.events.service.order;

import ch.wisv.events.data.model.order.Customer;
import ch.wisv.events.data.model.order.Order;
import ch.wisv.events.data.model.order.OrderStatus;
import ch.wisv.events.data.model.product.Product;
import ch.wisv.events.data.request.sales.SalesOrderRequest;
import ch.wisv.events.exception.OrderNotFound;
import ch.wisv.events.exception.ProductLimitExceededException;
import ch.wisv.events.repository.order.OrderRepository;
import ch.wisv.events.repository.product.ProductRepository;
import ch.wisv.events.service.product.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    private final ProductService productService;

    private final ProductRepository productRepository;

    public OrderServiceImpl(OrderRepository orderRepository, ProductService productService,
                            ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productService = productService;
        this.productRepository = productRepository;
    }

    /**
     * @return
     */
    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll().stream().filter(x -> x.getCustomer() != null).collect(Collectors.toList());
    }

    @Override
    public Order getByReference(String reference) {
        Optional<Order> orderOption = orderRepository.findByPublicReference(reference);
        if (orderOption.isPresent()) {
            return orderOption.get();
        }
        throw new OrderNotFound("Order with reference " + reference + " not found!");
    }

    @Override
    public Order createOrder(SalesOrderRequest orderRequest) {
        Order order = new Order();
        for (Map.Entry<String, Integer> entry : orderRequest.getProducts().entrySet()) {
            Product product = productService.getProductByKey(entry.getKey());

            if (product.getSold() + entry.getValue() > product.getMaxSold()) {
                throw new ProductLimitExceededException(
                        "Not enough products of " + product.getTitle() + ", only " + (product
                                .getMaxSold() - product.getSold()) + " items left. ");
            }

            for (Integer i = 0; i < entry.getValue(); i++) {
                order.addProduct(product);
            }
        }

        // TODO: check iff there are products in the order

        orderRepository.saveAndFlush(order);

        return order;
    }

    @Override
    public void addCustomerToOrder(Order order, Customer customer) {
        order.setCustomer(customer);

        orderRepository.save(order);
    }

    /**
     * @param order
     * @param orderStatus
     */
    @Override
    public void updateOrderStatus(Order order, OrderStatus orderStatus) {
        order.setStatus(orderStatus);

        orderRepository.save(order);
    }
}
