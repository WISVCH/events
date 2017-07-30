package ch.wisv.events.utils.dev.data;

import ch.wisv.events.core.model.sales.Vendor;
import ch.wisv.events.core.repository.EventRepository;
import ch.wisv.events.core.repository.VendorRepository;
import ch.wisv.events.utils.LDAPGroup;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

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
@Order(value = 4)
public class VendorTestDataRunner extends TestDataRunner {

    /**
     * Field eventRepository
     */
    private final VendorRepository vendorRepository;

    /**
     * Field eventRepository
     */
    private final EventRepository eventRepository;

    /**
     * Constructor EventTestDataRunner creates a new EventTestDataRunner instance.
     *
     * @param vendorRepository of type VendorRepository
     * @param eventRepository of type EventRepository
     */
    public VendorTestDataRunner(VendorRepository vendorRepository, EventRepository eventRepository) {
        this.vendorRepository = vendorRepository;
        this.eventRepository = eventRepository;

        this.setJsonFileName("vendors.json");
    }

    /**
     * Method loop
     *
     * @param jsonObject of type JSONObject
     */
    @Override
    protected void loop(JSONObject jsonObject) {
        Vendor vendor = this.createVendor(jsonObject);

        this.vendorRepository.save(vendor);
    }

    /**
     * Method createProduct ...
     *
     * @param jsonObject of type JSONObject
     * @return Product
     */
    private Vendor createVendor(JSONObject jsonObject) {
        Vendor vendor = new Vendor();
        vendor.setLdapGroup(LDAPGroup.valueOf((String) jsonObject.get("ldapGroup")));

        return this.addEvents(vendor, (JSONArray) jsonObject.get("events"));
    }

    /**
     * Method addEvents ...
     *
     * @param vendor of type Vendor
     * @param jsonArray of type JSONArray
     * @return Vendor
     */
    private Vendor addEvents(Vendor vendor, JSONArray jsonArray) {
        for (Object o : jsonArray) {
            int eventId = ((Long) o).intValue();
            vendor.addEvent(this.eventRepository.findById(eventId));
        }

        return vendor;
    }
}
