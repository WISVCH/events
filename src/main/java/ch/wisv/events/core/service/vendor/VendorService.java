package ch.wisv.events.core.service.vendor;

import ch.wisv.events.core.model.sales.Vendor;

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
public interface VendorService {

    /**
     * Method getAll returns the all of this VendorService object.
     *
     * @return the all (type List<Vendor>) of this VendorService object.
     */
    List<Vendor> getAll();


    /**
     * Method getAllByLDAPGroup get list of vendors by ldap group.
     *
     * @param ldapEnum of type LDAPGroupEnum
     * @return List<Vendor>
     */
    List<Vendor> getAllByLDAPGroup(String ldapEnum);

    /**
     * Method getByKey will return a Vendor by its key.
     *
     * @param key of type String
     * @return Vendor
     */
    Vendor getByKey(String key);

    /**
     * Method create will create a new Vendor.
     *
     * @param vendor of type Vendor
     */
    void create(Vendor vendor);

    /**
     * Method update will update an existing Vendor.
     *
     * @param vendor of type Vendor
     */
    void update(Vendor vendor);

    /**
     * Method delete will delete an existing Vendor.
     *
     * @param vendor of type Vendor
     */
    void delete(Vendor vendor);

}
