package ch.wisv.events.scan.service;

import ch.wisv.events.core.exception.normal.SoldProductNotFoundException;
import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.order.SoldProduct;
import ch.wisv.events.core.model.order.SoldProductStatus;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.repository.SoldProductRepository;
import ch.wisv.events.scan.object.ScanResult;
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
public class ScanAppSoldProductServiceImpl implements ScanAppSoldProductService {

    /**
     * Field soldProductRepository
     */
    private final SoldProductRepository soldProductRepository;

    /**
     * Constructor ScanAppSoldProductServiceImpl creates a new ScanAppSoldProductServiceImpl instance.
     *
     * @param soldProductRepository of type SoldProductRepository
     */
    public ScanAppSoldProductServiceImpl(SoldProductRepository soldProductRepository) {
        this.soldProductRepository = soldProductRepository;
    }

    /**
     * Method getAllByProductAndCustomer ...
     *
     * @param product  of type Product
     * @param customer of type Customer
     * @return List<SoldProduct>
     */
    @Override
    public List<SoldProduct> getAllByProductAndCustomer(Product product, Customer customer) {
        return this.soldProductRepository.findAllByProductAndCustomerAndStatusNotIn(product, customer, null);
    }

    /**
     * Method getByProductAndUniqueCode ...
     *
     * @param product    of type Product
     * @param uniqueCode of type String
     * @return SoldProduct
     */
    @Override
    public SoldProduct getByProductAndUniqueCode(Product product, String uniqueCode) throws SoldProductNotFoundException {
        Optional<SoldProduct> soldProduct = this.soldProductRepository.findByProductAndUniqueCode(product, uniqueCode);

        return soldProduct.orElseThrow(() -> new SoldProductNotFoundException("Model does not exist"));
    }

    /**
     * Method scanByProductAndUniqueCode ...
     *
     * @param product    of type Product
     * @param uniqueCode of type String
     * @return ScanResult
     */
    @Override
    public ScanResult scanByProductAndUniqueCode(Product product, String uniqueCode) {
        try {
            SoldProduct soldProduct = this.getByProductAndUniqueCode(product, uniqueCode);

            return this.scanSoldProduct(soldProduct);
        } catch (SoldProductNotFoundException e) {
            return ScanResult.PRODUCT_NOT_EXISTS;
        }
    }

    /**
     * Method scanSoldProduct ...
     *
     * @param soldProduct of type SoldProduct
     * @return ScanResult
     */
    @Override
    public ScanResult scanSoldProduct(SoldProduct soldProduct) {
        if (soldProduct.getStatus() == SoldProductStatus.OPEN) {
            soldProduct.setStatus(SoldProductStatus.SCANNED);
            this.soldProductRepository.save(soldProduct);

            return ScanResult.SUCCESSFUL;
        } else {
            return ScanResult.ALREADY_SCANNED;
        }
    }

    /**
     * Method scanProductWithRfid ...
     *
     * @param product  of type Product
     * @param customer of type String
     * @return ScanResult
     */
    @Override
    public ScanResult scanByProductAndCustomer(Product product, Customer customer) {
        List<SoldProduct> soldProductList = this.getAllByProductAndCustomer(product, customer);

        if (soldProductList.size() == 1) {
            if (soldProductList.get(0).getStatus() == SoldProductStatus.OPEN) {
                soldProductList.get(0).setStatus(SoldProductStatus.SCANNED);
                this.soldProductRepository.save(soldProductList);

                return ScanResult.SUCCESSFUL;
            } else {
                return ScanResult.ALREADY_SCANNED;
            }
        } else if (soldProductList.size() == 0) {
            return ScanResult.PRODUCT_NOT_EXISTS;
        } else {
            return ScanResult.MULTIPLE_PRODUCT;
        }
    }
}
