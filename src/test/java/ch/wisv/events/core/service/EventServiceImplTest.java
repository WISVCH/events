package ch.wisv.events.core.service;

import ch.wisv.events.core.exception.EventsModelNotFound;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.event.EventStatus;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.repository.EventRepository;
import ch.wisv.events.core.service.event.EventService;
import ch.wisv.events.core.service.event.EventServiceImpl;
import ch.wisv.events.core.service.product.ProductService;
import com.google.common.collect.ImmutableList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
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
public class EventServiceImplTest extends ServiceTest {

    @Mock
    private EventRepository repository;

    @Mock
    private ProductService productService;

    private EventService service;

    private Event event;

    @Before
    public void setUp() throws Exception {
        this.service = new EventServiceImpl(repository, productService);

        this.event = new Event(
                "Test",
                "test",
                "test",
                10,
                10,
                "path/to/files",
                LocalDateTime.now(),
                LocalDateTime.now(),
                "Short description"
        );
    }

    @After
    public void tearDown() throws Exception {
        this.event = null;
    }

    @Test
    public void testGetAllEvents() throws Exception {
        when(repository.findAll()).thenReturn(Collections.singletonList(this.event));

        assertEquals(ImmutableList.of(this.event), service.getAllEvents());
    }

    @Test
    public void testGetAllEventsEmpty() throws Exception {
        when(repository.findAll()).thenReturn(ImmutableList.of());

        assertEquals(ImmutableList.of(), service.getAllEvents());
    }

    @Test
    public void testGetUpcomingEvents() throws Exception {
        this.event.setPublished(EventStatus.PUBLISHED);
        when(repository.findByEndingAfter(any(LocalDateTime.class))).thenReturn(ImmutableList.of(this.event));

        assertEquals(ImmutableList.of(this.event), service.getUpcomingEvents());
    }

    @Test
    public void testGetUpcomingEventsEmpty() throws Exception {
        this.event.setPublished(EventStatus.PUBLISHED);
        when(repository.findByEndingAfter(any(LocalDateTime.class))).thenReturn(ImmutableList.of());

        assertEquals(ImmutableList.of(), service.getUpcomingEvents());
    }

    @Test
    public void testGetUpcomingEventsNotPublished() throws Exception {
        this.event.setPublished(EventStatus.NOT_PUBLISHED);
        when(repository.findByEndingAfter(any(LocalDateTime.class))).thenReturn(ImmutableList.of(this.event));

        assertEquals(ImmutableList.of(), service.getUpcomingEvents());
    }

    @Test
    public void testGetAvailableEvents() throws Exception {
        this.event.setPublished(EventStatus.PUBLISHED);
        when(repository.findAll()).thenReturn(Collections.singletonList(this.event));

        assertEquals(ImmutableList.of(this.event), service.getAvailableEvents());
    }

    @Test
    public void testGetAvailableEventsEmpty() throws Exception {
        when(repository.findAll()).thenReturn(ImmutableList.of());

        assertEquals(ImmutableList.of(), service.getAvailableEvents());
    }

    @Test
    public void testGetAvailableEventsNotPublished() throws Exception {
        when(repository.findAll()).thenReturn(ImmutableList.of());

        assertEquals(ImmutableList.of(), service.getAvailableEvents());
    }

    @Test
    public void testAdd() throws Exception {
        service.create(this.event);

        Mockito.verify(repository, times(1)).saveAndFlush(this.event);
    }

    @Test
    public void testGetByKey() throws Exception {
        when(repository.findByKey(this.event.getKey())).thenReturn(Optional.of(this.event));

        assertEquals(this.event, service.getByKey(this.event.getKey()));
    }

    @Test
    public void testGetByKeyException() throws Exception {
        when(repository.findByKey(this.event.getKey())).thenReturn(Optional.empty());

        thrown.expect(EventsModelNotFound.class);
        thrown.expectMessage("Event with key " + this.event.getKey() + " not found.");

        service.getByKey(this.event.getKey());
    }

    @Test
    public void testUpdateEvent() throws Exception {
        when(repository.findByKey(this.event.getKey())).thenReturn(Optional.of(this.event));

        service.update(this.event);
        verify(repository, times(1)).save(this.event);
    }

    @Test
    public void testDelete() throws Exception {
        service.delete(this.event);
        verify(repository, times(1)).delete(this.event);
    }

    @Test
    public void testGetEventByProductKey() throws Exception {
        Product product = new Product();
        this.event.addProduct(product);

        this.event.setPublished(EventStatus.PUBLISHED);
        when(repository.findAll()).thenReturn(Collections.singletonList(this.event));

        assertEquals(ImmutableList.of(this.event), service.getEventByProductKey(product.getKey()));
    }
}