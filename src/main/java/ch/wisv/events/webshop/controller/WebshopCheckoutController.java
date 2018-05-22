package ch.wisv.events.webshop.controller;

import ch.wisv.events.core.exception.normal.EventsException;
import ch.wisv.events.core.exception.normal.OrderInvalidException;
import ch.wisv.events.core.exception.normal.OrderNotFoundException;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderProductDto;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.service.order.OrderService;
import ch.wisv.events.core.service.order.OrderValidationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/checkout")
public class WebshopCheckoutController extends WebshopController {

    /** OrderValidationService. */
    private final OrderValidationService orderValidationService;

    /**
     * Constructor WebshopController.
     *
     * @param orderService           of type OrderService
     * @param orderValidationService of type OrderValidationService
     */
    public WebshopCheckoutController(OrderService orderService, OrderValidationService orderValidationService) {
        super(orderService);
        this.orderValidationService = orderValidationService;
    }

    /**
     * Post mapping for the checkout of a shopping basket.
     *
     * @param redirect        of type RedirectAttributes
     * @param orderProductDto of type OrderProductDto
     *
     * @return String
     */
    @PostMapping
    public String checkoutShoppingBasket(RedirectAttributes redirect, @ModelAttribute OrderProductDto orderProductDto) {
        try {
            if (orderProductDto.getProducts().isEmpty()) {
                redirect.addFlashAttribute("error", "Shopping basket can not be empty!");

                return "redirect:/";
            }

            Order order = orderService.createOrderByOrderProductDto(orderProductDto);
            order.setCreatedBy("events-webshop");
            orderValidationService.assertOrderIsValid(order);
            orderService.create(order);

            return "redirect:/checkout/" + order.getPublicReference();
        } catch (EventsException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/";
        }
    }

    /**
     * GetMapping for "/checkout/{key}/".
     *
     * @param model    of type Model
     * @param redirect of type RedirectAttributes
     * @param key      of type String
     *
     * @return String
     */
    @GetMapping("/{key}")
    public String checkoutOverview(Model model, RedirectAttributes redirect, @PathVariable String key) {
        try {
            Order order = orderService.getByReference(key);
            this.assertOrderIsSuitableForCheckout(order);

            model.addAttribute("order", order);

            return "webshop/checkout/index";
        } catch (OrderNotFoundException | OrderInvalidException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/";
        }
    }

    /**
     * Cancel a checkout.
     *
     * @param redirect of type RedirectAttributes
     * @param key      of type String
     *
     * @return String
     */
    @GetMapping("/{key}/cancel")
    public String checkoutCancel(RedirectAttributes redirect, @PathVariable String key) {
        try {
            Order order = orderService.getByReference(key);
            this.assertOrderIsSuitableForCheckout(order);
            orderService.updateOrderStatus(order, OrderStatus.CANCELLED);

            redirect.addFlashAttribute("success", "Order has successfully been cancelled.");

            return "redirect:/";
        } catch (EventsException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/";
        }
    }
}
