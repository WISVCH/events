package ch.wisv.events.infrastructure.webshop.controller;

import ch.wisv.events.domain.exception.ModelNotFoundException;
import ch.wisv.events.domain.exception.OrderInvalidException;
import ch.wisv.events.domain.model.order.Order;
import ch.wisv.events.infrastructure.webshop.dto.OrderDto;
import ch.wisv.events.services.OrderService;
import ch.wisv.events.services.OrderValidationService;
import ch.wisv.events.util.BindingResultBuilder;
import com.google.common.collect.ImmutableMap;
import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * WebshopIndexController class.
 */
@Controller
@RequestMapping("/webshop/checkout")
public class WebshopCheckoutController extends AbstractWebshopController {

    /** Redirect to home page. */
    private static final String REDIRECT_HOME_PAGE = "redirect:/webshop/";

    /** Redirect to order page. */
    private static final String REDIRECT_ORDER_PAGE = "redirect:/webshop/order/%s";

    /** OrderService. */
    private final OrderService orderService;

    /** OrderValidationService. */
    private final OrderValidationService orderValidationService;

    /**
     * WebshopController constructor.
     *
     * @param orderService of type OrderService
     * @param orderValidationService of type OrderValidationService
     */
    protected WebshopCheckoutController(OrderService orderService, OrderValidationService orderValidationService) {
        this.orderService = orderService;
        this.orderValidationService = orderValidationService;
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
                redirect.addFlashAttribute(MODEL_ATTR_ERRORS, BindingResultBuilder.createErrorMap(bindingResult));

                return REDIRECT_HOME_PAGE;
            }

            Order order = orderService.createByOrderDto(orderDto);
            orderValidationService.assertIsValidForCheckout(order);
            order.setCreatedBy("events-webshop");

            return String.format(REDIRECT_ORDER_PAGE, order.getPublicReference());
        } catch (ModelNotFoundException | OrderInvalidException e) {
            redirect.addFlashAttribute(MODEL_ATTR_ERRORS, ImmutableMap.of("invalid", e.getMessage()));

            return REDIRECT_HOME_PAGE;
        }
    }
}
