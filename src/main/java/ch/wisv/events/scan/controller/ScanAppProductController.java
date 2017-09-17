package ch.wisv.events.scan.controller;

import ch.wisv.events.core.exception.EventsModelNotFound;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.service.product.ProductService;
import ch.wisv.events.scan.object.ScanResult;
import ch.wisv.events.scan.service.ScanAppSoldProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
     * Constructor ScanAppMainController creates a new ScanAppMainController instance.
     *
     * @param productService            of type ProductService.
     * @param scanAppSoldProductService of type ScanAppSoldProductService.
     */
    @Autowired
    public ScanAppProductController(ProductService productService, ScanAppSoldProductService scanAppSoldProductService) {
        this.productService = productService;
        this.scanAppSoldProductService = scanAppSoldProductService;
    }

    @GetMapping("/{productKey}/")
    public String rfid(Model model, RedirectAttributes redirect, @PathVariable String productKey) {
        try {
            Product product = this.productService.getByKey(productKey);
            model.addAttribute("product", product);

            return "scan/product/rfid";
        } catch (EventsModelNotFound e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/scan/";
        }
    }

    @PostMapping("/{productKey}/")
    public String rfid(RedirectAttributes redirect,
            @PathVariable String productKey,
            @RequestParam(value = "rfidToken") String rfidToken,
            @RequestParam(value = "uniqueCode") String uniqueCode
    ) {
        Product product = this.productService.getByKey(productKey);

        if (uniqueCode.equals("")) {
            return this.handleRfidTokenRequest(product, rfidToken);
        } else if (!uniqueCode.equals("")) {
            return this.handleUniqueCodeRequest(redirect, product, uniqueCode);
        } else {
            redirect.addFlashAttribute("error", "Invalid request");

            return "redirect:/scan/product/" + productKey + "/";
        }
    }

    private String handleRfidTokenRequest(Product product, String rfidToken) {
        return "redirect:/scan/product/" + product.getKey() + "/select/";
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
        ScanResult result = this.scanAppSoldProductService.scanProductWithUniqueCode(product, uniqueCode);
        redirect.addFlashAttribute("result", result);
        redirect.addFlashAttribute("product", product);

        return "redirect:/scan/result/";
    }
}
