package ch.wisv.events.core.service.product;

import ch.wisv.events.api.request.ProductDTO;
import ch.wisv.events.core.exception.normal.ProductInvalidException;
import ch.wisv.events.core.exception.normal.ProductNotFoundException;
import ch.wisv.events.core.exception.runtime.ProductAlreadyLinkedException;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.repository.ProductRepository;
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
    private final ProductRepository productRepository;

    /**
     * Constructor ProductServiceImpl creates a new ProductServiceImpl instance.
     *
     * @param productRepository of type ProductRepository
     */
    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

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
        return productRepository.findALlBySellStartBefore(LocalDateTime.now())
                .stream().filter(product -> {
                    if (product.getSellEnd() != null && product.getSellEnd().isBefore(LocalDateTime.now())) {
                        return false;
                    }

                    return product.getMaxSold() == null || product.getSold() < product.getMaxSold();
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Get Product by Key
     *
     * @param key key of a Product
     * @return Product
     */
    @Override
    public Product getByKey(String key) throws ProductNotFoundException {
        Optional<Product> product = productRepository.findByKey(key);

        return product.orElseThrow(() -> new ProductNotFoundException("key " + key));
    }

    /**
     * Add a new Product using a Product
     *
     * @param product of type Product
     */
    @Override
    public Product create(Product product) throws ProductInvalidException {
        if (product.getSellStart() == null) {
            // Set sell start by default to now.
            product.setSellStart(LocalDateTime.now());
        }
        this.assertIsValidProduct(product);

        return productRepository.saveAndFlush(product);
    }

    /**
     * Method create ...
     *
     * @param productDTO of type ProductDTO
     */
    @Override
    public Product create(ProductDTO productDTO) throws ProductInvalidException {
        Product product = new Product();

        product.setTitle(productDTO.getTitle());
        product.setDescription(productDTO.getDescription());
        product.setCost(productDTO.getCost());
        product.setMaxSold(productDTO.getMaxSold());
        product.setMaxSoldPerCustomer(productDTO.getMaxSoldPerCustomer());

        return this.create(product);
    }

    /**
     * Update Product using a Product
     *
     * @param product Product containing the new product information
     */
    @Override
    public void update(Product product) throws ProductNotFoundException, ProductInvalidException {
        Product model = this.getByKey(product.getKey());
        this.updateLinkedProducts(model.getProducts(), false);

        model.setTitle(product.getTitle());
        model.setDescription(product.getDescription());
        model.setCost(product.getCost());
        model.setMaxSold(product.getMaxSold());
        model.setSellStart(product.getSellStart());
        model.setSellEnd(product.getSellEnd());
        model.setProducts(product.getProducts());
        model.setMaxSoldPerCustomer(product.getMaxSoldPerCustomer());

        this.assertIsValidProduct(product);
        this.updateLinkedProducts(model.getProducts(), true);
        productRepository.save(model);
    }

    /**
     * Remove a Product
     *
     * @param product Product to be deleted.
     */
    @Override
    public void delete(Product product) {
        if (product.isLinked()) {
            throw new ProductAlreadyLinkedException();
        }

        productRepository.delete(product);
    }

    /**
     * Update the linked status of Products
     *
     * @param products List of Products
     * @param linked   linked status
     */
    private void updateLinkedProducts(List<Product> products, boolean linked) {
        products.forEach(p -> {
            p.setLinked(linked);
            productRepository.save(p);
        });
    }

    /**
     * Method assertIsValidProduct ...
     *
     * @param product of type Product
     */
    private void assertIsValidProduct(Product product) throws ProductInvalidException {
        if (product.getTitle() == null || product.getTitle().equals("")) {
            throw new ProductInvalidException("Title is required, and therefore should be filled in!");
        }

        if (product.getSellStart() == null) {
            throw new ProductInvalidException("Starting date for selling is required, and therefore should be filled in!");
        }

        if (product.getSellStart() != null && product.getSellEnd() != null) {
            if (product.getSellStart().isAfter(product.getSellEnd())) {
                throw new ProductInvalidException("Starting date for selling should be before the ending time");
            }
        }

        if (product.getCost() == null) {
            throw new ProductInvalidException("Price is required, and therefore should be filled in!");
        }

        if (product.getProducts().stream().distinct().count() != product.getProducts().size()) {
            throw new ProductInvalidException("It is not possible to add the same product twice or more!");
        }

        if (product.getMaxSoldPerCustomer() == null || product.getMaxSoldPerCustomer() < 1 ||
                product.getMaxSoldPerCustomer() > 25) {
            throw new ProductInvalidException("Max sold per customer should be between 1 and 25!");
        }
    }
}
