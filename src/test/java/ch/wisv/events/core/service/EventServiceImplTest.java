package ch.wisv.events.core.service;

import ch.wisv.events.ServiceTest;
import ch.wisv.events.core.exception.normal.EventInvalidException;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.event.EventStatus;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.repository.EventRepository;
import ch.wisv.events.core.service.event.EventService;
import ch.wisv.events.core.service.event.EventServiceImpl;
import ch.wisv.events.core.service.product.ProductService;
import com.google.common.collect.ImmutableList;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.any;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * EventService test.
 */
public class EventServiceImplTest extends ServiceTest {

    @Mock
    private EventRepository repository;

    @Mock
    private ProductService productService;

    private EventService service;

    private Event event;

    @Before
    public void setUp() {
        this.service = new EventServiceImpl(repository, productService);

        this.event = new Event(
                "Test",
                "test",
                "test",
                10,
                10,
                "path/to/files",
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                "Short description"
        );
    }

    @After
    public void tearDown() {
        this.event = null;
    }

    @Test
    public void testGetAllEvents() {
        when(repository.findAll()).thenReturn(Collections.singletonList(this.event));

        assertEquals(ImmutableList.of(this.event), service.getAll());
    }

    @Test
    public void testGetAllEventsEmpty() {
        when(repository.findAll()).thenReturn(ImmutableList.of());

        assertEquals(ImmutableList.of(), service.getAll());
    }

    @Test
    public void testGetAllBetween() {
        when(repository.findAllByStartIsAfterAndStartIsBefore(
                any(LocalDateTime.class),
                any(LocalDateTime.class)
        )).thenReturn(ImmutableList.of(this.event));

        assertEquals(ImmutableList.of(this.event), service.getAllBetween(LocalDateTime.now(), LocalDateTime.now()));
    }

    @Test
    public void testGetUpcomingEvents() {
        this.event.setPublished(EventStatus.PUBLISHED);
        when(repository.findByEndingAfterOrderByStartAsc(any(LocalDateTime.class))).thenReturn(ImmutableList.of(this.event));

        assertEquals(ImmutableList.of(this.event), service.getUpcoming());
    }

    @Test
    public void testGetUpcomingEventsEmpty() {
        this.event.setPublished(EventStatus.PUBLISHED);
        when(repository.findByEndingAfterOrderByStartAsc(any(LocalDateTime.class))).thenReturn(ImmutableList.of());

        assertEquals(ImmutableList.of(), service.getUpcoming());
    }

    @Test
    public void testGetUpcomingEventsNotPublished() {
        this.event.setPublished(EventStatus.NOT_PUBLISHED);
        when(repository.findByEndingAfterOrderByStartAsc(any(LocalDateTime.class))).thenReturn(ImmutableList.of(this.event));

        assertEquals(ImmutableList.of(), service.getUpcoming());
    }

    @Test
    public void testGetPreviousEventsLastTwoWeeks() {
        when(repository.findAllByEndingBetween(any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(ImmutableList.of(this.event));

        assertEquals(ImmutableList.of(this.event), service.getPreviousEventsLastTwoWeeks());
    }

    @Test
    public void testCreate() throws EventInvalidException {
        service.create(this.event);

        Mockito.verify(repository, times(1)).saveAndFlush(this.event);
    }

    @Test
    public void testCreateInvalidTitle() throws EventInvalidException {
        this.event.setTitle(null);
        thrown.expect(EventInvalidException.class);
        thrown.expectMessage("Title is required, and therefore should be filled in!");

        service.create(this.event);
    }

    @Test
    public void testCreateInvalidShortDescription() throws EventInvalidException {
        this.event.setShortDescription(null);
        thrown.expect(EventInvalidException.class);
        thrown.expectMessage("Short description is required, and therefore should be filled in!");

        service.create(this.event);
    }

    @Test
    public void testCreateInvalidDescription() throws EventInvalidException {
        this.event.setDescription(null);
        thrown.expect(EventInvalidException.class);
        thrown.expectMessage("Description is required, and therefore should be filled in!");

        service.create(this.event);
    }

    @Test
    public void testCreateInvalidStart() throws EventInvalidException {
        this.event.setStart(null);
        thrown.expect(EventInvalidException.class);
        thrown.expectMessage("Starting time is required, and therefore should be filled in!");

        service.create(this.event);
    }

    @Test
    public void testCreateInvalidEnding() throws EventInvalidException {
        this.event.setEnding(null);
        thrown.expect(EventInvalidException.class);
        thrown.expectMessage("Ending time is required, and therefore should be filled in!");

        service.create(this.event);
    }

    @Test
    public void testCreateInvalidEndingBeforeStart() throws EventInvalidException {
        this.event.setEnding(LocalDateTime.now().minusDays(1));
        thrown.expect(EventInvalidException.class);
        thrown.expectMessage("Starting time should be before the ending time");

        service.create(this.event);
    }

    @Test
    public void testCreateInvalidTarget() throws EventInvalidException {
        this.event.setTarget(null);
        thrown.expect(EventInvalidException.class);
        thrown.expectMessage("Target is required, and therefore should be filled in!");

        service.create(this.event);
    }

    @Test
    public void testCreateInvalidLimitSmallerThanTarget() throws EventInvalidException {
        this.event.setTarget(20);
        this.event.setMaxSold(15);
        thrown.expect(EventInvalidException.class);
        thrown.expectMessage("Limit should be greater or equal to the target!");

        service.create(this.event);
    }

    @Test
    public void testCreateInvalidDoubleProduct() throws EventInvalidException {
        Product product = new Product();
        this.event.addProduct(product);
        this.event.addProduct(product);

        thrown.expect(EventInvalidException.class);
        thrown.expectMessage("It is not possible to add the same product twice or more!");

        service.create(this.event);
    }

    @Test
    public void testGetByKey() throws Exception {
        when(repository.findByKey(this.event.getKey())).thenReturn(Optional.of(this.event));

        assertEquals(this.event, service.getByKey(this.event.getKey()));
    }

    @Test
    public void testGetByKeyException() throws Exception {
        when(repository.findByKey(this.event.getKey())).thenReturn(Optional.empty());

        thrown.expect(Exception.class);
        thrown.expectMessage("Event with key " + this.event.getKey() + " not found!");

        service.getByKey(this.event.getKey());
    }

    @Test
    public void testUpdateEvent() throws Exception {
        when(repository.findByKey(this.event.getKey())).thenReturn(Optional.of(this.event));

        service.update(this.event);
        verify(repository, times(1)).save(this.event);
    }

    @Test
    public void testDelete() {
        service.delete(this.event);
        verify(repository, times(1)).delete(this.event);
    }

    @Test
    public void testGetEventByProductKey() throws Exception {
        Product product = new Product();
        this.event.addProduct(product);

        this.event.setPublished(EventStatus.PUBLISHED);
        when(repository.findByProductsContaining(any(Product.class))).thenReturn(Optional.of(this.event));

        assertEquals(this.event, service.getByProduct(product));
    }
}