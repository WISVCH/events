package ch.wisv.events.app.controller.sales.order;

import ch.wisv.events.app.request.ScanProductRequest;
import ch.wisv.events.core.model.order.Customer;
import ch.wisv.events.core.model.order.SoldProduct;
import ch.wisv.events.core.model.order.SoldProductStatus;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.service.customer.CustomerService;
import ch.wisv.events.core.service.product.ProductService;
import ch.wisv.events.core.service.product.SoldProductService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

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
@RequestMapping("/sales/scan")
@PreAuthorize("hasRole('USER')")
public class SalesScannerController {

    /**
     * Field soldProductService
     */
    private final SoldProductService soldProductService;


    /**
     * Field customerService
     */
    private final CustomerService customerService;

    /**
     * Field productService
     */
    private final ProductService productService;

    /**
     * Constructor SalesScannerController creates a new SalesScannerController instance.
     *
     * @param soldProductService of type SoldProductService
     * @param customerService    of type CustomerService
     * @param productService     of type ProductService
     */
    public SalesScannerController(SoldProductService soldProductService, CustomerService customerService,
                                  ProductService productService) {
        this.soldProductService = soldProductService;
        this.customerService = customerService;
        this.productService = productService;
    }

    /**
     * Method checkView ...
     *
     * @return String
     */
    @GetMapping("/check/")
    public String checkView(Model model) {
        if (model.containsAttribute("scanned")) {
            return "sales/scan/check";
        }
        return "redirect:/sales/scan/";
    }

    /**
     * Method check ...
     *
     * @param redirect of type RedirectAttributes
     * @param request  of type ScanProductRequest
     * @return String
     */
    @PostMapping("/check")
    public String check(RedirectAttributes redirect, @ModelAttribute @Validated ScanProductRequest request) {
        try {
            Customer customer = customerService.getByRFIDToken(request.getRfidToken());
            Product product = productService.getByKey(request.getProductKey());

            List<SoldProduct> list = soldProductService.getByCustomerAndProduct(customer, product);
            if (list.size() == 0) {
                redirect.addFlashAttribute("scanned", "error");
            } else {
                boolean correctScanned = false;
                for (SoldProduct soldProduct : list) {
                    if (soldProduct.getStatus() == SoldProductStatus.OPEN) {
                        soldProduct.setStatus(SoldProductStatus.SCANNED);
                        soldProductService.update(soldProduct);
                        correctScanned = true;
                        break;
                    }
                }
                redirect.addFlashAttribute("scanned", (correctScanned) ? "correct" : "already");
            }

            redirect.addFlashAttribute("customer", customer);
            return "redirect:/sales/scan/check/";
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/sales/scan/";
        }
    }
}
