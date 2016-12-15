package ch.wisv.events.core.service.vendor;

import ch.wisv.events.EventsApplicationTest;
import ch.wisv.events.core.exception.InvalidVendorException;
import ch.wisv.events.core.exception.VendorNotFoundException;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.sales.Vendor;
import ch.wisv.events.utils.LDAPGroupEnum;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

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
@RunWith(SpringRunner.class)
@SpringBootTest(classes = EventsApplicationTest.class)
@ActiveProfiles("test")
@DataJpaTest
public class VendorServiceTest {

    @Autowired
    private VendorService vendorService;

    @Autowired
    private TestEntityManager testEntityManager;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Vendor vendor;

    @Before
    public void setUp() throws Exception {
        vendor = new Vendor();
        vendor.setLdapGroup(LDAPGroupEnum.AKCIE);
        vendor.setEvents(Mockito.anyListOf(Event.class));

        testEntityManager.persist(vendor);
    }

    @After
    public void tearDown() throws Exception {
        vendor = null;
    }

    @Test
    public void getAll() throws Exception {
        assertEquals(1, vendorService.getAll().size());
    }

    @Test
    public void getByKey() throws Exception {
        Vendor temp = vendorService.getByKey(vendor.getKey());

        assertEquals(vendor, temp);
    }

    @Test
    public void getByKeyNotFound() throws Exception {
        String key = "123";
        thrown.expect(VendorNotFoundException.class);
        thrown.expectMessage("Vendor with key " + key + " not found");

        vendorService.getByKey(key);
    }

    @Test
    public void getByKeyNull() throws Exception {
        thrown.expect(VendorNotFoundException.class);
        thrown.expectMessage("Vendor with key " + null + " not found");

        vendorService.getByKey(null);
    }

    @Test
    public void add() throws Exception {
        Vendor temp = new Vendor();
        temp.setEvents(Mockito.anyListOf(Event.class));
        temp.setLdapGroup(LDAPGroupEnum.BEHEER);
        temp.setStartingTime(Mockito.any(LocalDateTime.class));
        temp.setEndingTime(Mockito.any(LocalDateTime.class));

        vendorService.add(temp);
        assertEquals(2, vendorService.getAll().size());
        assertEquals(temp, vendorService.getByKey(temp.getKey()));
    }

    @Test
    public void addEmptyKeyOrLDAPGroup() throws Exception {
        Vendor temp = new Vendor();
        temp.setKey(null);
        thrown.expect(InvalidVendorException.class);
        thrown.expectMessage("Key is empty, but is a required field, so please fill in this field!");
        vendorService.add(temp);

        temp.setKey(UUID.randomUUID().toString());
        thrown.expectMessage("Ldap group is empty, but is a required field, so please fill in this field!");
        vendorService.add(temp);
    }

    @Test
    public void addNull() throws Exception {
        thrown.expect(InvalidVendorException.class);

        vendorService.add(null);
    }

    @Test
    public void update() throws Exception {
        vendor.setLdapGroup(LDAPGroupEnum.CHIPCIE);
        vendor.setEvents(Mockito.anyListOf(Event.class));
        vendor.setStartingTime(LocalDateTime.now());
        vendor.setEndingTime(LocalDateTime.now());

        vendorService.update(vendor);
        assertEquals(vendor, vendorService.getByKey(vendor.getKey()));
    }

    @Test
    public void updateNull() throws Exception {
        thrown.expect(InvalidVendorException.class);
        thrown.expectMessage("Vendor can not be null!");

        vendorService.update(null);
    }

    @Test
    public void delete() throws Exception {
        vendorService.delete(vendor);
        assertEquals(0, vendorService.getAll().size());
    }

}