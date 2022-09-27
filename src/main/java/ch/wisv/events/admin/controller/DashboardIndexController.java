package ch.wisv.events.admin.controller;

import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.ticket.Ticket;
import ch.wisv.events.core.model.ticket.TicketStatus;
import ch.wisv.events.core.service.customer.CustomerService;
import ch.wisv.events.core.service.event.EventService;
import ch.wisv.events.core.service.ticket.TicketService;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * DashboardController.
 */
@Controller
@RequestMapping(value = "/administrator")
@PreAuthorize("hasRole('ADMIN')")
public class DashboardIndexController extends DashboardController {

    /** EventService. */
    private final EventService eventService;

    /** CustomerService. */
    private final CustomerService customerService;

    /** TicketService. */
    private final TicketService ticketService;

    /**
     * DashboardController constructor.
     *
     * @param eventService    of type EventService
     * @param customerService of type CustomerService
     * @param ticketService   of type TicketService
     */
    @Autowired
    public DashboardIndexController(
            EventService eventService, CustomerService customerService, TicketService ticketService
    ) {
        this.eventService = eventService;
        this.customerService = customerService;
        this.ticketService = ticketService;
    }

    /**
     * Get request on "/" will show index.
     *
     * @param model SpringUI Model
     *
     * @return path to Thymeleaf template
     */
    @GetMapping()
    public String index(Model model) {
        List<Event> upcomingEvents = this.determineUpcomingEvents();

        long totalEvents = this.eventService.count();
        model.addAttribute("totalEvents", totalEvents);
        model.addAttribute("increaseEvents", this.calculateChangePercentage(this.determineTotalEventsLastMonth(), totalEvents));

        long totalCustomers = this.customerService.count();
        model.addAttribute("totalCustomers", totalCustomers);
        model.addAttribute("increaseCustomers", this.calculateChangePercentage(this.determineTotalCustomersLastMonth(), totalCustomers));

        double targetRateCurrentBoard = this.determineAverageTargetRateCurrentBoard();
        model.addAttribute("averageTargetRate", targetRateCurrentBoard);
        model.addAttribute(
                "changeTargetRate",
                this.calculateChangePercentage(this.determineAverageTargetRatePreviousBoard(), targetRateCurrentBoard)
        );

        double attendanceRateCurrentBoard = this.determineAverageAttendanceRateEventCurrentBoard();
        model.addAttribute("averageAttendanceRate", attendanceRateCurrentBoard);
        model.addAttribute(
                "changeAttendanceRate",
                this.calculateChangePercentage(this.determineAverageAttendanceRateEventPreviousBoard(), attendanceRateCurrentBoard)
        );

        model.addAttribute("upcoming", upcomingEvents);
        model.addAttribute("previous", this.determinePreviousEventAttendance());

        return "admin/index";
    }

    /**
     * Method calculateChangePercentage ...
     *
     * @param previous of type double
     * @param current  of type double
     *
     * @return double
     */
    private double calculateChangePercentage(double previous, double current) {
        return Math.round((current - previous) / previous * 10000.d) / 100.d;
    }

    /**
     * Method determineAttendanceRateEvent ...
     *
     * @param event of type Event
     *
     * @return double
     */
    private double determineAttendanceRateEvent(Event event) {
        List<Ticket> eventTickets = event.getProducts().stream().flatMap(product -> ticketService.getAllByProduct(product).stream()).collect(
                Collectors.toList());
        long numberTicketsScanned = eventTickets.stream().filter(ticket -> ticket.getStatus() == TicketStatus.SCANNED).count();

        if (eventTickets.size() == 0) {
            return 0.d;
        }

        return Math.round(numberTicketsScanned / eventTickets.size() * 10000.d) / 100.d;
    }

    /**
     * Method determineAverageAttendanceRateEventCurrentBoard ...
     *
     * @return double
     */
    private double determineAverageAttendanceRateEventCurrentBoard() {
        double average = this.getEventsCurrentBoard()
                .stream()
                .filter(x -> x.getStart().isBefore(LocalDateTime.now()))
                .mapToDouble(this::determineAttendanceRateEvent)
                .average()
                .orElse(0);

        return Math.round(average * 100.d) / 100.d;
    }

    /**
     * Method determineAverageAttendanceRateEventCurrentBoard ...
     *
     * @return double
     */
    private double determineAverageAttendanceRateEventPreviousBoard() {
        double average = this.getEventsPreviousBoard().stream().mapToDouble(this::determineAttendanceRateEvent).average().orElse(0);

        return Math.round(average * 100.d) / 100.d;
    }

    /**
     * Method determineAverageTargetRate ...
     *
     * @param events of type List
     *
     * @return double
     */
    private double determineAverageTargetRate(List<Event> events) {
        return events.stream().mapToDouble(Event::calcSoldProgress).average().orElse(0);
    }

    /**
     * Method determineAverageTargetRateCurrentBoard ...
     *
     * @return double
     */
    private double determineAverageTargetRateCurrentBoard() {
        List<Event> eventsCurrentBoard = this.getEventsCurrentBoard().stream().filter(x -> x.getEnding().isBefore(LocalDateTime.now())).collect(
                Collectors.toList());

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
        return this.eventService.getAllBetween(LocalDateTime.of(2016, 9, 1, 0, 0), LocalDateTime.now().minusMonths(1)).size();
    }

    /**
     * Method determineUpcomingEvents ...
     *
     * @return List
     */
    private List<Event> determineUpcomingEvents() {
        return this.eventService.getUpcoming().stream().filter(event -> event.getStart().isBefore(LocalDateTime.now().plusWeeks(2))).collect(
                Collectors.toList());
    }

    /**
     * Method getEventsCurrentBoard returns the eventsCurrentBoard of this DashboardController object.
     *
     * @return the eventsCurrentBoard (type List) of this DashboardController object.
     */
    private List<Event> getEventsCurrentBoard() {
        LocalDateTime lowerbound = LocalDateTime.of(LocalDateTime.now().getYear(), 9, 1, 0, 0);

        if (LocalDateTime.now().getMonthValue() < 9) {
            lowerbound = lowerbound.minusYears(1);
        }

        return this.eventService.getAllBetween(lowerbound, lowerbound.plusYears(1));
    }

    /**
     * Method getEventsPreviousBoard returns the eventsPreviousBoard of this DashboardController object.
     *
     * @return the eventsPreviousBoard (type List) of this DashboardController object.
     */
    private List<Event> getEventsPreviousBoard() {
        LocalDateTime lowerbound = LocalDateTime.of(LocalDateTime.now().getYear() - 1, 9, 1, 0, 0);

        if (LocalDateTime.now().getMonthValue() < 9) {
            lowerbound = lowerbound.minusYears(1);
        }

        return this.eventService.getAllBetween(lowerbound, lowerbound.plusYears(1));
    }
}
