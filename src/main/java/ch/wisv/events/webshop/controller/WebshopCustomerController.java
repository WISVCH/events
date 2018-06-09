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

@Controller
@RequestMapping("/checkout/{key}/customer")
public class WebshopCustomerController extends WebshopController {

    /** CustomerService. */
    private final CustomerService customerService;

    /** AuthenticationService. */
    private final AuthenticationService authenticationService;

    /**
     * Constructor WebshopController.
     *
     * @param orderService           of type OrderService
     * @param customerService        of type CustomerService
     * @param authenticationService  of type AuthenticationService
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
            this.assertOrderIsSuitableForCheckout(order);
            model.addAttribute("customer", authenticationService.getCurrentCustomer());
            model.addAttribute("order", order);

            if (order.getStatus() != OrderStatus.ANONYMOUS) {
                return "redirect:/checkout/" + order.getPublicReference() + "/payment";
            }

            return "webshop/checkout/customer";
        } catch (OrderNotFoundException | OrderInvalidException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/";
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
            this.assertOrderIsSuitableForCheckout(order);

            if (order.getStatus() != OrderStatus.ANONYMOUS) {
                return "redirect:/checkout/" + order.getPublicReference() + "/payment";
            }

            Customer customer = authenticationService.getCurrentCustomer();
            orderService.addCustomerToOrder(order, customer);

            return "redirect:/checkout/" + order.getPublicReference() + "/payment";
        } catch (EventsException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/";
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
            this.assertOrderIsSuitableForCheckout(order);

            if (order.getStatus() == OrderStatus.ASSIGNED) {
                return "redirect:/checkout/" + order.getPublicReference() + "/payment";
            } else if (order.getStatus() == OrderStatus.ANONYMOUS) {
                if (!model.containsAttribute("customer")) {
                    model.addAttribute("customer", new Customer());
                }

                model.addAttribute("order", order);

                return "webshop/checkout/create";
            }
        } catch (OrderNotFoundException | OrderInvalidException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/";
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
            this.assertOrderIsSuitableForCheckout(order);

            Customer orderCustomer = this.getExistingCustomer(customer);
            if (orderCustomer == null) {
                customerService.create(customer);
                orderCustomer = customer;
            }

            orderService.addCustomerToOrder(order, orderCustomer);

            return "redirect:/checkout/" + order.getPublicReference() + "/payment";
        } catch (CustomerInvalidException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            redirect.addFlashAttribute("customer", customer);

            return "redirect:/checkout/" + key + "/customer/guest";
        } catch (EventsException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/";
        }
    }

    /**
     * Get an existing Customer based on email or username.
     *
     * @param customer of type Customer
     *
     * @return Customer
     */
    private Customer getExistingCustomer(Customer customer) {
        try {
            return customerService.getByEmail(customer.getEmail());
        } catch (CustomerNotFoundException e) {
            try {
                return customerService.getByUsername(customer.getChUsername());
            } catch (CustomerNotFoundException ignored) {
            }
        }

        return null;
    }
}
