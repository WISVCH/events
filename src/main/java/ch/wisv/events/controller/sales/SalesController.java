package ch.wisv.events.controller.sales;

import ch.wisv.connect.common.model.CHUserInfo;
import ch.wisv.events.data.model.order.Order;
import ch.wisv.events.data.model.product.Product;
import ch.wisv.events.data.model.sales.Vendor;
import ch.wisv.events.data.request.sales.SalesCustomerAddRequest;
import ch.wisv.events.data.request.sales.SalesOrderRequest;
import ch.wisv.events.exception.OrderNotFound;
import ch.wisv.events.service.order.OrderService;
import ch.wisv.events.service.sales.VendorService;
import org.mitre.openid.connect.model.OIDCAuthenticationToken;
import org.mitre.openid.connect.model.UserInfo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    private final VendorService vendorService;

    private final OrderService orderService;

    public SalesController(VendorService vendorService, OrderService orderService) {
        this.vendorService = vendorService;
        this.orderService = orderService;
    }

    @GetMapping("/")
    public String index(OIDCAuthenticationToken auth) {
        if (this.getGrantedProducts(auth).size() > 0) return "redirect:/sales/overview/";

        return "sales/index";
    }

    @GetMapping("/overview/")
    public String overviewIndex(OIDCAuthenticationToken auth, Model model) {
        List<Product> products = this.getGrantedProducts(auth);

        if (products.size() == 0) return "redirect:/sales/";

        products = products.stream().filter(x -> x.getSold() < x.getMaxSold()).collect(Collectors.toList());

        model.addAttribute("products", products);
        model.addAttribute("orderRequest", new SalesOrderRequest());

        return "sales/overview";
    }

    @GetMapping("/scan/")
    public String scanRFID(RedirectAttributes redirectAttributes, Model model) {
        try {
            Order order = orderService.getByReference((String) model.asMap().get("reference"));
            model.addAttribute("order", order);
            model.addAttribute("orderUserRequest", new SalesCustomerAddRequest(order.getPublicReference()));

            return "sales/scan";
        } catch (OrderNotFound e) {
            redirectAttributes.addFlashAttribute("error", "Order does not exists!");

            return "redirect:/sales/overview/";
        }
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
                                                .filter(x -> x.getStartingTime()
                                                              .isBefore(LocalDateTime.now()))
                                                .filter(x -> x.getEndingTime()
                                                              .isAfter(LocalDateTime.now()))
                                                .filter(x -> info.getLdapGroups().stream()
                                                                 .anyMatch(g -> g.equals(x.getLdapGroup
                                                                         ().getName())))
                                                .collect(Collectors.toCollection(ArrayList::new));
            ArrayList<Product> products = new ArrayList<>();
            vendors.forEach(x -> x.getEvents().forEach(y -> products.addAll(y.getProducts())));

            return products;
        }
        return new ArrayList<>();
    }
}
