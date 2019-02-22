package ch.wisv.events.infrastructure.webshop.controller;

import ch.wisv.events.domain.model.order.Order;
import ch.wisv.events.domain.model.order.OrderStatus;
import ch.wisv.events.domain.model.user.User;
import ch.wisv.events.services.AuthenticationService;
import ch.wisv.events.services.OrderService;
import static java.util.Objects.nonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * WebshopIndexController class.
 */
@Controller
@RequestMapping("/webshop/login")
public class WebshopLoginController extends AbstractWebshopController {

    /** Model attribute order. */
    private static final String MODEL_ATTR_ORDER = "order";

    /** Redirect to payment page. */
    private static final String REDIRECT_PAYMENT_PAGE = "redirect:/webshop/payment/%s";

    /** Redirect to login page. */
    private static final String REDIRECT_LOGIN_PAGE = "redirect:/webshop/login/%s";

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
     * View order string.
     *
     * @param model           of type Model
     * @param publicReference the public reference
     *
     * @return string
     */
    @GetMapping("/{publicReference}")
    public String customerIndex(Model model, @PathVariable String publicReference) {
        Order order = orderService.getByPublicReference(publicReference);
        if (order.getStatus() != OrderStatus.OPEN && nonNull(order.getCustomer())) {
            return REDIRECT_PAYMENT_PAGE;
        }

        model.addAttribute(MODEL_ATTR_ORDER, order);

        return "webshop/webshop-checkout-login";
    }

    /**
     * Login the user through CH Connect.
     *
     * @param publicReference of type String
     *
     * @return String
     */
    @GetMapping("/{publicReference}/connect")
    @PreAuthorize("hasRole('USER')")
    public String loginChConnect(@PathVariable String publicReference) {
        Order order = orderService.getByPublicReference(publicReference);
        if (order.getStatus() != OrderStatus.OPEN && nonNull(order.getCustomer())) {
            return String.format(REDIRECT_PAYMENT_PAGE, order.getPublicReference());
        }

        User customer = authenticationService.getLoggedInUser();
        orderService.addCustomerToOrder(order, customer);

        return String.format(REDIRECT_PAYMENT_PAGE, order.getPublicReference());
    }

    /**
     * Login the user through CH Connect.
     *
     * @param publicReference of type String
     *
     * @return String
     */
    @GetMapping("/{publicReference}/guest")
    @PreAuthorize("hasRole('USER')")
    public String create(@PathVariable String publicReference) {
        Order order = orderService.getByPublicReference(publicReference);
        if (order.hasChOnlyProduct()) {
            return String.format(REDIRECT_LOGIN_PAGE, order.getPublicReference());
        }

        if (order.getStatus() != OrderStatus.OPEN && nonNull(order.getCustomer())) {
            return String.format(REDIRECT_PAYMENT_PAGE, order.getPublicReference());
        }

        User customer = authenticationService.getLoggedInUser();
        orderService.addCustomerToOrder(order, customer);

        return String.format(REDIRECT_PAYMENT_PAGE, order.getPublicReference());
    }
}
