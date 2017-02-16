package ch.wisv.events.core.service.product;

import ch.wisv.events.core.exception.SoldProductNotFoundException;
import ch.wisv.events.core.model.order.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.SoldProduct;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.repository.SoldProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
@Service
public class SoldProductServiceImpl implements SoldProductService {

    /**
     * Field soldProductRepository
     */
    @Autowired
    private SoldProductRepository soldProductRepository;

    /**
     * Get SoldProduct by key
     *
     * @return SoldProduct
     */
    @Override
    public SoldProduct getByKey(String key) {
        Optional<SoldProduct> optional = this.soldProductRepository.findByKey(key);
        if (optional.isPresent()) {
            return optional.get();
        }
        throw new SoldProductNotFoundException("Sold Product with key " + key + " is not found!");
    }

    /**
     * Method getAll returns the all of this SoldProductService object.
     *
     * @return the all (type List<SoldProduct>) of this SoldProductService object.
     */
    @Override
    public List<SoldProduct> getAll() {
        return soldProductRepository.findAll();
    }

    /**
     * Method getByProduct find sold products by product.
     *
     * @param product of type Product
     * @return List<SoldProduct>
     */
    @Override
    public List<SoldProduct> getByProduct(Product product) {
        return soldProductRepository.findAllByProduct(product);
    }

    /**
     * Method getByCustomerAndProduct find sold products by customer and products.
     *
     * @param customer of type Customer
     * @param product  of type Product
     * @return List<SoldProduct>
     */
    @Override
    public List<SoldProduct> getByCustomerAndProduct(Customer customer, Product product) {
        return soldProductRepository.findAllByCustomerAndProduct(customer, product);
    }

    /**
     * Method getByCustomer find sold products by customer
     *
     * @param customer of type Customer
     * @return List<SoldProduct>
     */
    @Override
    public List<SoldProduct> getByCustomer(Customer customer) {
        return soldProductRepository.findAllByCustomer(customer);
    }

    /**
     * Method create ...
     *
     * @param order of type Order
     */
    @Override
    public void create(Order order) {
        for (Product product : order.getProducts()) {
            SoldProduct sold = new SoldProduct();

            sold.setProduct(product);
            sold.setCustomer(order.getCustomer());
            sold.setOrder(order);

            soldProductRepository.saveAndFlush(sold);
        }
    }

    /**
     * Method remove ...
     *
     * @param order of type Order
     */
    @Override
    public void remove(Order order) {
        List<SoldProduct> soldProducts = soldProductRepository.findAllByOrder(order);

        for (SoldProduct soldProduct : soldProducts) {
            soldProductRepository.delete(soldProduct);
        }
    }

    /**
     * Update sold product
     *
     * @param soldProduct
     */
    @Override
    public void update(SoldProduct soldProduct) {
        SoldProduct model = this.getByKey(soldProduct.getKey());
        model.setOrder(soldProduct.getOrder());
        model.setStatus(soldProduct.getStatus());
        model.setProduct(soldProduct.getProduct());
        model.setCustomer(soldProduct.getCustomer());

        soldProductRepository.save(soldProduct);
    }

}
