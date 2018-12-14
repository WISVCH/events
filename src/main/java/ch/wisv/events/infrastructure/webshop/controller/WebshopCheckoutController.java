package ch.wisv.events.infrastructure.webshop.controller;

import ch.wisv.events.domain.exception.ModelNotFoundException;
import ch.wisv.events.domain.model.order.Order;
import ch.wisv.events.infrastructure.webshop.dto.OrderDto;
import ch.wisv.events.services.OrderService;
import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;
import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * WebshopIndexController class.
 */
@Controller
@RequestMapping("/webshop/checkout")
public class WebshopCheckoutController extends AbstractWebshopController {

    /** Model attribute order. */
    private static final String MODEL_ATTR_ORDER = "order";

    /** OrderService. */
    private final OrderService orderService;

    /**
     * WebshopController constructor.
     *
     * @param orderService of type OrderService
     */
    protected WebshopCheckoutController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Create order based on an OrderDto.
     *
     * @param redirect      of type RedirectAttributes
     * @param orderDto      of type OrderDto
     * @param bindingResult of type BindingResult
     *
     * @return String string
     */
    @PostMapping
    public String createOrder(RedirectAttributes redirect, @Valid @ModelAttribute OrderDto orderDto, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                Map<String, String> errorMessages = new HashMap<>();
                for (FieldError fieldError : bindingResult.getFieldErrors()) {
                    errorMessages.put(fieldError.getField(), fieldError.getDefaultMessage());
                }

                redirect.addFlashAttribute("errors", errorMessages);

                return "redirect:/webshop/";
            }

            Order order = orderService.createByOrderDto(orderDto);
            order.setCreatedBy("");

            return "redirect:/webshop/checkout/" + order.getPublicReference();
        } catch (ModelNotFoundException e) {
            redirect.addFlashAttribute("errors", ImmutableMap.of("invalid", "Shopping cart contains invalid product"));

            return "redirect:/webshop/";
        }
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

        return "webshop/checkout/index";
    }
}
