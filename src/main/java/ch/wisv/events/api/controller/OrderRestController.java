package ch.wisv.events.api.controller;

import ch.wisv.events.core.exception.normal.EventsException;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.service.order.OrderService;
import ch.wisv.events.webshop.service.PaymentsService;
import java.util.LinkedHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * OrderRestController class.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
public class OrderRestController {

    /** OrderService. */
    private final OrderService orderService;

    /** PaymentsService. */
    private final PaymentsService paymentsService;

    /**
     * OrderRestController constructor.
     *
     * @param orderService    of type OrderService
     * @param paymentsService of type PaymentsService
     */
    public OrderRestController(OrderService orderService, PaymentsService paymentsService) {
        this.orderService = orderService;
        this.paymentsService = paymentsService;
    }

    /**
     * This endpoint is for the CH Payments.
     *
     * @param body of type LinkedHashMap
     *
     * @return ResponseEntity
     */
    @PostMapping("/status")
    public ResponseEntity updateOrderStatus(@RequestBody LinkedHashMap<String, Object> body) {
        try {
            String publicReference = (String) body.getOrDefault("publicReference", "");
            Order order = orderService.getByChPaymentsReference(publicReference);
            String paymentsStatus = paymentsService.getPaymentsOrderStatus(order.getChPaymentsReference());

            OrderStatus status = paymentsService.mapStatusToOrderStatus(paymentsStatus);
            orderService.updateOrderStatus(order, status);

            return new ResponseEntity(HttpStatus.OK);
        } catch (EventsException $e) {
            log.error($e.getMessage());

            return new ResponseEntity(HttpStatus.CONFLICT);
        }
    }
}
