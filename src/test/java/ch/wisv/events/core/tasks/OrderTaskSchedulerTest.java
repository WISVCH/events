package ch.wisv.events.core.tasks;

import ch.wisv.events.ServiceTest;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.service.order.OrderService;
import com.google.common.collect.ImmutableList;
import java.time.LocalDateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * OrderTaskScheduler test.
 */
public class OrderTaskSchedulerTest extends ServiceTest {

    @Mock
    private OrderService orderService;

    private OrderTaskScheduler orderTaskScheduler;

    /**
     * Set up.
     */
    @Before
    public void setUp() {
        this.orderTaskScheduler = new OrderTaskScheduler(orderService);
    }

    /**
     * Tear down.
     */
    @After
    public void tearDown() {
        this.orderTaskScheduler = null;
    }

    /**
     * Test cancelReservationTask
     *
     * @throws Exception when something is wrong
     */
    @Test
    public void testCancelReservationTask() throws Exception {
        Order order1 = new Order();
        order1.setCreatedAt(LocalDateTime.now().minusDays(4));

        Order order2 = new Order();
        order2.setCreatedAt(LocalDateTime.now().minusDays(3).plusHours(1));

        when(orderService.getAllReservations()).thenReturn(ImmutableList.of(order1, order2));
        doNothing().when(orderService).updateOrderStatus(order1, OrderStatus.EXPIRED);
        doNothing().when(orderService).updateOrderStatus(order2, OrderStatus.EXPIRED);

        orderTaskScheduler.cancelReservationTask();

        verify(orderService, times(1)).updateOrderStatus(order1, OrderStatus.EXPIRED);
        verify(orderService, times(0)).updateOrderStatus(order2, OrderStatus.EXPIRED);
    }

    /**
     * Test cleanUpTask.
     */
    @Test
    public void testCleanUpTask() {
        Order order1 = new Order();
        order1.setStatus(OrderStatus.ANONYMOUS);
        order1.setCreatedAt(LocalDateTime.now().minusMinutes(61));

        Order order2 = new Order();
        order2.setStatus(OrderStatus.ANONYMOUS);
        order2.setCreatedAt(LocalDateTime.now());

        when(orderService.getAllOrders()).thenReturn(ImmutableList.of(order1, order2));
        doNothing().when(orderService).delete(order1);
        doNothing().when(orderService).delete(order2);

        orderTaskScheduler.cleanUpTask();

        verify(orderService, times(1)).delete(order1);
        verify(orderService, times(0)).delete(order2);
    }
}