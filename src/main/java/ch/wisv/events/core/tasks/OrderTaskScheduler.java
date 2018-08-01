package ch.wisv.events.core.tasks;

import ch.wisv.events.core.exception.normal.EventsException;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.service.order.OrderService;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.ArrayUtils;

/**
 * OrderTaskScheduler class.
 */
@Component
@Slf4j
public class OrderTaskScheduler {

    /** Cancel task interval in seconds (10 minutes). */
    private static final int CANCEL_RESERVATION_TASK_INTERVAL_SECONDS = 600;

    /** Clean up task interval in seconds (30 minutes). */
    private static final int CLEAN_UP_TASK_INTERVAL_SECONDS = 1800;

    /** Max number of days a reservation is valid. */
    private static final int MAX_RESERVATION_DAYS = 3;

    /** Amount of milli seconds in a seconds. */
    private static final int MILLISEC_IN_SEC = 1000;

    /** OrderService. */
    private final OrderService orderService;

    /**
     * OrderTaskScheduler constructor.
     *
     * @param orderService of type OrderService
     */
    public OrderTaskScheduler(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Cancel all overdue reservation.
     */
    @Scheduled(fixedRate = CANCEL_RESERVATION_TASK_INTERVAL_SECONDS * MILLISEC_IN_SEC)
    public void cancelReservationTask() {
        orderService.getAllReservations().forEach(order -> {
            if (order.getCreatedAt().isBefore(LocalDateTime.now().minusDays(MAX_RESERVATION_DAYS))) {
                try {
                    orderService.updateOrderStatus(order, OrderStatus.REJECTED);
                } catch (EventsException e) {
                    log.warn(e.getMessage());
                }
            }
        });
    }

    /**
     * Clean up order.
     */
    @Scheduled(fixedRate = CLEAN_UP_TASK_INTERVAL_SECONDS * MILLISEC_IN_SEC)
    public void cleanUpTask() {
        orderService.getAllOrders().forEach(order -> {
            OrderStatus[] cleanUpStatus = new OrderStatus[]{OrderStatus.ANONYMOUS, OrderStatus.ASSIGNED, OrderStatus.CANCELLED, OrderStatus.PENDING};

            if (ArrayUtils.contains(cleanUpStatus, order.getStatus())) {
                orderService.delete(order);
            }
        });
    }
}
