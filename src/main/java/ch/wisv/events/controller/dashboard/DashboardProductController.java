package ch.wisv.events.controller.dashboard;

import ch.wisv.events.data.factory.product.ProductRequestFactory;
import ch.wisv.events.data.model.product.Product;
import ch.wisv.events.data.request.event.EventRequest;
import ch.wisv.events.data.request.product.ProductRequest;
import ch.wisv.events.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Copyright (c) 2016  W.I.S.V. 'Christiaan Huygens'
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

    @GetMapping("/edit/{key}")
    public String editEventView(Model model, @PathVariable String key) {
        Product product = productService.getProductByKey(key);
        if (product == null) {
            return "redirect:/dashboard/events/";
        }

        model.addAttribute("product", ProductRequestFactory.create(product));

        return "dashboard/products/edit";
    }

    @PostMapping("/update")
    public String editEvent(Model model, @ModelAttribute @Validated ProductRequest productRequest,
                            RedirectAttributes redirectAttributes) {
        productService.updateProduct(productRequest);
        redirectAttributes.addFlashAttribute("message", "Autosaved!");

        return "redirect:/dashboard/products/edit/" + productRequest.getKey();
    }

}
