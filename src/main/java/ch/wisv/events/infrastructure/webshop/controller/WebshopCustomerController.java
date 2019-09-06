package ch.wisv.events.infrastructure.webshop.controller;

import ch.wisv.events.domain.exception.ModelNotFoundException;
import ch.wisv.events.domain.model.order.Order;
import ch.wisv.events.domain.model.order.OrderStatus;
import ch.wisv.events.domain.model.user.User;
import ch.wisv.events.infrastructure.webshop.dto.UserDto;
import static ch.wisv.events.infrastructure.webshop.util.WebshopConstant.ERROR_INVALID;
import static ch.wisv.events.infrastructure.webshop.util.WebshopConstant.ERROR_MESSAGE_GUEST_CHECKOUT_NOT_ALLOWED;
import static ch.wisv.events.infrastructure.webshop.util.WebshopConstant.MODEL_ATTR_ERRORS;
import static ch.wisv.events.infrastructure.webshop.util.WebshopConstant.MODEL_ATTR_ORDER;
import static ch.wisv.events.infrastructure.webshop.util.WebshopConstant.MODEL_ATTR_USER_DTO;
import static ch.wisv.events.infrastructure.webshop.util.WebshopConstant.REDIRECT_CUSTOMER_PAGE;
import static ch.wisv.events.infrastructure.webshop.util.WebshopConstant.REDIRECT_LOGIN_PAGE;
import static ch.wisv.events.infrastructure.webshop.util.WebshopConstant.REDIRECT_PAYMENT_PAGE;
import static ch.wisv.events.infrastructure.webshop.util.WebshopConstant.ROUTE_WEBSHOP_CUSTOMER;
import static ch.wisv.events.infrastructure.webshop.util.WebshopConstant.VIEW_WEBSHOP_CUSTOMER;
import ch.wisv.events.services.OrderService;
import ch.wisv.events.services.UserService;
import ch.wisv.events.util.BindingResultBuilder;
import com.google.common.collect.ImmutableList;
import java.util.HashMap;
import static java.util.Objects.nonNull;
import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * WebshopCustomerController class.
 */
@Controller
@RequestMapping(ROUTE_WEBSHOP_CUSTOMER)
public class WebshopCustomerController extends AbstractWebshopController {

    /** OrderService. */
    private final OrderService orderService;

    /** UserService. */
    private final UserService userService;

    /**
     * WebshopCustomerController constructor.
     *
     * @param orderService of type OrderService
     * @param userService of type UserService
     */
    public WebshopCustomerController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    /**
     * Create new customer view.
     *
     * @param model           of type Model
     * @param redirect of type RedirectAttributes
     * @param publicReference of type String
     *
     * @return String
     */
    @GetMapping("/{publicReference}")
    public String createCustomer(Model model, RedirectAttributes redirect, @PathVariable String publicReference) {
        Order order = orderService.getByPublicReference(publicReference);
        if (order.getStatus() != OrderStatus.ANONYMOUS && nonNull(order.getCustomer())) {
            return String.format(REDIRECT_PAYMENT_PAGE, order.getPublicReference());
        }

        if (order.hasChOnlyProduct()) {
            redirect.addFlashAttribute(MODEL_ATTR_ERRORS, ImmutableList.of(ERROR_INVALID, ERROR_MESSAGE_GUEST_CHECKOUT_NOT_ALLOWED));

            return String.format(REDIRECT_LOGIN_PAGE, order.getPublicReference());
        }

        if (!model.containsAttribute(MODEL_ATTR_ERRORS)) {
            model.addAttribute(MODEL_ATTR_ERRORS, new HashMap<String, String>());
        }
        model.addAttribute(MODEL_ATTR_ORDER, order);
        model.addAttribute(MODEL_ATTR_USER_DTO, new UserDto());

        return VIEW_WEBSHOP_CUSTOMER;
    }

    /**
     * Create new customer POST request.
     *
     * @param redirect        of type RedirectAttributes
     * @param publicReference of type String
     * @param userDto         of type UserDto
     * @param bindingResult   of type BindingResult
     *
     * @return String
     */
    @PostMapping("/{publicReference}")
    public String createCustomer(
            RedirectAttributes redirect,
            @PathVariable String publicReference,
            @Valid @ModelAttribute UserDto userDto,
            BindingResult bindingResult
    ) {
        Order order = orderService.getByPublicReference(publicReference);
        if (order.getStatus() != OrderStatus.ANONYMOUS && nonNull(order.getCustomer())) {
            return String.format(REDIRECT_PAYMENT_PAGE, order.getPublicReference());
        }

        if (order.hasChOnlyProduct()) {
            redirect.addFlashAttribute(MODEL_ATTR_ERRORS, ImmutableList.of(ERROR_INVALID, ERROR_MESSAGE_GUEST_CHECKOUT_NOT_ALLOWED));

            return String.format(REDIRECT_LOGIN_PAGE, order.getPublicReference());
        }

        if (bindingResult.hasErrors()) {
            redirect.addFlashAttribute(MODEL_ATTR_ERRORS, BindingResultBuilder.createErrorMap(bindingResult));

            return String.format(REDIRECT_CUSTOMER_PAGE, order.getPublicReference());
        }

        User user = getOrCreateUser(userDto);
        orderService.addCustomerToOrder(order, user);

        return String.format(REDIRECT_PAYMENT_PAGE, order.getPublicReference());
    }

    /**
     * Get or create a User from the UserDto.
     *
     * @param userDto of type UserDto
     *
     * @return User
     */
    private User getOrCreateUser(@Valid UserDto userDto) {
        try {
            return userService.getByEmail(userDto.getEmail());
        } catch (ModelNotFoundException exception) {
            return userService.createByUserDto(userDto);
        }
    }
}
