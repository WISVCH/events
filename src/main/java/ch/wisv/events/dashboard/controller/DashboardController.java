package ch.wisv.events.dashboard.controller;

import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.order.SoldProduct;
import ch.wisv.events.core.model.order.SoldProductStatus;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.service.event.EventService;
import ch.wisv.events.core.service.product.SoldProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DashboardController.
 */
@Controller
@RequestMapping(value = "/dashboard")
@PreAuthorize("hasRole('ADMIN')")
public class DashboardController {

    /**
     * Field eventService
     */
    private final EventService eventService;

    /**
     * Field soldProductService
     */
    private final SoldProductService soldProductService;

    /**
     * DashboardController
     *
     * @param eventService       EventService
     * @param soldProductService SoldProductService
     */
    @Autowired
    public DashboardController(EventService eventService, SoldProductService soldProductService) {
        this.eventService = eventService;
        this.soldProductService = soldProductService;
    }

    /**
     * Get request on "/dashboard/" will show index.
     *
     * @param model SpringUI Model
     * @return path to Thymeleaf template
     */
    @GetMapping("/")
    public String index(Model model) {
        List<Event> upcomingEvents = this.determineUpcomingEvents();
        upcomingEvents.forEach(event -> event.setSold(event.getProducts().stream().mapToInt(Product::getSold).sum()));

        model.addAttribute("upcoming", upcomingEvents);
        model.addAttribute("previous", this.determinePreviousEventAttendance());

        return "dashboard/index";
    }

    /**
     * Method determinePreviousEventAttendance ...
     * @return HashMap<Event, Integer>
     */
    private HashMap<Event, Integer> determinePreviousEventAttendance() {
        HashMap<Event, Integer> events = new HashMap<>();

        this.eventService.getPreviousEventsLastTwoWeeks().forEach(event -> {
            event.setSold(event.getProducts().stream().mapToInt(Product::getSold).sum());
            List<SoldProduct> soldProducts = this.soldProductService.getAllByEvent(event);
            Long countScanned = soldProducts.stream().filter(soldProduct -> soldProduct.getStatus() == SoldProductStatus.SCANNED).count();

            if (soldProducts.size() == 0) {
                events.put(event, 0);
            } else {
                events.put(event, (int) ((countScanned.doubleValue() / soldProducts.size()) * 100.d));
            }
        });

        return events;
    }

    /**
     * Method determineUpcomingEvents ...
     *
     * @return List<Event>
     */
    private List<Event> determineUpcomingEvents() {
        return this.eventService.getUpcomingEvents().stream().filter(event ->
                event.getStart().isBefore(LocalDateTime.now().plusWeeks(2))
        ).collect(Collectors.toList());
    }
}
