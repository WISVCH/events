package ch.wisv.events.admin.controller;

import ch.wisv.events.core.admin.Attendence;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.ticket.Ticket;
import ch.wisv.events.core.model.ticket.TicketStatus;
import ch.wisv.events.core.repository.EventRepository;
import ch.wisv.events.core.service.customer.CustomerService;
import ch.wisv.events.core.service.event.EventService;
import ch.wisv.events.core.service.ticket.TicketService;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.tomcat.jni.Local;
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

    /** EventRepository. */
    private final EventRepository eventRepository;

    /**
     * DashboardController constructor.
     *
     * @param eventService    of type EventService
     * @param customerService of type CustomerService
     * @param ticketService   of type TicketService
     * @param eventRepository   of type eventRepository
     */
    @Autowired
    public DashboardIndexController(
            EventService eventService, CustomerService customerService, TicketService ticketService, EventRepository eventRepository
    ) {
        this.eventService = eventService;
        this.customerService = customerService;
        this.ticketService = ticketService;
        this.eventRepository = eventRepository;
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
        LocalDateTime CurrentBoardStartYear = this.getCurrentBoardStartDate();

        long totalEvents = this.eventService.count();
        model.addAttribute("totalEvents", totalEvents);
        model.addAttribute("increaseEvents", this.calculateChangePercentage(this.determineTotalEventsOfMonth(0), this.determineTotalEventsOfMonth(1)));

        long totalCustomers = this.customerService.count();
        model.addAttribute("totalCustomers", totalCustomers);
        model.addAttribute("increaseCustomers", this.calculateChangePercentage(totalCustomers - this.determineTotalCustomersLastMonth(), totalCustomers));

        Attendence attCurrBoard = this.eventRepository.getAttendenceFromEventsInDateRange(CurrentBoardStartYear.toLocalDate(), CurrentBoardStartYear.minusMonths(1).toLocalDate());
        Attendence attLastBoard = this.eventRepository.getAttendenceFromEventsInDateRange(CurrentBoardStartYear.minusYears(1).toLocalDate(), CurrentBoardStartYear.minusMonths(1).minusYears(1).toLocalDate());

        double attendanceRateCurrentBoard = 0d;
        double attendanceRateLastBoard = 0d;

        if(attLastBoard.getTicketsCount() != 0 && attCurrBoard.getTicketsCount() != 0) {
            attendanceRateCurrentBoard = attCurrBoard.getPercentageScanned();
            attendanceRateLastBoard = attLastBoard.getPercentageScanned();
        }


        model.addAttribute("averageAttendanceRate", attendanceRateCurrentBoard);
        model.addAttribute(
                "changeAttendanceRate",
                this.calculateChangePercentage(attendanceRateLastBoard, attendanceRateCurrentBoard)
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
        return Math.round(((current - previous) / previous) * 100.d) / 100.d;
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

        return Math.round(numberTicketsScanned / (eventTickets.size() * 10000.d)) / 100.d;
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
        return (int) this.customerService.countAllCustomerCreatedAfter(LocalDateTime.now().minusMonths(1));
    }

    /**
     * Determine the total amount of events in a month period.
     *
     * @param yearsBack The amount of years you want to look back
     *
     * @return double
     */
    private double determineTotalEventsOfMonth(int yearsBack) {
        return (double) this.eventService.getCountOfAllBetween(LocalDateTime.now().minusMonths(1).minusYears(yearsBack), LocalDateTime.now().minusYears(yearsBack));
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
     * Returns the current boards localdatetime
     * @return LocalDateTime
     */
    private LocalDateTime getCurrentBoardStartDate(){
        LocalDateTime lowerBound = LocalDateTime.of(LocalDateTime.now().getYear(), 9, 1, 0, 0);

        if (LocalDateTime.now().getMonthValue() < 9) {
            lowerBound = lowerBound.minusYears(1);
        }
        return lowerBound;
    }
}
