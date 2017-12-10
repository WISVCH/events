package ch.wisv.events.scan.controller;

import ch.wisv.events.core.exception.normal.CustomerNotFoundException;
import ch.wisv.events.core.exception.normal.ProductNotFoundException;
import ch.wisv.events.core.model.order.Customer;
import ch.wisv.events.core.model.order.SoldProduct;
import ch.wisv.events.core.model.order.SoldProductStatus;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.service.customer.CustomerService;
import ch.wisv.events.core.service.product.ProductService;
import ch.wisv.events.scan.object.ScanResult;
import ch.wisv.events.scan.service.ScanAppSoldProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

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
@PreAuthorize("hasRole('USER')")
@RequestMapping("/scan/product")
public class ScanAppProductController {


    /**
     * Field productService
     */
    private final ProductService productService;

    /**
     * Field scanAppSoldProductService
     */
    private final ScanAppSoldProductService scanAppSoldProductService;

    /**
     * Field customerService
     */
    private final CustomerService customerService;

    /**
     * Constructor ScanAppMainController creates a new ScanAppMainController instance.
     *
     * @param productService            of type ProductService.
     * @param scanAppSoldProductService of type ScanAppSoldProductService.
     * @param customerService           of type CustomerService.
     */
    @Autowired
    public ScanAppProductController(ProductService productService,
            ScanAppSoldProductService scanAppSoldProductService,
            CustomerService customerService
    ) {
        this.productService = productService;
        this.scanAppSoldProductService = scanAppSoldProductService;
        this.customerService = customerService;
    }

    /**
     * Method rfid.
     *
     * @param model      of type Model
     * @param redirect   of type RedirectAttributes
     * @param productKey of type String
     * @return String
     */
    @GetMapping("/{productKey}/")
    public String rfid(Model model, RedirectAttributes redirect, @PathVariable String productKey) {
        try {
            Product product = productService.getByKey(productKey);
            model.addAttribute("product", product);

            return "scan/product/rfid";
        } catch (ProductNotFoundException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/scan/";
        }
    }

    /**
     * Method rfid ...
     *
     * @param redirect   of type RedirectAttributes
     * @param productKey of type String
     * @param rfidToken  of type String
     * @param uniqueCode of type String
     * @return String
     */
    @PostMapping("/{productKey}/")
    public String rfid(RedirectAttributes redirect,
            @PathVariable String productKey,
            @RequestParam(value = "rfidToken") String rfidToken,
            @RequestParam(value = "uniqueCode") String uniqueCode
    ) {
        try {
            Product product = productService.getByKey(productKey);

            if (uniqueCode.equals("")) {
                return this.handleRfidTokenRequest(redirect, product, rfidToken);
            } else if (!uniqueCode.equals("")) {
                return this.handleUniqueCodeRequest(redirect, product, uniqueCode);
            } else {
                redirect.addFlashAttribute("error", "Invalid request");

                return "redirect:/scan/product/" + productKey + "/";
            }
        } catch (ProductNotFoundException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/scan/";
        }
    }

    /**
     * Method select ...
     *
     * @param model      of type Model
     * @param productKey of type String
     * @return String
     */
    @GetMapping("/{productKey}/select/{customerKey}/")
    public String select(Model model, RedirectAttributes redirect, @PathVariable String productKey, @PathVariable String customerKey) {
        try {
            Product product = productService.getByKey(productKey);
            Customer customer = customerService.getByKey(customerKey);

            List<SoldProduct> soldProducts = this.scanAppSoldProductService.getAllByProductAndCustomer(product, customer);
            soldProducts = soldProducts.stream().filter(x -> x.getStatus() != SoldProductStatus.SCANNED).collect(Collectors.toList());

            model.addAttribute("product", product);
            model.addAttribute("soldProducts", soldProducts);

            return "scan/product/select";
        } catch (ProductNotFoundException | CustomerNotFoundException e) {
            redirect.addFlashAttribute("message", e.getMessage());

            return "redirect:/scan/";
        }
    }

    /**
     * Method select ...
     *
     * @param redirect     of type RedirectAttributes
     * @param productKey   of type String
     * @param customerKey  of type String
     * @param soldProducts of type List<SoldProduct>
     * @return String
     */
    @PostMapping("/{productKey}/select/{customerKey}/")
    public String select(RedirectAttributes redirect,
            @PathVariable String productKey,
            @PathVariable String customerKey,
            @RequestParam(value = "soldProducts") List<SoldProduct> soldProducts
    ) {
        try {
            Product product = productService.getByKey(productKey);
            Customer customer = customerService.getByKey(customerKey);

            ScanResult result = null;
            for (SoldProduct soldProduct : soldProducts) {
                ScanResult temp = scanAppSoldProductService.scanSoldProduct(soldProduct);

                if (result != ScanResult.ALREADY_SCANNED) {
                    result = temp;
                }
            }

            return this.createRedirect(redirect, product, result, customer);
        } catch (ProductNotFoundException | CustomerNotFoundException e) {
            redirect.addFlashAttribute("message", e.getMessage());

            return "redirect:/scan/";
        }
    }

    /**
     * Handle a request that uses a rfid token to scan a ticket.
     *
     * @param redirect  of type RedirectAttributes
     * @param product   of type Product
     * @param rfidToken of type String
     * @return String
     */
    private String handleRfidTokenRequest(RedirectAttributes redirect, Product product, String rfidToken) {
        try {
            Customer customer = customerService.getByRfidToken(rfidToken);
            ScanResult result = scanAppSoldProductService.scanByProductAndCustomer(product, customer);

            if (result == ScanResult.MULTIPLE_PRODUCT) {
                return "redirect:/scan/product/" + product.getKey() + "/select/" + customer.getKey() + "/";
            } else {
                return this.createRedirect(redirect, product, result, customer);
            }
        } catch (CustomerNotFoundException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            redirect.addFlashAttribute("product", product);

            return "redirect:/scan/result/";
        }
    }

    /**
     * Handle a request that uses a unique code to scan a ticket.
     *
     * @param redirect   of type RedirectAttributes
     * @param product    of type Product
     * @param uniqueCode of type String
     * @return String
     */
    private String handleUniqueCodeRequest(RedirectAttributes redirect, Product product, String uniqueCode) {
        ScanResult result = scanAppSoldProductService.scanByProductAndUniqueCode(product, uniqueCode);

        return this.createRedirect(redirect, product, result, null);
    }

    /**
     * Create default redirect response.
     *
     * @param redirect of type RedirectAttributes
     * @param product  of type Product
     * @param result   of type ScanResult
     * @param customer of type Customer
     * @return String
     */
    private String createRedirect(RedirectAttributes redirect, Product product, ScanResult result, Customer customer) {
        redirect.addFlashAttribute("result", result);
        redirect.addFlashAttribute("product", product);
        redirect.addFlashAttribute("customer", customer);

        return "redirect:/scan/result/";
    }
}
