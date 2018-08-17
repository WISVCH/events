package ch.wisv.events.webshop.controller;

import ch.wisv.events.core.exception.normal.CustomerInvalidException;
import ch.wisv.events.core.exception.normal.EventsException;
import ch.wisv.events.core.exception.normal.OrderNotFoundException;
import ch.wisv.events.core.exception.normal.RegistrationInvalidException;
import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.model.registration.Registration;
import ch.wisv.events.core.service.auth.AuthenticationService;
import ch.wisv.events.core.service.customer.CustomerService;
import ch.wisv.events.core.service.order.OrderService;
import ch.wisv.events.core.service.registration.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/checkout/{key}/registration")
public class WebshopRegistrationController extends WebshopController {

    /** Redirect to the payment page. */
    private static final String REDIRECT_CHECKOUT_PAYMENT = "redirect:/checkout/%s/payment";

    /** Redirect to the registration page. */
    private static final String REDIRECT_CHECKOUT_REGISTRATION = "redirect:/checkout/%s/registration";

    /** RegistrationService. */
    private final RegistrationService registrationService;

    /** CustomerService. */
    private final CustomerService customerService;

    /**
     * Constructor WebshopController.
     *
     * @param orderService          of type OrderService.
     * @param authenticationService of type AuthenticationService
     * @param registrationService   of type RegistrationService
     * @param customerService       of type CustomerService
     */
    @Autowired
    public WebshopRegistrationController(
            OrderService orderService,
            AuthenticationService authenticationService,
            RegistrationService registrationService,
            CustomerService customerService
    ) {
        super(orderService, authenticationService);
        this.registrationService = registrationService;
        this.customerService = customerService;
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
    public String registrationView(Model model, RedirectAttributes redirect, @PathVariable String key) {
        try {
            Order order = orderService.getByReference(key);
            if (order.getStatus() != OrderStatus.ANONYMOUS) {
                return String.format(REDIRECT_CHECKOUT_PAYMENT, order.getPublicReference());
            }

            model.addAttribute(MODEL_ATTR_ORDER, order);
            if (!model.containsAttribute(MODEL_ATTR_REGISTRATION)) {
                model.addAttribute(MODEL_ATTR_REGISTRATION, new Registration());
            }

            return "webshop/registration/index";
        } catch (OrderNotFoundException e) {
            redirect.addFlashAttribute(MODEL_ATTR_ERROR, e.getMessage());

            return REDIRECT_EVENTS_HOME;
        }
    }

    /**
     * POST mapping validate and create a Registration.
     *
     * @param redirect     of type RedirectAttributes
     * @param key          of type String
     * @param registration of type Registration
     *
     * @return String
     */
    @PostMapping
    public String registrationCreate(RedirectAttributes redirect, @PathVariable String key, @ModelAttribute Registration registration) {
        try {
            Order order = orderService.getByReference(key);
            this.assertOrderIsSuitableForCheckout(order);

            registrationService.create(registration);
            this.addRegistrationToOrder(order, registration);

            return String.format(REDIRECT_CHECKOUT_PAYMENT, order.getPublicReference());
        } catch (RegistrationInvalidException | CustomerInvalidException e) {
            redirect.addFlashAttribute(MODEL_ATTR_ERROR, e.getMessage());
            redirect.addFlashAttribute(MODEL_ATTR_REGISTRATION, registration);

            return String.format(REDIRECT_CHECKOUT_REGISTRATION, key);
        } catch (EventsException e) {
            redirect.addFlashAttribute(MODEL_ATTR_ERROR, e.getMessage());

            return REDIRECT_EVENTS_HOME;
        }
    }

    /**
     * Add Registration to Order.
     *
     * @param order        of type Order
     * @param registration of type Registration
     *
     * @throws EventsException when
     */
    private void addRegistrationToOrder(Order order, Registration registration) throws EventsException {
        String name = String.format(
                "%s %s %s",
                registration.getProfile().getFirstName(),
                registration.getProfile().getSurnamePrefix(),
                registration.getProfile().getSurname()
        );

        Customer customer = new Customer();
        customer.setName(name);
        customer.setEmail(registration.getProfile().getEmail());
        customerService.create(customer);

        orderService.addCustomerToOrder(order, customer);
    }
}
