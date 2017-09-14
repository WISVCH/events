package ch.wisv.events.sales.order.service;

import ch.wisv.events.core.exception.EventsSalesAppException;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.repository.SoldProductRepository;
import org.springframework.stereotype.Service;

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
public class SalesAppSoldProductServiceImpl implements SalesAppSoldProductService {

    /**
     * Field soldProductRepository
     */
    private final SoldProductRepository soldProductRepository;

    /**
     * Constructor SalesAppEventServiceImpl creates a new SalesAppEventServiceImpl instance.
     *
     * @param soldProductRepository of type SoldProductRepository
     */
    public SalesAppSoldProductServiceImpl(SoldProductRepository soldProductRepository) {
        this.soldProductRepository = soldProductRepository;
    }

    /**
     * Method assertAmountOfProductLeft.
     *
     * @param product of type Product
     * @param integer of type Integer
     */
    @Override
    public void assertAmountOfProductLeft(Product product, Long integer) {
        Long numberProductSold = this.soldProductRepository.countAllByProduct(product);
        
        if (product.getMaxSold() != null) {
            int numberRemainingProduct = product.getMaxSold() - numberProductSold.intValue();

            if (numberRemainingProduct < integer.intValue()) {
                throw new EventsSalesAppException("Not enough tickets left for " + product.getTitle() + " only " +
                        numberRemainingProduct + " tickets left.");
            }
        }
    }
}
