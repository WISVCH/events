package ch.wisv.events.admin.controller;

import ch.wisv.events.core.exception.normal.CustomerInvalidException;
import ch.wisv.events.core.exception.normal.CustomerNotFoundException;
import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.service.customer.CustomerService;
import ch.wisv.events.core.service.ticket.TicketService;
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
 * DashboardCustomerController class.
 */
@Controller
@RequestMapping(value = "/administrator/customers")
@PreAuthorize("hasRole('ADMIN')")
public class DashboardCustomerController extends DashboardController {

    /** CustomerService. */
    private final CustomerService customerService;

    /** TicketService. */
    private final TicketService ticketService;

    /**
     * Autowired constructor.
     *
     * @param customerService CustomerService
     * @param ticketService   TicketService
     */
    @Autowired
    public DashboardCustomerController(CustomerService customerService, TicketService ticketService) {
        this.customerService = customerService;
        this.ticketService = ticketService;
    }

    /**
     * Index of customers pages.
     *
     * @param model String model
     *
     * @return path to customers index template
     */
    @GetMapping()
    public String index(Model model) {
        model.addAttribute(OBJ_CUSTOMERS, customerService.getAllCustomers());

        return "admin/customers/index";
    }

    /**
     * Edit an existing customer page. It will redirect the user when the key provided does not belong to any of the
     * customers available.
     *
     * @param model    of type Model
     * @param redirect of type RedirectAttributes
     * @param key      of type String
     *
     * @return path to the customer edit template
     */
    @GetMapping({"/view/{key}","/view/{key}/"})
    public String view(Model model, RedirectAttributes redirect, @PathVariable String key) {
        try {
            Customer customer = customerService.getByKey(key);
            model.addAttribute(OBJ_CUSTOMER, customer);
            model.addAttribute(OBJ_TICKETS, ticketService.getAllByCustomer(customer));

            return "admin/customers/view";
        } catch (CustomerNotFoundException e) {
            redirect.addFlashAttribute(FLASH_ERROR, e.getMessage());

            return "redirect:/administrator/customers/";
        }
    }

    /**
     * Create a new customer page.
     *
     * @param model String model
     *
     * @return path to customer create template
     */
    @GetMapping({"/create","/create/"})
    public String create(Model model) {
        if (!model.containsAttribute(OBJ_CUSTOMER)) {
            model.addAttribute(OBJ_CUSTOMER, new Customer());
        }

        return "admin/customers/customer";
    }

    /**
     * Creates a new customer using the Customer model. It will redirect the user back to the create page when not
     * all the required field are filled in.
     *
     * @param redirect RedirectAttributes for the user feedback
     * @param model    Customer model
     *
     * @return redirect
     */
    @PostMapping({"/create","/create/"})
    public String create(RedirectAttributes redirect, @ModelAttribute Customer model) {
        try {
            customerService.create(model);
            redirect.addFlashAttribute(FLASH_SUCCESS, "Customer with name " + model.getName() + "  has been created!");

            return "redirect:/administrator/customers/";
        } catch (CustomerInvalidException e) {
            redirect.addFlashAttribute(FLASH_ERROR, e.getMessage());
            redirect.addFlashAttribute(OBJ_CUSTOMER, model);

            return "redirect:/administrator/customers/create/";
        }

    }

    /**
     * Edit an existing customer page. It will redirect the user when the key provided does not belong to any of the
     * customers available.
     *
     * @param model    of type Model
     * @param redirect of type RedirectAttributes
     * @param key      of type String
     *
     * @return path to the customer edit template
     */
    @GetMapping({"/edit/{key}","/edit/{key}/"})
    public String edit(Model model, RedirectAttributes redirect, @PathVariable String key) {
        try {
            Customer customer = customerService.getByKey(key);
            if (!model.containsAttribute(OBJ_CUSTOMER)) {
                model.addAttribute(OBJ_CUSTOMER, customer);
            }
            model.addAttribute("products", ticketService.getAllByCustomer(customer));

            return "admin/customers/customer";
        } catch (CustomerNotFoundException e) {
            redirect.addFlashAttribute(FLASH_ERROR, e.getMessage());

            return "redirect:/administrator/customers/";
        }
    }

    /**
     * Updates an existing customer using the Customer customer. It will show an error when not all the required fields
     * are filled in.
     *
     * @param redirect of type RedirectAttributes
     * @param customer of type Customer
     * @param key      of type String
     *
     * @return redirect
     */
    @PostMapping({"/edit/{key}","/edit/{key}/"})
    public String edit(RedirectAttributes redirect, @ModelAttribute Customer customer, @PathVariable String key) {
        try {
            customer.setKey(key);
            customerService.update(customer);
            redirect.addFlashAttribute(FLASH_SUCCESS, "Customer changes have been saved!");

            return "redirect:/administrator/customers/view/" + customer.getKey();
        } catch (CustomerInvalidException | CustomerNotFoundException e) {
            redirect.addFlashAttribute(OBJ_CUSTOMER, customer);
            redirect.addFlashAttribute(FLASH_ERROR, e.getMessage());

            return "redirect:/administrator/customers/edit/" + customer.getKey();
        }
    }

    /**
     * Deletes a existing customer. It will show an error when the customer that would be deleted already ordered
     * some products. Because in this case it is not possible to only delete the customer, without also deleting the
     * orders.
     *
     * @param redirect RedirectAttributes for the user feedback
     * @param key      Vendor model
     *
     * @return redirect
     */
    @GetMapping({"/delete/{key}","/delete/{key}/"})
    public String delete(RedirectAttributes redirect, @PathVariable String key) {
        try {
            Customer customer = customerService.getByKey(key);
            customerService.delete(customer);

            redirect.addFlashAttribute(FLASH_SUCCESS, "Customer with name " + customer.getName() + " has been deleted!");

            return "redirect:/administrator/customers/";
        } catch (CustomerNotFoundException e) {
            redirect.addFlashAttribute(FLASH_ERROR, e.getMessage());

            return "redirect:/administrator/customers/";
        }
    }
}
