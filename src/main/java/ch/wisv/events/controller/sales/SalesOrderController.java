package ch.wisv.events.controller.sales;

import ch.wisv.events.data.model.order.Customer;
import ch.wisv.events.data.model.order.Order;
import ch.wisv.events.data.request.sales.SalesCustomerAddRequest;
import ch.wisv.events.data.request.sales.SalesOrderRequest;
import ch.wisv.events.exception.CustomerNotFound;
import ch.wisv.events.exception.OrderNotFound;
import ch.wisv.events.exception.ProductLimitExceededException;
import ch.wisv.events.service.order.CustomerService;
import ch.wisv.events.service.order.OrderService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
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
     * Constructor SalesOrderController creates a new SalesOrderController instance.
     *
     * @param orderService    of type OrderService
     * @param customerService of type CustomerService
     */
    public SalesOrderController(OrderService orderService, CustomerService customerService) {
        this.orderService = orderService;
        this.customerService = customerService;
    }

    /**
     * Method createOrder creates an new order using the SalesOrderRequest.
     *
     * @param redirectAttributes of type RedirectAttributes
     * @param salesOrderRequest  of type SalesOrderRequest
     * @return String
     */
    @PostMapping("/create")
    public String createOrder(RedirectAttributes redirectAttributes,
                              @ModelAttribute @Validated SalesOrderRequest salesOrderRequest) {
        try {
            Order order = orderService.create(salesOrderRequest);
            redirectAttributes.addFlashAttribute("reference", order.getPublicReference());

            return "redirect:/sales/scan/";
        } catch (ProductLimitExceededException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());

            return "redirect:/sales/overview/";
        }
    }

    /**
     * Method addUserToOrder adds a customer to an Order.
     *
     * @param redirectAttributes      of type RedirectAttributes
     * @param salesCustomerAddRequest of type SalesCustomerAddRequest
     * @return String
     */
    @PostMapping("/customer/add")
    public String addUserToOrder(RedirectAttributes redirectAttributes, @ModelAttribute @Validated
            SalesCustomerAddRequest salesCustomerAddRequest) {
        try {
            Order order = orderService.getByReference(salesCustomerAddRequest.getOrderReference());
            Customer customer = customerService.getByRFIDToken(salesCustomerAddRequest.getRfidToken());
            orderService.addCustomerToOrder(order, customer);
            redirectAttributes.addFlashAttribute("reference", order.getPublicReference());

            return "redirect:/sales/payment/";
        } catch (OrderNotFound e) {
            redirectAttributes.addFlashAttribute("error", "Order does not exists!");

            return "redirect:/sales/overview/";
        } catch (CustomerNotFound e) {
            redirectAttributes.addFlashAttribute("orderUser", salesCustomerAddRequest);

            return "redirect:/sales/customer/create/";
        }
    }
}
