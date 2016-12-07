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

    /**
     * Field orderService
     */
    private final OrderService orderService;

    /**
     * Constructor SalesPaymentController creates a new SalesPaymentController instance.
     *
     * @param orderService of type OrderService
     */
    public SalesPaymentController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Method paymentOrder shows view with order overview and the button how to pay.
     *
     * @param model              of type Model
     * @param redirectAttributes of type RedirectAttributes
     * @return String
     */
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

    /**
     * Method orderPayment will process the orders and the payment method and will the redirect to the method to the
     * given payment method.
     *
     * @param redirectAttributes of type RedirectAttributes
     * @param payment            of type String
     * @param publicReference    of type String
     * @return String
     */
    @PostMapping("")
    public String orderPayment(RedirectAttributes redirectAttributes,
                               @RequestParam(value = "payment") String payment,
                               @RequestParam(value = "publicReference") String publicReference) {
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

    /**
     * Method paymentCash will handle cash payment.
     *
     * @param model              of type Model
     * @param redirectAttributes of type RedirectAttributes
     * @return String
     */
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

    /**
     * Method paymentCancel will handle cancel payment, so the order will be cancelled.
     *
     * @param model              of type Model
     * @param redirectAttributes of type RedirectAttributes
     * @return String
     */
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
