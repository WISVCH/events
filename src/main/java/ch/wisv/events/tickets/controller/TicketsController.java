package ch.wisv.events.tickets.controller;

import ch.wisv.events.core.exception.normal.*;
import ch.wisv.events.core.exception.runtime.PaymentsConnectionException;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderProductDTO;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.model.order.PaymentMethod;
import ch.wisv.events.core.service.auth.AuthenticationService;
import ch.wisv.events.core.service.event.EventService;
import ch.wisv.events.core.service.order.OrderService;
import ch.wisv.events.tickets.service.TicketsService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
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
@PreAuthorize("hasRole('ROLE_USER')")
public class TicketsController {

    /**
     * Field eventService.
     */
    private final EventService eventService;

    /**
     * Field authenticationService.
     */
    private final AuthenticationService authenticationService;

    /**
     * Field ticketsService
     */
    private final TicketsService ticketsService;

    /**
     * Field orderService
     */
    private final OrderService orderService;

    /**
     * Redirects
     */
    private final String REDIRECT_HOME = "redirect:/";

    /**
     * Constructor TicketsController.
     *  @param eventService  of type EventService
     * @param authenticationService
     * @param ticketService of type TicketsService
     * @param orderService  of type OrderService
     */
    public TicketsController(EventService eventService,
            AuthenticationService authenticationService,
            TicketsService ticketService,
            OrderService orderService
    ) {
        this.eventService = eventService;
        this.authenticationService = authenticationService;
        this.ticketsService = ticketService;
        this.orderService = orderService;
    }

    /**
     * GetMapping for "/".
     *
     * @return String
     */
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("events", eventService.getUpcoming());
        model.addAttribute("orderProduct", new OrderProductDTO());

        if (authenticationService.getCurrentCustomer().getRfidToken().equals("")) {
            model.addAttribute("message", "No card coupled to your account. For faster check-in at the event couple a card to your account at the " +
                    "board.");
        }

        return "tickets/index";
    }

    /**
     * GetMapping for "/checkout/{key}/".
     *
     * @param model    of type Model
     * @param redirect of type RedirectAttributes
     * @param key      of type String
     * @return String
     */
    @GetMapping("/checkout/{key}/")
    public String checkout(Model model, RedirectAttributes redirect, @PathVariable String key) {
        try {
            Order order = orderService.getByReference(key);

            if (authenticationService.getCurrentCustomer().equals(order.getOwner())) {
                model.addAttribute("order", order);

                return "tickets/checkout";
            } else {
                throw new AccessDeniedException("Access denied!");
            }
        } catch (OrderNotFoundException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }

        return REDIRECT_HOME;
    }

    /**
     * GetMapping for "/cancel/{key}".
     *
     * @param redirect of type RedirectAttributes
     * @param key      of type String
     * @return String
     */
    @GetMapping("/cancel/{key}/")
    public String cancel(RedirectAttributes redirect, @PathVariable String key) {
        try {
            Order order = this.getOrderByKeyAndAuthCustomer(key);
            orderService.updateOrderStatus(order, OrderStatus.CANCELLED);
        } catch (OrderNotFoundException | OrderInvalidException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }

        return REDIRECT_HOME;
    }

    /**
     * GetMapping for "/payments/{key}".
     *
     * @param redirect of type RedirectAttributes
     * @param key      of type String
     * @return String
     */
    @GetMapping("/payment/{key}/")
    public String payment(RedirectAttributes redirect, @PathVariable String key) {
        try {
            Order order = this.getOrderByKeyAndAuthCustomer(key);

            if (order.getPaymentMethod() == PaymentMethod.FREE) {
                orderService.updateOrderStatusPaid(order);

                return "redirect:/complete/" + order.getPublicReference() + "/";
            }

            return "redirect:" + ticketsService.getPaymentsMollieUrl(order);
        } catch (OrderNotFoundException | UnassignedOrderException | UndefinedPaymentMethodOrderException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return REDIRECT_HOME;
        } catch (PaymentsConnectionException e) {
            redirect.addFlashAttribute("error", "CH Payments is not responding!");

            return "redirect:/checkout/" + key + "/";
        }
    }

    /**
     * GetMapping for "/complete/{key}/". Redirect from mollie payments.
     *
     * @param redirect          of type RedirectAttributes
     * @param key               of type String
     * @param paymentsReference of type String
     * @return String
     */
    @GetMapping("/status/{key}/")
    public String status(RedirectAttributes redirect, @PathVariable String key, @RequestParam("reference") String paymentsReference) {
        try {
            Order order = this.getOrderByKeyAndAuthCustomer(key);
            ticketsService.updateOrderStatus(order, paymentsReference);

            if (order.getStatus() == OrderStatus.PENDING) {
                this.retryFetchOrderStatus(order, paymentsReference);
            }

            return "redirect:/complete/" + order.getPublicReference() + "/";
        } catch (OrderNotFoundException | OrderInvalidException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return REDIRECT_HOME;
        } catch (PaymentsStatusUnknown | PaymentsConnectionException e) {
            redirect.addFlashAttribute("error", "Something went wrong trying to fetch the payment status.");

            return "redirect:/checkout/" + key + "/";
        }
    }

    /**
     * Retry fetching the OrderStatus when the OrderStatus is WAITING.
     *
     * @param order             of type Order
     * @param paymentsReference of type String
     * @throws PaymentsStatusUnknown when the status that Payments gives is unknown.
     * @throws OrderInvalidException when the order is valid.
     */
    private void retryFetchOrderStatus(Order order, String paymentsReference) throws PaymentsStatusUnknown, OrderInvalidException {
        int count = 0;
        int maxCount = 5;
        while (order.getStatus() == OrderStatus.PENDING && count < maxCount) {
            try {
                sleep(500);
                ticketsService.updateOrderStatus(order, paymentsReference);
            } catch (InterruptedException ignored) {
            }
            count++;
        }

        // Close Order when the OrderStatus has not been changes
        if (count == maxCount) {
            orderService.updateOrderStatus(order, OrderStatus.EXPIRED);
        }
    }

    /**
     * GetMapping for "/complete/{key}/".
     *
     * @param model    of type Model
     * @param redirect of type RedirectAttributes
     * @param key      of type String
     * @return String
     */
    @GetMapping("/complete/{key}/")
    public String complete(Model model, RedirectAttributes redirect, @PathVariable String key) {
        try {
            model.addAttribute("order", this.getOrderByKeyAndAuthCustomer(key));

            return "tickets/complete";
        } catch (OrderNotFoundException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return REDIRECT_HOME;
        }
    }

    /**
     * Get an Order by its keys and auth if Customer is auth to see this Order.
     *
     * @param key of type String
     * @return Order
     * @throws OrderNotFoundException when the Order is not found.
     */
    private Order getOrderByKeyAndAuthCustomer(String key) throws OrderNotFoundException {
        Order order = orderService.getByReference(key);

        if (authenticationService.getCurrentCustomer().equals(order.getOwner())) {
            return order;
        } else {
            throw new AccessDeniedException("Access denied!");
        }
    }

    /**
     * Creates an order with Products and current Customer.
     *
     * @param redirect        of type RedirectAttributes
     * @param orderProductDTO of type OrderProductDTO
     * @return String
     */
    @PostMapping("/checkout/")
    public String checkout(RedirectAttributes redirect, @ModelAttribute OrderProductDTO orderProductDTO) {
        try {
            if (orderProductDTO.getProducts().isEmpty()) {
                redirect.addFlashAttribute("error", "Shopping basket can not be empty!");

                return REDIRECT_HOME;
            }

            Order order = orderService.createOrderByOrderProductDTO(orderProductDTO);
            order.setOwner(authenticationService.getCurrentCustomer());
            order.setCreatedBy("events-online");
            if (order.getAmount() == 0.d && order.getOrderProducts().size() > 0) {
                order.setPaymentMethod(PaymentMethod.FREE);
            } else {
                order.setPaymentMethod(PaymentMethod.MOLLIE);
            }

            orderService.create(order);

            return "redirect:/checkout/" + order.getPublicReference() + "/";
        } catch (ProductNotFoundException | EventNotFoundException | OrderInvalidException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        } catch (EventsException e) {
            redirect.addFlashAttribute("error", "Limit exceeded: " + e.getMessage());
        }

        return REDIRECT_HOME;
    }
}
