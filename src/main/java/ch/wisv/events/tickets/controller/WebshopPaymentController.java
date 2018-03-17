package ch.wisv.events.tickets.controller;

import ch.wisv.events.core.exception.normal.EventsException;
import ch.wisv.events.core.exception.normal.OrderInvalidException;
import ch.wisv.events.core.exception.normal.OrderNotFoundException;
import ch.wisv.events.core.exception.normal.PaymentsStatusUnknown;
import ch.wisv.events.core.exception.runtime.EventsRuntimeException;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.model.order.PaymentMethod;
import ch.wisv.events.core.service.mail.MailService;
import ch.wisv.events.core.service.order.OrderService;
import ch.wisv.events.core.service.order.OrderValidationService;
import ch.wisv.events.tickets.service.PaymentsService;
import ch.wisv.events.tickets.service.WebshopService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static java.lang.Thread.sleep;

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
@RequestMapping("/checkout/{key}/payment")
public class WebshopPaymentController {

    private final OrderService orderService;

    private final OrderValidationService orderValidationService;

    private final PaymentsService paymentsService;

    private final WebshopService webshopService;

    private final MailService mailService;

    /**
     * Constructor WebshopController.
     *
     * @param orderService           of type OrderService
     * @param orderValidationService of type OrderValidationService
     * @param paymentsService        of type PaymentsService
     * @param webshopService         of type WebshopService
     * @param mailService
     */
    public WebshopPaymentController(OrderService orderService, OrderValidationService orderValidationService, PaymentsService paymentsService, WebshopService webshopService, MailService mailService) {
        this.orderService = orderService;
        this.orderValidationService = orderValidationService;
        this.paymentsService = paymentsService;
        this.webshopService = webshopService;
        this.mailService = mailService;
    }


    @GetMapping("")
    public String checkout(Model model, RedirectAttributes redirect, @PathVariable String key) {
        try {
            Order order = orderService.getByReference(key);
            model.addAttribute("order", order);

            if (order.getStatus() == OrderStatus.PAID || order.getStatus() == OrderStatus.REJECTED) {
                redirect.addFlashAttribute("error", "Order has already been paid or has been rejected.");

                return "redirect:/";
            }

            return "webshop/payment/index";
        } catch (OrderNotFoundException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/";
        }
    }

    @GetMapping("/reservation")
    public String checkoutReservation(Model model, RedirectAttributes redirect, @PathVariable String key) {
        try {
            Order order = orderService.getByReference(key);
            model.addAttribute("model", order);

            orderService.updateOrderStatus(order, OrderStatus.RESERVATION);

            return "redirect:/return/" + order.getPublicReference() + "/reservation";
        } catch (OrderNotFoundException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/";
        } catch (EventsException | EventsRuntimeException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/checkout/" + key + "/payment";
        }
    }


    @GetMapping("/ideal")
    public String checkoutIdeal(Model model, RedirectAttributes redirect, @PathVariable String key) {
        try {
            Order order = orderService.getByReference(key);
            model.addAttribute("model", order);

            order.setPaymentMethod(PaymentMethod.IDEAL);
            order.setStatus(OrderStatus.PENDING);
            orderService.update(order);
            orderValidationService.assertOrderIsValidForPayment(order);

            return "redirect:" + paymentsService.getPaymentsMollieUrl(order);
        } catch (OrderNotFoundException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/";
        } catch (EventsException | EventsRuntimeException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/checkout/" + key + "/payment";
        }
    }

    @GetMapping("/return")
    public String returnMolliePayment(RedirectAttributes redirect, @PathVariable String key, @RequestParam("reference") String paymentsReference) {
        try {
            Order order = orderService.getByReference(key);
            if (order.getStatus() == OrderStatus.PENDING) {
                this.fetchOrderStatus(order, paymentsReference);

                return "redirect:/return/" + order.getPublicReference();
            } else {
                redirect.addFlashAttribute("error", "Order is in an invalid state.");

                return "redirect:/checkout/" + key + "/payment";
            }
        } catch (OrderNotFoundException | OrderInvalidException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/";
        } catch (PaymentsStatusUnknown e) {
            redirect.addFlashAttribute("error", "Something went wrong trying to fetch the payment status.");

            return "redirect:/checkout/" + key + "/payment";
        }
    }

    private void fetchOrderStatus(Order order, String paymentsReference) throws OrderInvalidException, PaymentsStatusUnknown {
        int count = 0;
        int maxCount = 5;

        while (order.getStatus() == OrderStatus.PENDING && count < maxCount) {
            try {
                webshopService.updateOrderStatus(order, paymentsReference);
                sleep(500);
            } catch (InterruptedException ignored) {
            }

            count++;
        }

        if (count == maxCount) {
            orderService.updateOrderStatus(order, OrderStatus.ERROR);
            mailService.sendErrorPaymentOrder(order);
        }
    }
}