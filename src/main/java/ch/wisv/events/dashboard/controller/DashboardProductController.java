package ch.wisv.events.dashboard.controller;

import ch.wisv.events.event.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by sven on 15/10/2016.
 */
@Controller
@RequestMapping("/dashboard/products")
public class DashboardProductController {

    private final ProductService productService;

    @Autowired
    public DashboardProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "dashboard/products/index";
    }

}
