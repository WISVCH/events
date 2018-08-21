package ch.wisv.events.webshop.controller;

import ch.wisv.events.core.exception.normal.OrderNotFoundException;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.service.auth.AuthenticationService;
import ch.wisv.events.core.service.order.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * WebshopReturnController class.
 */
@Controller
@RequestMapping("/return/{key}")
public class WebshopReturnController extends WebshopController {

    /**
     * WebshopReturnController constructor.
     *
     * @param orderService          of type OrderService
     * @param authenticationService of type AuthenticationService
     */
    public WebshopReturnController(OrderService orderService, AuthenticationService authenticationService) {
        super(orderService, authenticationService);
    }

    /**
     * Completion page index.
     *
     * @param model    of type Model
     * @param redirect of type RedirectAttributes
     * @param key      of type String
     *
     * @return String
     */
    @GetMapping
    public String returnIndex(Model model, RedirectAttributes redirect, @PathVariable String key) {
        try {
            Order order = orderService.getByReference(key);
            model.addAttribute(MODEL_ATTR_ORDER, order);

            switch (order.getStatus()) {
                case PENDING:
                    return "webshop/return/pending";
                case EXPIRED:
                    return "webshop/return/expired";
                case PAID:
                    return "webshop/return/success";
                case CANCELLED:
                    return "webshop/return/cancelled";
                case RESERVATION:
                    return "webshop/return/reservation";
                case ERROR:
                default:
                    return "webshop/return/error";
            }
        } catch (OrderNotFoundException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/";
        }
    }
}
