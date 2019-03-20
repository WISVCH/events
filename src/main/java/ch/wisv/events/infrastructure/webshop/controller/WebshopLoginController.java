package ch.wisv.events.infrastructure.webshop.controller;

import ch.wisv.events.domain.model.order.Order;
import ch.wisv.events.domain.model.order.OrderStatus;
import ch.wisv.events.domain.model.user.User;
import ch.wisv.events.infrastructure.webshop.util.WebshopConstant;
import static ch.wisv.events.infrastructure.webshop.util.WebshopConstant.ERROR_INVALID;
import static ch.wisv.events.infrastructure.webshop.util.WebshopConstant.ERROR_MESSAGE_GUEST_CHECKOUT_NOT_ALLOWED;
import static ch.wisv.events.infrastructure.webshop.util.WebshopConstant.MODEL_ATTR_ERRORS;
import static ch.wisv.events.infrastructure.webshop.util.WebshopConstant.MODEL_ATTR_ORDER;
import static ch.wisv.events.infrastructure.webshop.util.WebshopConstant.REDIRECT_CUSTOMER_PAGE;
import static ch.wisv.events.infrastructure.webshop.util.WebshopConstant.REDIRECT_LOGIN_PAGE;
import static ch.wisv.events.infrastructure.webshop.util.WebshopConstant.REDIRECT_PAYMENT_PAGE;
import static ch.wisv.events.infrastructure.webshop.util.WebshopConstant.ROUTE_WEBSHOP_LOGIN;
import static ch.wisv.events.infrastructure.webshop.util.WebshopConstant.ROUTE_WEBSHOP_LOGIN_OPTION_CONNECT;
import static ch.wisv.events.infrastructure.webshop.util.WebshopConstant.ROUTE_WEBSHOP_LOGIN_OPTION_GUEST;
import static ch.wisv.events.infrastructure.webshop.util.WebshopConstant.ROUTE_WEBSHOP_OPTION_PUBLIC_REFERENCE;
import ch.wisv.events.services.AuthenticationService;
import ch.wisv.events.services.OrderService;
import com.google.common.collect.ImmutableMap;
import static java.util.Objects.nonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * WebshopIndexController class.
 */
@Controller
@RequestMapping(ROUTE_WEBSHOP_LOGIN)
public class WebshopLoginController extends AbstractWebshopController {

    /** OrderService. */
    private final OrderService orderService;

    /** AuthenticationService. */
    private final AuthenticationService authenticationService;

    /**
     * WebshopController constructor.
     *
     * @param orderService          of type OrderService
     * @param authenticationService of type
     */
    protected WebshopLoginController(OrderService orderService, AuthenticationService authenticationService) {
        this.orderService = orderService;
        this.authenticationService = authenticationService;
    }

    /**
     * Show login index, with the login options.
     *
     * @param model           of type Model
     * @param publicReference the public reference
     *
     * @return string
     */
    @GetMapping(ROUTE_WEBSHOP_OPTION_PUBLIC_REFERENCE)
    public String loginIndex(Model model, @PathVariable String publicReference) {
        Order order = orderService.getByPublicReference(publicReference);
        if (order.getStatus() != OrderStatus.ANONYMOUS && nonNull(order.getCustomer())) {
            return String.format(REDIRECT_PAYMENT_PAGE, order.getPublicReference());
        }

        model.addAttribute(MODEL_ATTR_ORDER, order);

        return WebshopConstant.VIEW_WEBSHOP_CHECKOUT_LOGIN;
    }

    /**
     * Login the user through CH Connect.
     *
     * @param publicReference of type String
     *
     * @return String
     */
    @GetMapping(ROUTE_WEBSHOP_OPTION_PUBLIC_REFERENCE + ROUTE_WEBSHOP_LOGIN_OPTION_CONNECT)
    @PreAuthorize("hasRole('USER')")
    public String loginChConnect(@PathVariable String publicReference) {
        Order order = orderService.getByPublicReference(publicReference);
        if (order.getStatus() != OrderStatus.ANONYMOUS && nonNull(order.getCustomer())) {
            return String.format(REDIRECT_PAYMENT_PAGE, order.getPublicReference());
        }

        User customer = authenticationService.getLoggedInUser();
        orderService.addCustomerToOrder(order, customer);

        return String.format(REDIRECT_PAYMENT_PAGE, order.getPublicReference());
    }

    /**
     * Login the user through CH Connect.
     *
     * @param redirect        of type RedirectAttributes
     * @param publicReference of type String
     *
     * @return String
     */
    @GetMapping(ROUTE_WEBSHOP_OPTION_PUBLIC_REFERENCE + ROUTE_WEBSHOP_LOGIN_OPTION_GUEST)
    public String create(RedirectAttributes redirect, @PathVariable String publicReference) {
        Order order = orderService.getByPublicReference(publicReference);
        if (order.getStatus() != OrderStatus.ANONYMOUS && nonNull(order.getCustomer())) {
            return String.format(REDIRECT_PAYMENT_PAGE, order.getPublicReference());
        }

        if (order.hasChOnlyProduct()) {
            redirect.addFlashAttribute(MODEL_ATTR_ERRORS, ImmutableMap.of(ERROR_INVALID, ERROR_MESSAGE_GUEST_CHECKOUT_NOT_ALLOWED));

            return String.format(REDIRECT_LOGIN_PAGE, order.getPublicReference());
        }

        return String.format(REDIRECT_CUSTOMER_PAGE, order.getPublicReference());
    }
}
