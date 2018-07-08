package ch.wisv.events.sales.controller.sell;

import ch.wisv.events.core.exception.normal.OrderNotFoundException;
import ch.wisv.events.core.service.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * SalesSellOrderController class.
 */
@Controller
@PreAuthorize("hasRole('USER')")
@RequestMapping(value = "/sales/sell/order/{publicReference}")
public class SalesSellOrderController {

    /** OrderService. */
    private final OrderService orderService;

    /**
     * Constructor SalesController creates a new SalesController instance.
     *
     * @param orderService of type OrderService.
     */
    @Autowired
    public SalesSellOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Method overview ...
     *
     * @param model    of type Model
     * @param redirect of type RedirectAttributes
     *
     * @return String
     */
    @GetMapping
    public String overview(Model model, RedirectAttributes redirect, @PathVariable String publicReference) {
        try {
            model.addAttribute("order", orderService.getByReference(publicReference));

            return "sales/sell/order/index";
        } catch (OrderNotFoundException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/sales/";
        }
    }

    /**
     * Method complete ...
     *
     * @param redirect        of type RedirectAttributes
     * @param publicReference of type String
     *
     * @return String
     */
    @GetMapping("/{ending}")
    public String complete(RedirectAttributes redirect, @PathVariable String publicReference, @PathVariable String ending) {
        try {
            orderService.getByReference(publicReference);

            return "sales/sell/order/" + ending;
        } catch (OrderNotFoundException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/sales/";
        }
    }
}