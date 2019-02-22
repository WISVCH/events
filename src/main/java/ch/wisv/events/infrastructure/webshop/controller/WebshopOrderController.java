package ch.wisv.events.infrastructure.webshop.controller;

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
@RequestMapping("/webshop/order")
public class WebshopOrderController extends AbstractWebshopController {

    /** Model attribute order. */
    private static final String MODEL_ATTR_ORDER = "order";

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
    @GetMapping("/{publicReference}")
    public String viewOrder(Model model, @PathVariable String publicReference) {
        model.addAttribute(MODEL_ATTR_ORDER, orderService.getByPublicReference(publicReference));

        return "webshop/webshop-checkout-order";
    }
}
