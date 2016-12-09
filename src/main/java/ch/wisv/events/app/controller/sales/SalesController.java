package ch.wisv.events.app.controller.sales;

import ch.wisv.connect.common.model.CHUserInfo;
import ch.wisv.events.app.request.OrderRequest;
import ch.wisv.events.app.request.ScanProductRequest;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.model.sales.Vendor;
import ch.wisv.events.core.service.vendor.VendorService;
import org.mitre.openid.connect.model.OIDCAuthenticationToken;
import org.mitre.openid.connect.model.UserInfo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
@RequestMapping("/sales")
public class SalesController {

    /**
     * Field vendorService
     */
    private final VendorService vendorService;

    /**
     * Constructor SalesController creates a new SalesController instance.
     *
     * @param vendorService of type VendorService
     */
    public SalesController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    /**
     * Method index shows the index and check if the user has granted products.
     *
     * @return String
     */
    @GetMapping("/")
    public String index() {
        return "sales/index";
    }


    /**
     * Method overviewIndex shows the products that the user is allowed to sell.
     *
     * @param auth  of type OIDCAuthenticationToken
     * @param model of type Model
     * @return String
     */
    @GetMapping("/order/")
    public String orderIndex(OIDCAuthenticationToken auth, Model model) {
        List<Product> products = this.getGrantedProducts(auth);

        if (products.size() == 0) return "redirect:/sales/";

        products = products.stream().filter(x -> x.getMaxSold() == null || x.getSold() < x.getMaxSold()).collect
                (Collectors.toList());

        model.addAttribute("products", products);
        model.addAttribute("orderRequest", new OrderRequest());

        return "sales/order/index";
    }

    /**
     * Method scanIndex shows the event your are allowed to scan.
     *
     * @param auth of type OIDCAuthenticationToken
     * @param model of type Model
     * @return String
     */
    @GetMapping("/scan/")
    public String scanIndex(OIDCAuthenticationToken auth, Model model) {
        List<Product> products = this.getGrantedProducts(auth);

        if (products.size() == 0) return "redirect:/sales/";

        model.addAttribute("products", products);
        model.addAttribute("scanProduct", new ScanProductRequest());

        return "sales/scan/index";
    }

    /**
     * Return list of granted products to sell for LDAP group
     *
     * @param auth OIDCAuthenticationToken
     * @return List of Products
     */
    private List<Product> getGrantedProducts(OIDCAuthenticationToken auth) {
        UserInfo userInfo = auth.getUserInfo();
        if (userInfo instanceof CHUserInfo) {
            CHUserInfo info = (CHUserInfo) userInfo;
            List<Vendor> vendors = vendorService.getAll().stream()
                    .filter(x -> info.getLdapGroups().stream().anyMatch(g -> g.equals(x.getLdapGroup().getName())))
                    .filter(x -> x.getStartingTime() == null || x.getStartingTime().isBefore(LocalDateTime.now()))
                    .filter(x -> x.getEndingTime() == null || x.getEndingTime().isAfter(LocalDateTime.now()))
                    .collect(Collectors.toCollection(ArrayList::new));
            ArrayList<Product> products = new ArrayList<>();
            vendors.forEach(x -> x.getEvents().forEach(p -> products.addAll(p.getProducts())));

            return products;
        }
        return new ArrayList<>();
    }

}
