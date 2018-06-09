package ch.wisv.events.webshop.controller;

import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.order.OrderProductDto;
import ch.wisv.events.core.service.auth.AuthenticationService;
import ch.wisv.events.core.service.event.EventService;
import ch.wisv.events.core.service.order.OrderService;
import ch.wisv.events.webshop.service.WebshopService;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * WebshopIndexController class.
 */
@Controller
public class WebshopIndexController extends WebshopController {

    /** Model attr events. */
    private static final String MODEL_ATTR_EVENTS = "events";

    /** Model attr of the OrderProductDTO. */
    private static final String MODEL_ATTR_ORDER_PRODUCT = "orderProduct";

    /** EventService. */
    private final EventService eventService;

    /** WebshopService. */
    private final WebshopService webshopService;

    /**
     * WebshopController constructor.
     *
     * @param eventService          of type EventService
     * @param webshopService        of type WebshopService
     * @param orderService          of type OrderService
     * @param authenticationService of type AuthenticationService.
     */
    protected WebshopIndexController(
            EventService eventService,
            WebshopService webshopService,
            OrderService orderService,
            AuthenticationService authenticationService
    ) {
        super(orderService, authenticationService);
        this.eventService = eventService;
        this.webshopService = webshopService;
    }

    /**
     * Front page of the webshop.
     *
     * @param model of type Model
     *
     * @return String
     */
    @GetMapping("/")
    public String index(Model model) {
        List<Event> upcoming = eventService.getUpcoming();
        model.addAttribute(MODEL_ATTR_CUSTOMER, authenticationService.getCurrentCustomer());
        model.addAttribute(MODEL_ATTR_EVENTS, webshopService.filterNotSalableProducts(upcoming));
        model.addAttribute(MODEL_ATTR_ORDER_PRODUCT, new OrderProductDto());

        return "webshop/index";
    }
}
