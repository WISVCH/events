package ch.wisv.events.api.controller;

import ch.wisv.events.core.service.order.OrderService;
import ch.wisv.events.webshop.service.PaymentsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * OrderRestController class.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
public class OrderRestController {

    /** PaymentsService. */
    private final PaymentsService paymentsService;

    /**
     * OrderRestController constructor.
     *
     * @param paymentsService of type PaymentsService
     */
    public OrderRestController(PaymentsService paymentsService) {
        this.paymentsService = paymentsService;
    }

    /**
     * This endpoint is for the paymentprovider. Webhooks will arrive here.
     *
     * @param providerReference The provider Order Reference
     *
     * @return Status Message
     */
    @RequestMapping(value = "/status", method = RequestMethod.POST)
    public ResponseEntity<HttpStatus> updateOrderStatus(@RequestParam(name = "id") String providerReference) {
        paymentsService.updateStatusByProviderReference(providerReference);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
