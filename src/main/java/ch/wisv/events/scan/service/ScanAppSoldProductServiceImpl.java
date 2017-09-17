package ch.wisv.events.scan.service;

import ch.wisv.events.core.exception.EventsModelNotFound;
import ch.wisv.events.core.model.order.Customer;
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

    public ScanAppSoldProductServiceImpl(SoldProductRepository soldProductRepository) {
        this.soldProductRepository = soldProductRepository;
    }

    @Override
    public List<SoldProduct> getAllByProductAndCustomer(Product product, Customer customer) {
        return this.soldProductRepository.findAllByProductAndCustomer(product, customer);
    }

    @Override
    public SoldProduct getByProductAndUniqueCode(Product product, String uniqueCode) {
        Optional<SoldProduct> soldProduct = this.soldProductRepository.findByProductAndUniqueCode(product, uniqueCode);

        return soldProduct.orElseThrow(() -> new EventsModelNotFound("Model does not exist"));
    }

    @Override
    public ScanResult scanProductWithUniqueCode(Product product, String uniqueCode) {
        try {
            SoldProduct soldProduct = this.getByProductAndUniqueCode(product, uniqueCode);

            if (soldProduct.getStatus() == SoldProductStatus.OPEN) {
                soldProduct.setStatus(SoldProductStatus.SCANNED);
                this.soldProductRepository.saveAndFlush(soldProduct);

                return ScanResult.SUCCESSFUL;
            } else {
                return ScanResult.ALREADY_SCANNED;
            }
        } catch (EventsModelNotFound e) {
            return ScanResult.PRODUCT_NOT_EXISTS;
        }
    }
}
