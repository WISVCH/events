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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * WebshopIndexController class.
 */
@Controller
@RequestMapping("/webshop/customer")
public class WebshopCustomerController extends AbstractWebshopController {

    /** Model attribute order. */
    private static final String MODEL_ATTR_ORDER = "order";

    private static final String REDIRECT_TO_SELECT_PAYMENT_OPTION = "";

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
    protected WebshopCustomerController(OrderService orderService, AuthenticationService authenticationService) {
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
            return REDIRECT_TO_SELECT_PAYMENT_OPTION;
        }

        model.addAttribute(MODEL_ATTR_ORDER, order);
        model.addAttribute("isChOnly", order.getItems().stream().anyMatch(item -> item.getProduct().isChOnly()));

        return "webshop/checkout/customer";
    }

    @GetMapping("/{publicReference}/connect")
    @PreAuthorize("hasRole('USER')")
    public String loginChConnect(RedirectAttributes redirect, @PathVariable String publicReference) {
        Order order = orderService.getByPublicReference(publicReference);
        if (order.getStatus() != OrderStatus.OPEN && nonNull(order.getCustomer())) {
            return REDIRECT_TO_SELECT_PAYMENT_OPTION;
        }

        User customer = authenticationService.getLoggedInUser();
        orderService.addCustomerToOrder(order, customer);

        return "redirect:/webshop/checkout/" + order.getPublicReference();
    }
}
