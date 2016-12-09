package ch.wisv.events.app.controller.sales.order;

import ch.wisv.events.app.request.CustomerAddRequest;
import ch.wisv.events.app.request.OrderRequest;
import ch.wisv.events.core.exception.CustomerNotFound;
import ch.wisv.events.core.exception.OrderNotFound;
import ch.wisv.events.core.exception.ProductLimitExceededException;
import ch.wisv.events.core.model.order.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.service.customer.CustomerService;
import ch.wisv.events.core.service.order.OrderService;
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
@RequestMapping("/sales/order")
public class SalesOrderController {

    /**
     * Field orderService
     */
    private final OrderService orderService;

    /**
     * Field customerService
     */
    private final CustomerService customerService;


    /**
     * Field soldProductService
     */
    private final SoldProductService soldProductService;

    /**
     * Constructor SalesOrderController creates a new SalesOrderController instance.
     *
     * @param orderService       of type OrderService
     * @param customerService    of type CustomerService
     * @param soldProductService of type SoldProductService
     */
    public SalesOrderController(OrderService orderService, CustomerService customerService,
                                SoldProductService soldProductService) {
        this.orderService = orderService;
        this.customerService = customerService;
        this.soldProductService = soldProductService;
    }

    /**
     * Method scanRFID shows view to scan the RFID.
     *
     * @param redirect of type RedirectAttributes
     * @param model    of type Model
     * @return String
     */
    @GetMapping("/scan/")
    public String scanRFID(Model model, RedirectAttributes redirect) {
        try {
            Order order = orderService.getByReference((String) model.asMap().get("reference"));
            model.addAttribute("order", order);
            model.addAttribute("orderUserRequest", new CustomerAddRequest(order.getPublicReference()));

            return "sales/order/scan";
        } catch (OrderNotFound e) {
            redirect.addFlashAttribute("error", "Order does not exists!");

            return "redirect:/sales/order/";
        }
    }

    /**
     * Method createOrder creates an new order using the OrderRequest.
     *
     * @param redirect of type RedirectAttributes
     * @param request  of type OrderRequest
     * @return String
     */
    @PostMapping("/create")
    public String createOrder(RedirectAttributes redirect,
                              @ModelAttribute @Validated OrderRequest request) {
        try {
            Order order = orderService.create(request);
            redirect.addFlashAttribute("reference", order.getPublicReference());

            return "redirect:/sales/order/scan/";
        } catch (ProductLimitExceededException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/sales/order/";
        }
    }

    /**
     * Method addUserToOrder adds a customer to an Order.
     *
     * @param redirect of type RedirectAttributes
     * @param request  of type CustomerAddRequest
     * @return String
     */
    @PostMapping("/customer/add")
    public String addUserToOrder(RedirectAttributes redirect, @ModelAttribute @Validated CustomerAddRequest request) {
        try {
            Order order = orderService.getByReference(request.getOrderReference());
            Customer customer = customerService.getByRFIDToken(request.getRfidToken());

            for (Product product : order.getProducts()) {
                if (soldProductService.getByCustomerAndProduct(customer, product).size() > 0) {
                    redirect.addFlashAttribute("warning", "Customer " + customer.getName() + " already bought product" +
                            " " + product.getTitle() + ".");
                    return "redirect:/sales/order/";
                }
            }

            orderService.addCustomerToOrder(order, customer);
            redirect.addFlashAttribute("reference", order.getPublicReference());

            return "redirect:/sales/order/payment/";
        } catch (OrderNotFound e) {
            redirect.addFlashAttribute("error", "Order does not exists!");

            return "redirect:/sales/order/";
        } catch (CustomerNotFound e) {
            redirect.addFlashAttribute("orderUser", request);

            return "redirect:/sales/order/customer/create/";
        }
    }
}
