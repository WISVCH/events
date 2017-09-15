package ch.wisv.events.utils.dev.data;

import ch.wisv.events.core.model.order.Customer;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.repository.CustomerRepository;
import ch.wisv.events.core.repository.OrderRepository;
import ch.wisv.events.core.repository.ProductRepository;
import org.json.simple.JSONObject;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

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
@Component
@Profile("dev")
@Order(value = 6)
public class OrderTestDataRunner extends TestDataRunner {

    /**
     * Field orderRepository
     */
    private final OrderRepository orderRepository;

    /**
     * Field customerRepository
     */
    private final CustomerRepository customerRepository;

    /**
     * Field productRepository
     */
    private final ProductRepository productRepository;

    /**
     * Constructor EventTestDataRunner creates a new EventTestDataRunner instance.
     *
     * @param orderRepository    of type OrderRepository
     * @param customerRepository of type CustomerRepository
     * @param productRepository  of type ProductRepository
     */
    public OrderTestDataRunner(OrderRepository orderRepository, CustomerRepository customerRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;

        this.setJsonFileName("orders.json");
    }

    /**
     * Method loop
     *
     * @param jsonObject of type JSONObject
     */
    @Override
    protected void loop(JSONObject jsonObject) {
        ch.wisv.events.core.model.order.Order order = this.createOrder(jsonObject);

        this.orderRepository.save(order);
    }

    /**
     * Method createProduct ...
     *
     * @param jsonObject of type JSONObject
     * @return Product
     */
    private ch.wisv.events.core.model.order.Order createOrder(JSONObject jsonObject) {
        String customerRfid = (String) jsonObject.get("customerRfid");
        Optional<Customer> customer = this.customerRepository.findByRfidToken(customerRfid);

        if (customer.isPresent()) {
            ch.wisv.events.core.model.order.Order order = new ch.wisv.events.core.model.order.Order();
            order.setCustomer(customer.get());

            List<Product> allProduct = this.productRepository.findAll();
            order.addProduct(allProduct.get(df.getNumberBetween(0, allProduct.size())));
            order.setStatus(OrderStatus.valueOf((String) jsonObject.get("orderStatus")));

            return order;
        }

        return new ch.wisv.events.core.model.order.Order();
    }
}
