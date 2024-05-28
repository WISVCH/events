package ch.wisv.events.webshop.controller;

import ch.wisv.events.core.exception.normal.EventsException;
import ch.wisv.events.core.exception.normal.OrderInvalidException;
import ch.wisv.events.core.exception.normal.OrderNotFoundException;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderProductDto;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.service.auth.AuthenticationService;
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

/**
 * WebshopCheckoutController class.
 */
@Controller
@RequestMapping({"/checkout","/checkout/"})
public class WebshopCheckoutController extends WebshopController {

    /** Error message no products in Order. */
    private static final String ERROR_MESSAGE_ORDER_WITHOUT_PRODUCTS = "Shopping basket can not be empty!";

    private static final String ERROR_MESSAGE_ORDER_NOT_AGREED = "You have to agree with the General Terms and Conditions to proceed to checkout.";

    /** Username of the user that created this order. */
    private static final String USERNAME_ORDER_CREATED = "events-webshop";

    /** Success message Order cancelled. */
    private static final String SUCCESS_MESSAGE_ORDER_CANCELLED = "Order has successfully been cancelled.";

    /** OrderValidationService. */
    private final OrderValidationService orderValidationService;

    /**
     * Constructor WebshopController.
     *
     * @param orderService           of type OrderService
     * @param orderValidationService of type OrderValidationService
     * @param authenticationService  of type AuthenticationService
     */
    public WebshopCheckoutController(
            OrderService orderService,
            OrderValidationService orderValidationService,
            AuthenticationService authenticationService
    ) {
        super(orderService, authenticationService);
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
                redirect.addFlashAttribute(MODEL_ATTR_ERROR, ERROR_MESSAGE_ORDER_WITHOUT_PRODUCTS);

                return REDIRECT_EVENTS_HOME;
            }
            if (orderProductDto.getAgreedGTC().equals(Boolean.FALSE)) {
                redirect.addFlashAttribute(MODEL_ATTR_ERROR, ERROR_MESSAGE_ORDER_NOT_AGREED);

                return REDIRECT_EVENTS_HOME;
            }

            Order order = orderService.createOrderByOrderProductDto(orderProductDto);
            order.setCreatedBy(USERNAME_ORDER_CREATED);
            orderValidationService.assertOrderIsValid(order);
            orderService.create(order);

            return "redirect:/checkout/" + order.getPublicReference();
        } catch (EventsException e) {
            redirect.addFlashAttribute(MODEL_ATTR_ERROR, e.getMessage());

            return REDIRECT_EVENTS_HOME;
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
    @GetMapping({"/{key}","/{key}/"})
    public String checkoutOverview(Model model, RedirectAttributes redirect, @PathVariable String key) {
        try {
            Order order = orderService.getByReference(key);
            this.assertOrderIsSuitableForCheckout(order);

            model.addAttribute(MODEL_ATTR_CUSTOMER, authenticationService.getCurrentCustomer());
            model.addAttribute(MODEL_ATTR_ORDER, order);

            return "webshop/checkout/index";
        } catch (OrderNotFoundException | OrderInvalidException e) {
            redirect.addFlashAttribute(MODEL_ATTR_ERROR, e.getMessage());

            return REDIRECT_EVENTS_HOME;
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
    @GetMapping({"/{key}/cancel","/{key}/cancel/"})
    public String checkoutCancel(RedirectAttributes redirect, @PathVariable String key) {
        try {
            Order order = orderService.getByReference(key);
            this.assertOrderIsSuitableForCheckout(order);
            orderService.updateOrderStatus(order, OrderStatus.CANCELLED);

            redirect.addFlashAttribute(MODEL_ATTR_SUCCESS, SUCCESS_MESSAGE_ORDER_CANCELLED);

            return REDIRECT_EVENTS_HOME;
        } catch (EventsException e) {
            redirect.addFlashAttribute(MODEL_ATTR_ERROR, e.getMessage());

            return REDIRECT_EVENTS_HOME;
        }
    }
}
