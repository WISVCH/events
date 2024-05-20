package ch.wisv.events.admin.controller;

import ch.wisv.events.core.exception.normal.ProductInvalidException;
import ch.wisv.events.core.exception.normal.ProductNotFoundException;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.model.webhook.WebhookTrigger;
import ch.wisv.events.core.service.product.ProductService;
import ch.wisv.events.core.service.ticket.TicketService;
import ch.wisv.events.core.webhook.WebhookPublisher;
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
 * DashboardProductController.
 */
@Controller
@RequestMapping({"/administrator/products","/administrator/products/"})
@PreAuthorize("hasRole('ADMIN')")
public class DashboardProductController extends DashboardController {

    /** ProductService. */
    private final ProductService productService;

    /** TicketService. */
    private final TicketService ticketService;

    /** WebhookPublisher. */
    private final WebhookPublisher webhookPublisher;

    /**
     * DashboardProductController constructor.
     *
     * @param productService   of type ProductService.
     * @param ticketService    of type TicketService.
     * @param webhookPublisher of type WebhookPublisher.
     */
    @Autowired
    public DashboardProductController(
            ProductService productService, TicketService ticketService, WebhookPublisher webhookPublisher
    ) {
        this.productService = productService;
        this.ticketService = ticketService;
        this.webhookPublisher = webhookPublisher;
    }

    /**
     * Get request for ProductOverview.
     *
     * @param model SpringUI model
     *
     * @return thymeleaf template path
     */
    @GetMapping
    public String index(Model model) {
        model.addAttribute(OBJ_PRODUCTS, this.productService.getAllProducts());

        return "admin/products/index";
    }

    /**
     * Get request for showing Product overview.
     *
     * @param model    of type Model
     * @param redirect of type RedirectAttributes
     * @param key      of type String
     *
     * @return String
     */
    @GetMapping({"/view/{key}","/view/{key}"})
    public String view(Model model, RedirectAttributes redirect, @PathVariable String key) {
        try {
            model.addAttribute(OBJ_PRODUCT, productService.getByKey(key));

            return "admin/products/view";
        } catch (ProductNotFoundException e) {
            redirect.addFlashAttribute(FLASH_ERROR, e.getMessage());

            return "redirect:/administrator/products/";
        }
    }

    /**
     * Get request to create a Product.
     *
     * @param model SpringUI model
     *
     * @return thymeleaf template path
     */
    @GetMapping({"/create","/create/"})
    public String create(Model model) {
        if (!model.containsAttribute(OBJ_PRODUCT)) {
            model.addAttribute(OBJ_PRODUCT, new Product());
        }

        return "admin/products/product";
    }

    /**
     * Post request to create a Product.
     *
     * @param product  Product product attr.
     * @param redirect Spring RedirectAttributes
     *
     * @return redirect
     */
    @PostMapping({"/create","/create/"})
    public String create(RedirectAttributes redirect, @ModelAttribute Product product) {
        try {
            if (product.getRedirectUrl() != null && product.getRedirectUrl().length() == 0){
               product.setRedirectUrl(null);
            }
            productService.create(product);
            webhookPublisher.createWebhookTask(WebhookTrigger.PRODUCT_CREATE_UPDATE, product);
            redirect.addFlashAttribute(FLASH_SUCCESS, "Product with title " + product.getTitle() + " has been created!");

            return "redirect:/administrator/products/view/" + product.getKey();
        } catch (ProductInvalidException e) {
            redirect.addFlashAttribute(FLASH_ERROR, e.getMessage());
            redirect.addFlashAttribute(OBJ_PRODUCT, product);

            return "redirect:/administrator/products/create/";
        }
    }

    /**
     * Get request to edit a Product or if the key does not exists it will redirect to the
     * Product Overview page.
     *
     * @param model    of type Model
     * @param redirect of type RedirectAttributes
     * @param key      of type String
     *
     * @return thymeleaf template path
     */
    @GetMapping({"/edit/{key}","/edit/{key}/"})
    public String edit(Model model, RedirectAttributes redirect, @PathVariable String key) {
        try {
            if (!model.containsAttribute(OBJ_PRODUCT)) {
                model.addAttribute(OBJ_PRODUCT, productService.getByKey(key));
            }

            return "admin/products/product";
        } catch (ProductNotFoundException e) {
            redirect.addFlashAttribute(FLASH_ERROR, e.getMessage());

            return "redirect:/administrator/products/";
        }
    }

    /**
     * Method edit post request to update an existing Product.
     *
     * @param redirect of type RedirectAttributes
     * @param product  of type Product
     * @param key      of type String
     *
     * @return String
     */
    @PostMapping({"/edit/{key}","/edit/{key}/"})
    public String update(RedirectAttributes redirect, @ModelAttribute Product product, @PathVariable String key) {
        try {
            product.setKey(key);
            if (product.getRedirectUrl() != null && product.getRedirectUrl().length() == 0){
                product.setRedirectUrl(null);
            }
            productService.update(product);
            webhookPublisher.createWebhookTask(WebhookTrigger.PRODUCT_CREATE_UPDATE, product);
            redirect.addFlashAttribute(FLASH_SUCCESS, "Product changes have been saved!");

            return "redirect:/administrator/products/view/" + product.getKey();
        } catch (ProductNotFoundException | ProductInvalidException e) {
            redirect.addFlashAttribute(FLASH_ERROR, e.getMessage());
            redirect.addFlashAttribute(OBJ_PRODUCT, product);

            return "redirect:/administrator/products/edit/" + product.getKey();
        }
    }

    /**
     * Method overview will show a list of the users with this product.
     *
     * @param model    of type Model
     * @param redirect of type RedirectAttributes
     * @param key      of type String
     *
     * @return String
     */
    @GetMapping({"/overview/{key}","/overview/{key}/"})
    public String overview(Model model, RedirectAttributes redirect, @PathVariable String key) {
        try {
            Product product = productService.getByKey(key);

            model.addAttribute(OBJ_TICKETS, ticketService.getAllByProduct(product));
            model.addAttribute(OBJ_PRODUCT, product);

            return "admin/products/overview";
        } catch (ProductNotFoundException e) {
            redirect.addFlashAttribute(FLASH_ERROR, e.getMessage());

            return "redirect:/administrator/products/";
        }
    }

    /**
     * Get request to delete a Product.
     *
     * @param redirectAttributes Spring RedirectAttributes
     * @param key                key of a Product
     *
     * @return redirect
     */
    @GetMapping({"/delete/{key}","/delete/{key}/"})
    public String delete(RedirectAttributes redirectAttributes, @PathVariable String key) {
        try {
            Product product = productService.getByKey(key);
            productService.delete(product);
            webhookPublisher.createWebhookTask(WebhookTrigger.PRODUCT_DELETE, product);

            redirectAttributes.addFlashAttribute(FLASH_SUCCESS, "Product " + product.getTitle() + " has been deleted!");
        } catch (ProductNotFoundException e) {
            redirectAttributes.addFlashAttribute(FLASH_ERROR, e.getMessage());
        }

        return "redirect:/administrator/products/";
    }
}
