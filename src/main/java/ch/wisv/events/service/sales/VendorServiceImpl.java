package ch.wisv.events.service.sales;

import ch.wisv.events.data.model.sales.Vendor;
import ch.wisv.events.exception.VendorNotFoundException;
import ch.wisv.events.repository.sales.VendorRepository;
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
public class VendorServiceImpl implements VendorService {

    /**
     * VendorRepository.
     */
    private final VendorRepository vendorRepository;

    /**
     * @param vendorRepository
     */
    public VendorServiceImpl(VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    /**
     * @param vendor
     */
    @Override
    public void addVendor(Vendor vendor) {
        vendorRepository.saveAndFlush(vendor);
    }

    /**
     * @return
     */
    @Override
    public List<Vendor> getAllSellAccess() {
        return vendorRepository.findAll();
    }

    @Override
    public Vendor getVendorByKey(String key) {
        Optional<Vendor> optional = vendorRepository.findByKey(key);
        if (optional.isPresent()) {
            return optional.get();
        }
        throw new VendorNotFoundException("Vendor with key " + key + " not found");
    }

    @Override
    public Vendor getVendorById(Long id) {
        Optional<Vendor> optional = vendorRepository.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        }
        throw new VendorNotFoundException("Vendor with id " + id + " not found");
    }

    @Override
    public void updateVendor(Vendor model) {
        Vendor vendor = this.getVendorByKey(model.getKey());

        vendor.setLdapGroup(model.getLdapGroup());
        vendor.setStartingTime(model.getStartingTime());
        vendor.setEndingTime(model.getEndingTime());
        vendor.setEvents(model.getEvents());

        vendorRepository.save(vendor);
    }

    @Override
    public void deleteVendor(Vendor vendor) {
        vendorRepository.delete(vendor);
    }
}
