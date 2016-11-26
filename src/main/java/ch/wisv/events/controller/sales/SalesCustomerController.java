package ch.wisv.events.controller.sales;

import ch.wisv.events.data.model.order.Customer;
import ch.wisv.events.data.model.order.Order;
import ch.wisv.events.data.request.sales.SalesOrderCustomerCreateRequest;
import ch.wisv.events.data.request.sales.SalesOrderUserRequest;
import ch.wisv.events.exception.OrderNotFound;
import ch.wisv.events.exception.RFIDTokenAlreadyUsedException;
import ch.wisv.events.service.order.CustomerService;
import ch.wisv.events.service.order.OrderService;
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
@RequestMapping("/sales/customer")
@PreAuthorize("hasRole('USER')")
public class SalesCustomerController {

    private final OrderService orderService;

    private final CustomerService customerService;

    public SalesCustomerController(OrderService orderService, CustomerService customerService) {
        this.orderService = orderService;
        this.customerService = customerService;
    }

    @GetMapping("/create/")
    public String createCustomer(Model model, RedirectAttributes redirectAttributes) {
        Object object = model.asMap().get("orderUser");
        if (object instanceof SalesOrderUserRequest) {
            SalesOrderUserRequest user = (SalesOrderUserRequest) object;

            SalesOrderCustomerCreateRequest request = new SalesOrderCustomerCreateRequest();
            request.setOrderReference(user.getOrderReference());
            request.setCustomerRFIDToken(user.getRfidToken());

            model.addAttribute("customerCreate", request);

            return "sales/create-customer";
        }
        redirectAttributes.addFlashAttribute("error", "Order does not exists!");

        return "redirect:/sales/overview/";
    }

    @PostMapping("/create")
    public String createCustomer(RedirectAttributes redirectAttributes,
            @ModelAttribute @Validated SalesOrderCustomerCreateRequest salesOrderCustomerCreateRequest) {
        try {
            Order order = orderService.getByReference(salesOrderCustomerCreateRequest.getOrderReference());
            Customer customer = customerService.createCustomer(salesOrderCustomerCreateRequest);

            orderService.addCustomerToOrder(order, customer);
            redirectAttributes.addFlashAttribute("reference", order.getPublicReference());

            return "redirect:/sales/payment/";
        } catch (OrderNotFound e) {
            redirectAttributes.addFlashAttribute("error", "Order does not exists!");

            return "redirect:/sales/overview/";
        } catch (RFIDTokenAlreadyUsedException e) {
            redirectAttributes.addFlashAttribute("reference", salesOrderCustomerCreateRequest.getOrderReference());

            return "redirect:/create/customer/";
        }
    }

}
