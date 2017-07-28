package ch.wisv.events.core.service;

import ch.wisv.events.EventsApplicationTest;
import ch.wisv.events.core.exception.InvalidVendorException;
import ch.wisv.events.core.exception.VendorNotFoundException;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.sales.Vendor;
import ch.wisv.events.core.repository.VendorRepository;
import ch.wisv.events.core.service.vendor.VendorService;
import ch.wisv.events.core.service.vendor.VendorServiceImpl;
import ch.wisv.events.utils.LDAPGroupEnum;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

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
public class VendorServiceTest extends ServiceTest {

    /**
     * Mock of the VendorRepository
     */
    @Mock
    private VendorRepository repository;

    /**
     * VendorService with the Mock of the VendorRepository
     */
    @InjectMocks
    private VendorService vendorService = new VendorServiceImpl();

    /**
     * Default instance of the Vendor class
     */
    private Vendor vendor;

    /**
     * Method setUp create default Vendor class instance
     *
     * @throws Exception when
     */
    @Before
    public void setUp() throws Exception {
        vendor = new Vendor();

        vendor.setLdapGroup(LDAPGroupEnum.BEHEER);
        vendor.setEvents(Collections.singletonList(Mockito.mock(Event.class)));
        vendor.setStartingTime(LocalDateTime.now());
        vendor.setEndingTime(LocalDateTime.now());
    }

    /**
     * Method tearDown set Vendor class to null
     *
     * @throws Exception when
     */
    @After
    public void tearDown() throws Exception {
        vendor = null;
    }

    /**
     * Test if the getAll method returns the right amount of objects.
     *
     * @throws Exception when
     */
    @Test
    public void getAll() throws Exception {
        when(repository.findAll()).thenReturn(Collections.singletonList(vendor));

        assertEquals(1, vendorService.getAll().size());
    }

    /**
     * Test if the getByKey method return the right element
     *
     * @throws Exception when
     */
    @Test
    public void getByKey() throws Exception {
        when(repository.findByKey(vendor.getKey())).thenReturn(Optional.of(vendor));
        Vendor temp = vendorService.getByKey(vendor.getKey());

        assertEquals(vendor, temp);
    }

    /**
     * Test if the getByKey method throws a VendorNotFoundException when there is no Vendor with the given key.
     *
     * @throws Exception when
     */
    @Test
    public void getByKeyNotFound() throws Exception {
        when(repository.findByKey(Mockito.anyString())).thenReturn(Optional.empty());

        String key = "123";
        thrown.expect(VendorNotFoundException.class);
        thrown.expectMessage("Vendor with key " + key + " not found");

        vendorService.getByKey(key);
    }

    /**
     * Test if the getByKey method throws a VendorNotFoundException when there is no Vendor when the key is null.
     *
     * @throws Exception when
     */
    @Test
    public void getByKeyNull() throws Exception {
        when(repository.findByKey(null)).thenReturn(Optional.empty());
        thrown.expect(VendorNotFoundException.class);
        thrown.expectMessage("Vendor with key " + null + " not found");

        vendorService.getByKey(null);
    }

    /**
     * Test if the create method has the right behaviour by calling the save and flush function ones.
     *
     * @throws Exception when
     */
    @Test
    public void add() throws Exception {
        Vendor temp = new Vendor();
        temp.setEvents(Collections.singletonList(Mockito.mock(Event.class)));
        temp.setLdapGroup(LDAPGroupEnum.BEHEER);
        temp.setStartingTime(LocalDateTime.now());
        temp.setEndingTime(LocalDateTime.now());

        vendorService.create(temp);
        verify(repository, times(1)).saveAndFlush(Mockito.any(Vendor.class));
    }

    /**
     * Test if the create method throws an InvalidVendorException when the key or the LDAP group is empty.
     *
     * @throws Exception when
     */
    @Test
    public void addEmptyKeyOrLDAPGroup() throws Exception {
        Vendor temp = new Vendor();
        temp.setKey(null);
        thrown.expect(InvalidVendorException.class);
        thrown.expectMessage("Key is empty, but is a required field, so please fill in this field!");
        vendorService.create(temp);

        temp.setKey(UUID.randomUUID().toString());
        thrown.expectMessage("Ldap group is empty, but is a required field, so please fill in this field!");
        vendorService.create(temp);
    }

    /**
     * Test if the create method throws an InvalidVendorException when trying to create null.
     *
     * @throws Exception when
     */
    @Test
    public void addNull() throws Exception {
        thrown.expect(InvalidVendorException.class);

        vendorService.create(null);
    }

    /**
     * Test if the update method show the right behavior.
     *
     * @throws Exception when
     */
    @Test
    public void update() throws Exception {
        when(repository.findByKey(Mockito.anyString())).thenReturn(Optional.of(vendor));

        vendorService.update(vendor);
        verify(repository, times(1)).save(Mockito.any(Vendor.class));
    }

    /**
     * Test if the update method throws an InvalidVendorException when trying to update null
     *
     * @throws Exception when
     */
    @Test
    public void updateNull() throws Exception {
        thrown.expect(InvalidVendorException.class);
        thrown.expectMessage("Vendor can not be null!");

        vendorService.update(null);
    }

    /**
     * Test if the delete function has the right behaviour.
     *
     * @throws Exception when
     */
    @Test
    public void delete() throws Exception {
        vendorService.delete(vendor);

        verify(repository, times(1)).delete(vendor);
    }

}