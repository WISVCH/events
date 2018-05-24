package ch.wisv.events.webshop.controller;

import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.order.OrderProductDto;
import ch.wisv.events.core.service.auth.AuthenticationService;
import ch.wisv.events.core.service.event.EventService;
import ch.wisv.events.webshop.service.WebshopService;
import java.util.List;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebshopIndexController {

    /** EventService. */
    private final EventService eventService;

    /** WebshopService. */
    private final WebshopService webshopService;

    /** AuthenticationService */
    private final AuthenticationService authenticationService;

    /**
     * Constructor WebshopController.
     *
     * @param eventService          of type EventService
     * @param webshopService        of type WebshopService
     * @param authenticationService of type AuthenticationService
     */
    public WebshopIndexController(
            EventService eventService,
            WebshopService webshopService,
            AuthenticationService authenticationService
    ) {
        this.eventService = eventService;
        this.webshopService = webshopService;
        this.authenticationService = authenticationService;
    }

    /**
     * Front page of the webshop.
     *
     * @param model of type Model
     *
     * @return String
     */
    @GetMapping("/")
    public String index(Model model) {
        List<Event> upcoming = eventService.getUpcoming();

        try {
            model.addAttribute("customer", authenticationService.getCurrentCustomer());
        } catch (AccessDeniedException ignored) {
        }

        model.addAttribute("events", webshopService.filterNotSalableProducts(upcoming));
        model.addAttribute("orderProduct", new OrderProductDto());

        return "webshop/index";
    }
}
