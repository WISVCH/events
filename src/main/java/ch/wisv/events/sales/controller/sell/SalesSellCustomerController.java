package ch.wisv.events.sales.controller.sell;

import ch.wisv.events.core.exception.normal.CustomerInvalidException;
import ch.wisv.events.core.exception.normal.CustomerNotFoundException;
import ch.wisv.events.core.exception.normal.EventsException;
import ch.wisv.events.core.exception.normal.OrderNotFoundException;
import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.service.customer.CustomerService;
import ch.wisv.events.core.service.order.OrderService;
import ch.wisv.events.core.service.order.OrderValidationService;
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
 * SalesSellCustomerController class.
 */
@Controller
@PreAuthorize("hasRole('USER')")
@RequestMapping({"/sales/sell/customer/{publicReference}","/sales/sell/customer/{publicReference}/"})
public class SalesSellCustomerController {

    /** CustomerService. */
    private final CustomerService customerService;

    /** OrderService. */
    private final OrderService orderService;

    /**
     * SalesSellCustomerController constructor.
     *
     * @param orderService           of type OrderService
     * @param customerService        of type CustomerService
     */
    @Autowired
    public SalesSellCustomerController(
            OrderService orderService, CustomerService customerService
    ) {
        this.orderService = orderService;
        this.customerService = customerService;
    }

    /**
     * @param model           of type Model
     * @param redirect        of type RedirectAttributes
     * @param publicReference of type String
     *
     * @return String
     */
    @GetMapping("")
    public String identifyCustomer(Model model, RedirectAttributes redirect, @PathVariable String publicReference) {
        try {
            Order order = orderService.getByReference(publicReference);

            if (order.getOwner() != null) {
                return "redirect:/sales/sell/payment/" + order.getPublicReference();
            }

            model.addAttribute("customer", new Customer());
            model.addAttribute("order", order);

            return "sales/sell/customer/rfid";
        } catch (EventsException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/sales/sell";
        }
    }

    @PostMapping("")
    public String determineCustomer(RedirectAttributes redirect, @PathVariable String publicReference, @ModelAttribute Customer customer) {
        Order order;
        try {
            order = orderService.getByReference(publicReference);
        } catch (OrderNotFoundException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/sales/sell";
        }

        try {
            if (customer.getEmail().equals("")) {
                customer = customerService.getByRfidToken(customer.getRfidToken());
            } else {
                customer = customerService.getByEmail(customer.getEmail());
            }

            orderService.addCustomerToOrder(order, customer);

            return "redirect:/sales/sell/order/" + order.getPublicReference();
        } catch (CustomerNotFoundException e) {
            return "redirect:/sales/sell/customer/" + order.getPublicReference() + "/create";
        } catch (EventsException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/sales/sell";
        }
    }

    @GetMapping({"/create","/create/"})
    public String createCustomer(Model model, RedirectAttributes redirect, @PathVariable String publicReference) {
        try {
            model.addAttribute(orderService.getByReference(publicReference));
            if (!model.containsAttribute("customer")) {
                model.addAttribute("customer", new Customer());
            }

            return "sales/sell/customer/create";
        } catch (OrderNotFoundException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/sales/sell";
        }
    }

    /**
     * PostMapping "/sales/order/{publicReference}/customer/create/" creates a new customer.
     *
     * @param redirect        of type RedirectAttributes
     * @param customer        of type Customer
     * @param publicReference of type String
     *
     * @return String
     */
    @PostMapping({"/create","/create/"})
    public String create(RedirectAttributes redirect, @ModelAttribute Customer customer, @PathVariable String publicReference) {
        try {
            Order order = orderService.getByReference(publicReference);
            customerService.create(customer);
            orderService.addCustomerToOrder(order, customer);

            redirect.addFlashAttribute("success", "Customer successfully created!");

            return "redirect:/sales/order/" + order.getPublicReference();
        } catch (CustomerInvalidException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            redirect.addFlashAttribute("customer", customer);

            return "redirect:/sales/sell/customer/" + publicReference + "/create";
        } catch (EventsException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/sales/sell";
        }
    }
}
