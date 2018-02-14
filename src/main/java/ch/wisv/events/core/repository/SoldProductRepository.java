package ch.wisv.events.core.repository;

import ch.wisv.events.core.model.order.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.SoldProduct;
import ch.wisv.events.core.model.order.SoldProductStatus;
import ch.wisv.events.core.model.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;

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
public interface SoldProductRepository extends JpaRepository<SoldProduct, Integer> {

    /**
     * Method findAllByOrder find list of sold product by order.
     *
     * @param order of type Order
     * @return List<SoldProduct>
     */
    List<SoldProduct> findAllByOrder(Order order);

    /**
     * Method findAllByProduct find list of sold products by product.
     *
     * @param product of type Product
     * @return List<SoldProduct>
     */
    List<SoldProduct> findAllByProduct(Product product);

    /**
     * Method findAllByCustomerAndProduct find list of sold products by customer and product.
     *
     * @param product  of type Product
     * @param customer of type Customer
     * @return List<SoldProduct>
     */
    List<SoldProduct> findAllByProductAndCustomerAndStatusNotIn(Product product, Customer customer, List<SoldProductStatus> status);

    /**
     * Method findAllByCustomer find list of sold products by customer.
     *
     * @param customer of type Customer
     * @return List<SoldProduct>
     */
    List<SoldProduct> findAllByCustomer(Customer customer);

    /**
     * Method findByKey find a sold product by key
     *
     * @param key key of a SoldProduct
     * @return SoldProduct
     */
    Optional<SoldProduct> findByKey(String key);

    /**
     * Method countAllByProduct.
     *
     * @param product of type Product
     * @return Long
     */
    Long countAllByProduct(Product product);

    /**
     * Method findAllByProductAndUniqueCode.
     *
     * @param product    of type Product
     * @param uniqueCode of type String
     * @return List<SoldProduct>
     */
    Optional<SoldProduct> findByProductAndUniqueCode(Product product, String uniqueCode);
}
