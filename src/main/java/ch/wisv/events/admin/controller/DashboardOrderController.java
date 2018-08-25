package ch.wisv.events.admin.controller;

import ch.wisv.events.core.exception.normal.EventsException;
import ch.wisv.events.core.exception.normal.OrderNotFoundException;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.model.order.PaymentMethod;
import ch.wisv.events.core.service.order.OrderService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(value = "/administrator/orders")
@PreAuthorize("hasRole('ADMIN')")
public class DashboardOrderController {

    /** OrderService. */
    private final OrderService orderService;

    /**
     * DashboardOrderController constructor.
     *
     * @param orderService OrderService
     */
    public DashboardOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Show list of all orders.
     *
     * @param model of type Model
     *
     * @return String
     */
    @GetMapping()
    public String index(Model model) {
        model.addAttribute("orders", this.orderService.getAllOrders());

        return "admin/orders/index";
    }

    /**
     * Get a view of an order.
     *
     * @param model    of type Model
     * @param redirect of type RedirectAttributes
     * @param key      of type String
     *
     * @return String
     */
    @GetMapping("/view/{key}")
    public String edit(Model model, RedirectAttributes redirect, @PathVariable String key) {
        try {
            Order order = orderService.getByReference(key);
            model.addAttribute("order", order);

            return "admin/orders/view";
        } catch (OrderNotFoundException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/administrator/orders/";
        }
    }

    /**
     * Set the order status to REJECTED, "deleting" the error.
     *
     * @param redirect of type RedirectAttributes
     * @param key      of type String
     *
     * @return String
     */
    @GetMapping("/delete/{key}")
    public String delete(RedirectAttributes redirect, @PathVariable String key) {
        try {
            Order order = orderService.getByReference(key);
            orderService.updateOrderStatus(order, OrderStatus.REJECTED);

            redirect.addFlashAttribute("success", "Order #" + order.getId() + " has been rejected!");
        } catch (EventsException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/administrator/orders/";
    }

    /**
     * Approve a reservation.
     *
     * @param redirect of type RedirectAttributes
     * @param key      of type String
     * @param payment  of type PaymentMethod
     *
     * @return String
     */
    @GetMapping("/approve/{key}/{payment}")
    public String approve(RedirectAttributes redirect, @PathVariable String key, @PathVariable PaymentMethod payment) {
        try {
            Order order = orderService.getByReference(key);
            order.setPaymentMethod(payment);
            orderService.updateOrderStatus(order, OrderStatus.PAID);

            redirect.addFlashAttribute("success", "Order #" + order.getId() + " has been approved!");
        } catch (EventsException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/administrator/orders/";
    }
}
