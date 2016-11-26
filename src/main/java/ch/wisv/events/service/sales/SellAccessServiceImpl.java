package ch.wisv.events.service.sales;

import ch.wisv.events.data.model.sales.SellAccess;
import ch.wisv.events.repository.sales.SellAccessRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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
public class SellAccessServiceImpl implements SellAccessService {

    /**
     * SellAccessRepository.
     */
    private final SellAccessRepository sellAccessRepository;

    /**
     * @param sellAccessRepository
     */
    public SellAccessServiceImpl(SellAccessRepository sellAccessRepository) {
        this.sellAccessRepository = sellAccessRepository;
    }

    /**
     * @param sellAccess
     */
    @Override
    public void addSellAccess(SellAccess sellAccess) {
        sellAccessRepository.saveAndFlush(sellAccess);
    }

    /**
     * @return
     */
    @Override
    public List<SellAccess> getAllSellAccess() {
        return sellAccessRepository.findAll();
    }

}
