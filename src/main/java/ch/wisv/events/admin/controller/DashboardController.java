package ch.wisv.events.admin.controller;

import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.order.SoldProduct;
import ch.wisv.events.core.model.order.SoldProductStatus;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.service.customer.CustomerService;
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
@RequestMapping(value = "/administrator")
@PreAuthorize("hasRole('ADMIN')")
public class DashboardController {

    /**
     * Field eventService
     */
    private final EventService eventService;

    /**
     * Field customerService
     */
    private final CustomerService customerService;

    /**
     * Field soldProductService
     */
    private final SoldProductService soldProductService;

    /**
     * DashboardController
     *
     * @param eventService       of type EventService
     * @param customerService    of type CustomerService
     * @param soldProductService of type SoldProductService
     */
    @Autowired
    public DashboardController(EventService eventService,
            CustomerService customerService,
            SoldProductService soldProductService
    ) {
        this.eventService = eventService;
        this.customerService = customerService;
        this.soldProductService = soldProductService;
    }

    /**
     * Get request on "/" will show index.
     *
     * @param model SpringUI Model
     * @return path to Thymeleaf template
     */
    @GetMapping("/")
    public String index(Model model) {
        List<Event> upcomingEvents = this.determineUpcomingEvents();
        upcomingEvents.forEach(event -> event.setSold(event.getProducts().stream().mapToInt(Product::getSold).sum()));

        int totalEvents = this.eventService.getAllEvents().size();
        model.addAttribute("totalEvents", totalEvents);
        model.addAttribute("increaseEvents", this.calculateChangePercentage(
                this.determineTotalEventsLastMonth(),
                totalEvents
        ));

        int totalCustomers = this.customerService.getAllCustomers().size();
        model.addAttribute("totalCustomers", totalCustomers);
        model.addAttribute("increaseCustomers", this.calculateChangePercentage(
                this.determineTotalCustomersLastMonth(),
                totalCustomers
        ));

        double targetRateCurrentBoard = this.determineAverageTargetRateCurrentBoard();
        model.addAttribute("averageTargetRate", targetRateCurrentBoard);
        model.addAttribute("changeTargetRate", this.calculateChangePercentage(
                this.determineAverageTargetRatePreviousBoard(),
                targetRateCurrentBoard
        ));

        double attendanceRateCurrentBoard = this.determineAverageAttendanceRateEventCurrentBoard();
        model.addAttribute("averageAttendanceRate", attendanceRateCurrentBoard);
        model.addAttribute("changeAttendanceRate", this.calculateChangePercentage(
                this.determineAverageAttendanceRateEventPreviousBoard(),
                attendanceRateCurrentBoard
        ));


        model.addAttribute("upcoming", upcomingEvents);
        model.addAttribute("previous", this.determinePreviousEventAttendance());

        return "admin/index";
    }

    /**
     * Method determineTotalCustomersLastMonth ...
     *
     * @return int
     */
    private int determineTotalCustomersLastMonth() {
        return this.customerService.getAllCustomerCreatedAfter(LocalDateTime.now().minusMonths(1)).size();
    }

    /**
     * Method determineTotalEventsLastMonth ...
     *
     * @return double
     */
    private double determineTotalEventsLastMonth() {
        return this.eventService.getAllEventsBetween(LocalDateTime.of(2016, 9, 1, 0, 0), LocalDateTime.now().minusMonths(1)).size();
    }

    /**
     * Method determineAverageTargetRateCurrentBoard ...
     *
     * @return double
     */
    private double determineAverageTargetRateCurrentBoard() {
        List<Event> eventsCurrentBoard = this.getEventsCurrentBoard().stream().filter(x -> x.getEnding().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());

        return this.determineAverageTargetRate(eventsCurrentBoard);
    }

    /**
     * Method determineAverageTargetRatePreviousBoard ...
     *
     * @return double
     */
    private double determineAverageTargetRatePreviousBoard() {
        List<Event> eventsCurrentBoard = this.getEventsPreviousBoard();

        return this.determineAverageTargetRate(eventsCurrentBoard);
    }

    /**
     * Method getEventsCurrentBoard returns the eventsCurrentBoard of this DashboardController object.
     *
     * @return the eventsCurrentBoard (type List<Event>) of this DashboardController object.
     */
    private List<Event> getEventsCurrentBoard() {
        LocalDateTime lowerbound = LocalDateTime.of(LocalDateTime.now().getYear(), 9, 1, 0, 0);

        if (LocalDateTime.now().getMonthValue() < 9) {
            lowerbound = lowerbound.minusYears(1);
        }

        return this.eventService.getAllEventsBetween(lowerbound, lowerbound.plusYears(1));
    }

    /**
     * Method getEventsPreviousBoard returns the eventsPreviousBoard of this DashboardController object.
     *
     * @return the eventsPreviousBoard (type List<Event>) of this DashboardController object.
     */
    private List<Event> getEventsPreviousBoard() {
        LocalDateTime lowerbound = LocalDateTime.of(LocalDateTime.now().getYear() - 1, 9, 1, 0, 0);

        if (LocalDateTime.now().getMonthValue() < 9) {
            lowerbound = lowerbound.minusYears(1);
        }

        return this.eventService.getAllEventsBetween(lowerbound, lowerbound.plusYears(1));
    }

    /**
     * Method determineAverageTargetRate ...
     *
     * @param events of type List<Event>
     * @return double
     */
    private double determineAverageTargetRate(List<Event> events) {
        return events.stream()
                .mapToDouble(x -> {
                    x.setSold(x.getProducts().stream().mapToInt(Product::getSold).sum());

                    return x.calcProgress();
                }).average().orElse(0);
    }

    /**
     * Method determineAverageAttendanceRateEventCurrentBoard ...
     *
     * @return double
     */
    private double determineAverageAttendanceRateEventCurrentBoard() {
        double average = this.getEventsCurrentBoard().stream().filter(x -> x.getStart().isBefore(LocalDateTime.now()))
                .mapToDouble(this::determineAttendanceRateEvent).average().orElse(0);

        return Math.round(average * 100.d) / 100.d;
    }

    /**
     * Method determineAverageAttendanceRateEventCurrentBoard ...
     *
     * @return double
     */
    private double determineAverageAttendanceRateEventPreviousBoard() {
        double average = this.getEventsPreviousBoard().stream()
                .mapToDouble(this::determineAttendanceRateEvent).average().orElse(0);

        return Math.round(average * 100.d) / 100.d;
    }

    /**
     * Method determineAttendanceRateEvent ...
     *
     * @param event of type Event
     * @return double
     */
    private double determineAttendanceRateEvent(Event event) {
        List<SoldProduct> soldProducts = this.soldProductService.getAllByEvent(event);
        Long countScanned = soldProducts.stream().filter(soldProduct -> soldProduct.getStatus() == SoldProductStatus.SCANNED).count();

        if (soldProducts.size() == 0) {
            return 0.d;
        }

        return Math.round(countScanned.doubleValue() / soldProducts.size() * 10000.d) / 100.d;
    }

    /**
     * Method determinePreviousEventAttendance ...
     *
     * @return HashMap
     */
    private HashMap<Event, Double> determinePreviousEventAttendance() {
        HashMap<Event, Double> events = new HashMap<>();

        this.eventService.getPreviousEventsLastTwoWeeks().forEach(event -> events.put(event, this.determineAttendanceRateEvent(event)));

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

    /**
     * Method calculateChangePercentage ...
     *
     * @param previous of type double
     * @param current  of type double
     * @return double
     */
    private double calculateChangePercentage(double previous, double current) {
        return Math.round((current - previous) / previous * 10000.d) / 100.d;
    }
}
