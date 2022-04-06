package ch.wisv.events.webshop.controller;

import ch.wisv.events.core.exception.normal.CustomerInvalidException;
import ch.wisv.events.core.exception.normal.CustomerNotFoundException;
import ch.wisv.events.core.exception.normal.EventsException;
import ch.wisv.events.core.exception.normal.OrderInvalidException;
import ch.wisv.events.core.exception.normal.OrderNotFoundException;
import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.service.auth.AuthenticationService;
import ch.wisv.events.core.service.customer.CustomerService;
import ch.wisv.events.core.service.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * WebshopCustomerController.
 */
@Controller
@RequestMapping("/checkout/{key}/customer")
public class WebshopCustomerController extends WebshopController {

    /** Redirect to the payment page. */
    private static final String REDIRECT_CHECKOUT_PAYMENT = "redirect:/checkout/%s/payment";

    /** Redirect to the CH Connect customer checkout page. */
    private static final String REDIRECT_CHECKOUT_CUSTOMER_CHCONNECT = "redirect:/checkout/%s/customer/chconnect";

    /** Redirect to the Guest customer checkout page. */
    private static final String REDIRECT_CHECKOUT_CUSTOMER_GUEST = "redirect:/checkout/%s/customer/guest";

    /** CustomerService. */
    private final CustomerService customerService;

    /** AuthenticationService. */
    private final AuthenticationService authenticationService;

    /**
     * Constructor WebshopController.
     *
     * @param orderService          of type OrderService
     * @param customerService       of type CustomerService
     * @param authenticationService of type AuthenticationService
     */
    @Autowired
    public WebshopCustomerController(
            OrderService orderService,
            CustomerService customerService,
            AuthenticationService authenticationService
    ) {
        super(orderService, authenticationService);
        this.customerService = customerService;
        this.authenticationService = authenticationService;
    }

    /**
     * Show the possible options to checkout as a Customer.
     *
     * @param model    of type Model
     * @param redirect of type RedirectAttributes
     * @param key      of type String
     *
     * @return String
     */
    @GetMapping
    public String customerOptions(Model model, RedirectAttributes redirect, @PathVariable String key) {
        try {
            Order order = orderService.getByReference(key);
            if (this.orderCheck(order) != null) {
                return this.orderCheck(order);
            }

            Customer customer = authenticationService.getCurrentCustomer();
            if(customer != null) {
                return String.format(REDIRECT_CHECKOUT_CUSTOMER_CHCONNECT, key);
            }

            model.addAttribute(MODEL_ATTR_ORDER, order);
            model.addAttribute("chOnly", orderService.containsChOnlyProduct(order));

            return "webshop/checkout/customer";
        } catch (OrderNotFoundException | OrderInvalidException e) {
            redirect.addFlashAttribute(MODEL_ATTR_ERROR, e.getMessage());

            return REDIRECT_EVENTS_HOME;
        }
    }

    /**
     * Checkout using CHConnect.
     *
     * @param redirect of type RedirectAttributes
     * @param key      of type String
     *
     * @return String
     */
    @GetMapping("/chconnect")
    @PreAuthorize("hasRole('USER')")
    public String customerChConnect(RedirectAttributes redirect, @PathVariable String key) {
        try {
            Order order = orderService.getByReference(key);
            if (this.orderCheck(order) != null) {
                return this.orderCheck(order);
            }

            Customer customer = authenticationService.getCurrentCustomer();
            orderService.addCustomerToOrder(order, customer);

            return String.format(REDIRECT_CHECKOUT_PAYMENT, order.getPublicReference());
        } catch (EventsException e) {
            redirect.addFlashAttribute(MODEL_ATTR_ERROR, e.getMessage());

            return REDIRECT_EVENTS_HOME;
        }
    }

    /**
     * Checkout as a guest.
     *
     * @param model    of type Model
     * @param redirect of type RedirectAttributes
     * @param key      of type String
     *
     * @return String
     */
    @GetMapping("/guest")
    public String customerGuest(Model model, RedirectAttributes redirect, @PathVariable String key) {
        try {
            Order order = orderService.getByReference(key);
            if (this.orderCheck(order) != null) {
                return this.orderCheck(order);
            }

            if (!model.containsAttribute(MODEL_ATTR_CUSTOMER)) {
                model.addAttribute(MODEL_ATTR_CUSTOMER, new Customer());
            }
            model.addAttribute(MODEL_ATTR_ORDER, order);

            return "webshop/checkout/create";
        } catch (EventsException e) {
            redirect.addFlashAttribute(MODEL_ATTR_ERROR, e.getMessage());

            return REDIRECT_EVENTS_HOME;
        }
    }

    /**
     * Checkout as a guest by getting or creating an Customer.
     *
     * @param redirect of type RedirectAttributes
     * @param key      of type String
     * @param customer of type Customer
     *
     * @return String
     */
    @PostMapping("/guest")
    public String checkoutGuest(RedirectAttributes redirect, @PathVariable String key, @ModelAttribute Customer customer) {
        try {
            Order order = orderService.getByReference(key);
            if (this.orderCheck(order) != null) {
                return this.orderCheck(order);
            }

            Customer orderCustomer = this.getOrCreateCustomer(customer);
            if (orderService.containsChOnlyProduct(order) && !orderCustomer.isVerifiedChMember()) {
                throw new CustomerInvalidException("Customer has not been verified as CH member");
            }

            orderService.addCustomerToOrder(order, orderCustomer);

            return String.format(REDIRECT_CHECKOUT_PAYMENT, order.getPublicReference());
        } catch (CustomerInvalidException e) {
            redirect.addFlashAttribute(MODEL_ATTR_ERROR, e.getMessage());
            redirect.addFlashAttribute(MODEL_ATTR_CUSTOMER, customer);

            return String.format(REDIRECT_CHECKOUT_CUSTOMER_GUEST, key);
        } catch (EventsException e) {
            redirect.addFlashAttribute(MODEL_ATTR_ERROR, e.getMessage());

            return REDIRECT_EVENTS_HOME;
        }
    }

    /**
     * Get an existing Customer based on email or username.
     *
     * @param customer of type Customer
     *
     * @return Customer
     *
     * @throws CustomerInvalidException when Customer is invalid.
     */
    private Customer getOrCreateCustomer(Customer customer) throws CustomerInvalidException {
        try {
            if (customer.getEmail() != null && !customer.getEmail().equals("")) {
                return customerService.getByEmail(customer.getEmail());
            }
        } catch (CustomerNotFoundException e) {
            customerService.create(customer);
        }

        return customer;
    }

    /**
     * Check if this step is suitable for the Order.
     *
     * @param order of type Order
     *
     * @return String
     *
     * @throws OrderInvalidException when Order is not suitable for checkout
     */
    private String orderCheck(Order order) throws OrderInvalidException {
        this.assertOrderIsSuitableForCheckout(order);

        if (order.getStatus() != OrderStatus.ANONYMOUS) {
            return String.format(REDIRECT_CHECKOUT_PAYMENT, order.getPublicReference());
        }

        return null;
    }
}
