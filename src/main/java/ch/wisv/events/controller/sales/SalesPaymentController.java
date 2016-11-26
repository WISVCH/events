package ch.wisv.events.controller.sales;

import ch.wisv.events.data.model.order.Order;
import ch.wisv.events.data.model.order.OrderStatus;
import ch.wisv.events.data.model.sales.PaymentOptions;
import ch.wisv.events.exception.OrderNotFound;
import ch.wisv.events.service.order.OrderService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
@RequestMapping("/sales/payment")
public class SalesPaymentController {

    private final OrderService orderService;

    public SalesPaymentController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/")
    public String paymentOrder(Model model, RedirectAttributes redirectAttributes) {
        try {
            Order order = orderService.getByReference((String) model.asMap().get("reference"));

            model.addAttribute("order", order);
            model.addAttribute("orderPaymentRequest", null);
        } catch (OrderNotFound e) {
            redirectAttributes.addFlashAttribute("error", "Order does not exists!");

            return "redirect:/sales/overview/";
        }

        return "sales/payment";
    }

    @PostMapping("")
    public String orderPayment(RedirectAttributes redirectAttributes,
                               @RequestParam(value = "payment", required = true) String payment,
                               @RequestParam(value = "publicReference", required = true) String publicReference) {
        try {
            Order order = orderService.getByReference(publicReference);
            if (PaymentOptions.getStatusByValue(payment) != OrderStatus.REJECTED) {
                redirectAttributes.addFlashAttribute("reference", publicReference);

                return "redirect:/sales/payment/" + payment + "/";
            }
            orderService.updateOrderStatus(order, OrderStatus.REJECTED);
            redirectAttributes.addFlashAttribute("error", "Payment method is not allowed!");

            return "redirect:/sales/overview/";
        } catch (OrderNotFound e) {
            redirectAttributes.addFlashAttribute("error", "Order does not exists!");

            return "redirect:/sales/overview/";
        }
    }

    @GetMapping("/cash/")
    public String paymentCash(Model model, RedirectAttributes redirectAttributes) {
        try {
            Order order = orderService.getByReference((String) model.asMap().get("reference"));
            orderService.updateOrderStatus(order, OrderStatus.PAID_CASH);

            redirectAttributes.addFlashAttribute("message", "Order has successfully been paid by cash!");

            return "redirect:/sales/overview/";
        } catch (OrderNotFound e) {
            redirectAttributes.addFlashAttribute("error", "Order does not exists!");

            return "redirect:/sales/overview/";
        }
    }

    @GetMapping("/cancel/")
    public String paymentCancel(Model model, RedirectAttributes redirectAttributes) {
        try {
            Order order = orderService.getByReference((String) model.asMap().get("reference"));
            orderService.updateOrderStatus(order, OrderStatus.CANCELLED);

            redirectAttributes.addFlashAttribute("warning", "Order has been cancelled!");

            return "redirect:/sales/overview/";
        } catch (OrderNotFound e) {
            redirectAttributes.addFlashAttribute("error", "Order does not exists!");

            return "redirect:/sales/overview/";
        }
    }


}
