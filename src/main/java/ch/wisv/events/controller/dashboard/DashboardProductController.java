package ch.wisv.events.controller.dashboard;

import ch.wisv.events.data.factory.product.ProductRequestFactory;
import ch.wisv.events.data.model.product.Product;
import ch.wisv.events.data.request.product.ProductRequest;
import ch.wisv.events.service.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * DashboardProductController.
 */
@Controller
@RequestMapping("/dashboard/products")
@PreAuthorize("hasRole('ADMIN')")
public class DashboardProductController {

    /**
     * ProductService
     */
    private final ProductService productService;

    /**
     * Default constructor
     *
     * @param productService ProductService
     */
    @Autowired
    public DashboardProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Get request for ProductOverview
     *
     * @param model SpringUI model
     * @return thymeleaf template path
     */
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("products", productService.getAllProducts());

        return "dashboard/products/index";
    }

    /**
     * Get request to create a Product
     *
     * @param model SpringUI model
     * @return thymeleaf template path
     */
    @GetMapping("/create/")
    public String createProductView(Model model) {
        model.addAttribute("product", new ProductRequest());

        return "dashboard/products/create";
    }

    /**
     * Get request to edit a Product or if the key does not exists it will redirect to the
     * Product Overview page
     *
     * @param model SpringUI model
     * @return thymeleaf template path
     */
    @GetMapping("/edit/{key}")
    public String editProductView(Model model, @PathVariable String key) {
        Product product = productService.getProductByKey(key);
        if (product == null) {
            return "redirect:/dashboard/events/";
        }

        model.addAttribute("product", ProductRequestFactory.create(product));

        return "dashboard/products/edit";
    }

    /**
     * Get request to delete a Product
     *
     * @param redirectAttributes Spring RedirectAttributes
     * @param key                key of a Product
     * @return redirect
     */
    @GetMapping("/delete/{key}")
    public String deleteEvent(RedirectAttributes redirectAttributes, @PathVariable String key) {
        Product product = productService.getProductByKey(key);
        try {
            productService.deleteProduct(product);
            redirectAttributes.addFlashAttribute("message", "Product " + product.getTitle() + " has been deleted!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/dashboard/products/";
    }

    /**
     * Post request to add a Product
     *
     * @param productRequest     ProductRequest model attr.
     * @param redirectAttributes Spring RedirectAttributes
     * @return redirect
     */
    @PostMapping("/add")
    public String createEvent(@ModelAttribute @Validated ProductRequest productRequest, RedirectAttributes
            redirectAttributes) {
        try {
            productService.addProduct(productRequest);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        redirectAttributes.addFlashAttribute("message", productRequest.getTitle() + " successfully created!");

        return "redirect:/dashboard/products/";
    }

    /**
     * Post request to update a Product
     *
     * @param productRequest     ProductRequest model attr.
     * @param redirectAttributes Spring RedirectAttributes
     * @return redirect
     */
    @PostMapping("/update")
    public String editEvent(@ModelAttribute @Validated ProductRequest productRequest,
                            RedirectAttributes redirectAttributes) {
        productService.updateProduct(productRequest);
        redirectAttributes.addFlashAttribute("message", "Autosaved!");

        return "redirect:/dashboard/products/edit/" + productRequest.getKey();
    }

}
