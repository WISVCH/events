package ch.wisv.events.core.service.product;

import ch.wisv.events.core.exception.ProductInUseException;
import ch.wisv.events.core.exception.ProductNotFound;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.repository.ProductRepository;
import ch.wisv.events.core.service.event.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
public class ProductServiceImpl implements ProductService {

    /**
     * ProductRepository
     */
    @Autowired
    private ProductRepository productRepository;

    /**
     * EventService
     */
    @Autowired
    private EventService eventService;

    /**
     * Get all products
     *
     * @return List of Products
     */
    @Override
    public List<Product> getAllProducts() {
        return this.productRepository.findAll();
    }

    /**
     * Get all available products
     *
     * @return Collection of Products
     */
    @Override
    public List<Product> getAvailableProducts() {
        return productRepository.findAllBySellStartBeforeAndSellEndAfter(LocalDateTime.now(), LocalDateTime.now())
                                .stream().filter(x -> x.getSold() < x.getMaxSold())
                                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Get Product by Key
     *
     * @param key key of a Product
     * @return Product
     */
    @Override
    public Product getByKey(String key) {
        Optional<Product> product = productRepository.findByKey(key);
        if (product.isPresent()) {
            return product.get();
        }
        throw new ProductNotFound("Product with key " + key + " not found!");
    }

    /**
     * Get Product by ID
     *
     * @param productID id of a Product
     * @return Product
     */
    @Override
    public Product getByID(Integer productID) {
        Optional<Product> product = productRepository.findById(productID);
        if (product.isPresent()) {
            return product.get();
        }
        throw new ProductNotFound("Product with id " + productID + " not found!");
    }

    /**
     * Update Product using a Product
     *
     * @param product Product containing the new product information
     */
    @Override
    public void update(Product product) {
        Product model = this.getByKey(product.getKey());
        model.setTitle(product.getTitle());
        model.setDescription(product.getDescription());
        model.setSold(product.getSold());
        model.setCost(product.getCost());
        model.setMaxSold(product.getMaxSold());
        model.setSellStart(product.getSellStart());
        model.setSellEnd(product.getSellEnd());

        productRepository.save(model);
    }

    /**
     * Add a new Product using a Product
     *
     * @param product of type Product
     */
    @Override
    public void create(Product product) {
        productRepository.saveAndFlush(product);
    }

    /**
     * Remove a Product
     *
     * @param product Product to be deleted.
     */
    @Override
    public void delete(Product product) {
        List<Event> events = eventService.getEventByProductKey(product.getKey());
        if (events.size() > 0) {
            throw new ProductInUseException("Product is already added to an Event");
        }
        productRepository.delete(product);
    }

}
