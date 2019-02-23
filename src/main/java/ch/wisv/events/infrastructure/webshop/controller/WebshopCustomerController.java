package ch.wisv.events.infrastructure.webshop.controller;

import ch.wisv.events.domain.model.order.Order;
import ch.wisv.events.domain.model.order.OrderStatus;
import static ch.wisv.events.infrastructure.webshop.util.WebshopConstant.ROUTE_WEBSHOP_CUSTOMER;
import static ch.wisv.events.infrastructure.webshop.util.WebshopConstant.ROUTE_WEBSHOP_OPTION_PUBLIC_REFERENCE;
import ch.wisv.events.services.OrderService;
import static java.util.Objects.nonNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * WebshopCustomerController class.
 */
@Controller
@RequestMapping(ROUTE_WEBSHOP_CUSTOMER)
public class WebshopCustomerController extends AbstractWebshopController {

    /** OrderService. */
    private final OrderService orderService;

    /**
     * WebshopCustomerController constructor.
     *
     * @param orderService of type OrderService
     */
    public WebshopCustomerController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * @param publicReference of type String
     *
     * @return String
     */
    @GetMapping(ROUTE_WEBSHOP_OPTION_PUBLIC_REFERENCE)
    public String function(@PathVariable String publicReference) {
        Order order = orderService.getByPublicReference(publicReference);
        if (order.getStatus() != OrderStatus.ANONYMOUS && nonNull(order.getCustomer())) {

        }

        return "";
    }
}
