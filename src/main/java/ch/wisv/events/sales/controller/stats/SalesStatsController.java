package ch.wisv.events.sales.controller.stats;

import ch.wisv.events.core.exception.normal.EventNotFoundException;
import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.service.auth.AuthenticationService;
import ch.wisv.events.core.service.event.EventService;
import ch.wisv.events.sales.service.SalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SalesScanEventController.
 */
@Controller
@RequestMapping(value = "/sales/stats")
@PreAuthorize("hasRole('USER')")
public class SalesStatsController {
    /**
     * Default return redirect on error.
     */
    private static final String ERROR_REDIRECT = "redirect:/sales/stats/";

    /**
     * EventService.
     */
    private final EventService eventService;

    /**
     * AuthenticationService.
     */
    private final AuthenticationService authenticationService;

    /**
     * SalesService.
     */
    private final SalesService salesService;

    /**
     * SalesScanMainController.
     *
     * @param eventService          of type EventService.
     * @param authenticationService of type AuthenticationService.
     * @param salesService          of type SalesService.
     */
    @Autowired
    public SalesStatsController(EventService eventService, AuthenticationService authenticationService, SalesService salesService) {
        this.eventService = eventService;
        this.authenticationService = authenticationService;
        this.salesService = salesService;
    }

    /**
     * Index view of the stats page.
     *
     * @param model of type Model.
     * @return String
     */
    @GetMapping
    public String indexView(Model model) {
        Customer currentUser = authenticationService.getCurrentCustomer();
        model.addAttribute("events", salesService.getAllGrantedEventByCustomer(currentUser));

        return "sales/stats/index";
    }

    /**
     * Ticket sales view.
     *
     * @param model of type Model
     * @param key   of type String
     * @return String
     */
    @GetMapping("/products/{key}")
    public String ticketSalesindex(Model model, @PathVariable String key) throws EventNotFoundException {
        Event event = eventService.getByKey(key);

        model.addAttribute("products", event.getProducts());
        model.addAttribute("target", event.getTarget());
        model.addAttribute("key", key);

        return "sales/stats/products/index";
    }

    /**
     * Event sales view.
     *
     * @param model of type Model
     * @param key   of type String
     * @return String
     */
    @GetMapping("/event/{key}")
    public String eventSalesView(Model model, @PathVariable String key) throws EventNotFoundException {
        Event event = eventService.getByKey(key);
        List<Order> orders = salesService.getAllOrdersByEvent(event).stream().peek((Order order) -> {
            order.setOwner(null);
            order.setChPaymentsReference(null);
            order.setPaymentMethod(null);
        }).collect(Collectors.toList());

        double moneyEarned = 0.0;
        int ticketsSold = 0;

        for (Product product : event.getProducts()) {
            moneyEarned += product.getCost() * product.getSold();
            ticketsSold += product.getSold();
        }

        model.addAttribute("orders", orders);
        model.addAttribute("moneyEarned", moneyEarned);
        model.addAttribute("ticketsSold", ticketsSold);
        model.addAttribute("event", event);
        model.addAttribute("key", event.getKey());
        model.addAttribute("attendance", eventService.getAttendance(event));

        return "sales/stats/event/index";
    }

    @ExceptionHandler(EventNotFoundException.class)
    public final String handleEventNotFoundException(EventNotFoundException ex) {
        return ERROR_REDIRECT;
    }

}
