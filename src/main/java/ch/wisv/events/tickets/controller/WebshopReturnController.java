package ch.wisv.events.tickets.controller;

import ch.wisv.events.core.exception.normal.OrderNotFoundException;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.service.order.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.util.ArrayUtils;

@Controller
@RequestMapping("/return/{key}")
public class WebshopReturnController {

    public final OrderService orderService;

    public WebshopReturnController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public String returnIndex(RedirectAttributes redirect, @PathVariable String key) {
        try {
            Order order = orderService.getByReference(key);
            switch (order.getStatus()) {
                case PAID:
                    return "redirect:/return/" + order.getPublicReference() + "/success";
                case CANCELLED:
                    return "redirect:/return/" + order.getPublicReference() + "/cancelled";
                case ERROR:
                    return "redirect:/return/" + order.getPublicReference() + "/error";
                case RESERVATION:
                    return "redirect:/return/" + order.getPublicReference() + "/reservation";
            }

            return "webshop/return/error";
        } catch (OrderNotFoundException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/";
        }
    }

    @GetMapping("/{status}")
    public String returnSuccess(Model model, RedirectAttributes redirect, @PathVariable String key, @PathVariable String status) {
        try {
            Order order = orderService.getByReference(key);
            model.addAttribute("order", order);

            String[] validStatus = new String[]{"success", "cancelled", "error", "reservation"};

            if (ArrayUtils.contains(validStatus, status)) {
                return "webshop/return/" + status;
            }

            return "webshop/return/error";
        } catch (OrderNotFoundException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/";
        }
    }
}
