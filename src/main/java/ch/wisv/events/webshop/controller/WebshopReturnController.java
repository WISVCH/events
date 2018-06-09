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
import org.thymeleaf.util.ArrayUtils;

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
     * @param redirect of type RedirectAttributes
     * @param key      of type String
     *
     * @return String
     */
    @GetMapping
    public String returnIndex(RedirectAttributes redirect, @PathVariable String key) {
        try {
            Order order = orderService.getByReference(key);
            switch (order.getStatus()) {
                case PAID:
                    return "redirect:/return/" + order.getPublicReference() + "/success";
                case CANCELLED:
                    return "redirect:/return/" + order.getPublicReference() + "/cancelled";
                case ERROR:
                    return "redirect:/return/" + order.getPublicReference() + "/error";
                case RESERVATION:
                    return "redirect:/return/" + order.getPublicReference() + "/reservation";
                default:
                    return "redirect:/return/" + order.getPublicReference() + "/error";
            }
        } catch (OrderNotFoundException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/";
        }
    }

    /**
     * Return page depending on status.
     *
     * @param model    of type Model
     * @param redirect of type RedirectAttributes
     * @param key      of type String
     * @param status   of type String
     *
     * @return String
     */
    @GetMapping("/{status}")
    public String returnStatus(Model model, RedirectAttributes redirect, @PathVariable String key, @PathVariable String status) {
        try {
            Order order = orderService.getByReference(key);
            model.addAttribute("order", order);

            String[] validStatus = new String[]{"success", "cancelled", "error", "reservation"};

            if (ArrayUtils.contains(validStatus, status)) {
                return "webshop/return/" + status;
            }

            return "webshop/return/error";
        } catch (OrderNotFoundException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/";
        }
    }
}
