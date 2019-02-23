package ch.wisv.events.infrastructure.webshop.controller;

import static ch.wisv.events.infrastructure.webshop.util.WebshopConstant.MODEL_ATTR_ORDER;
import static ch.wisv.events.infrastructure.webshop.util.WebshopConstant.ROUTE_WEBSHOP_OPTION_PUBLIC_REFERENCE;
import static ch.wisv.events.infrastructure.webshop.util.WebshopConstant.ROUTE_WEBSHOP_ORDER;
import static ch.wisv.events.infrastructure.webshop.util.WebshopConstant.VIEW_WEBSHOP_CHECKOUT_ORDER;
import ch.wisv.events.services.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * WebshopIndexController class.
 */
@Controller
@RequestMapping(ROUTE_WEBSHOP_ORDER)
public class WebshopOrderController extends AbstractWebshopController {

    /** OrderService. */
    private final OrderService orderService;

    /**
     * WebshopController constructor.
     *
     * @param orderService of type OrderService
     */
    protected WebshopOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * View order string.
     *
     * @param model           of type Model
     * @param publicReference the public reference
     *
     * @return string
     */
    @GetMapping(ROUTE_WEBSHOP_OPTION_PUBLIC_REFERENCE)
    public String viewOrder(Model model, @PathVariable String publicReference) {
        model.addAttribute(MODEL_ATTR_ORDER, orderService.getByPublicReference(publicReference));

        return VIEW_WEBSHOP_CHECKOUT_ORDER;
    }
}
