package ch.wisv.events.dashboard.controller;

import ch.wisv.events.dashboard.request.AddTicketRequest;
import ch.wisv.events.dashboard.request.EventRequest;
import ch.wisv.events.event.model.Event;
import ch.wisv.events.event.service.EventService;
import ch.wisv.events.event.service.TicketService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Created by sven on 15/10/2016.
 */
@Controller
@RequestMapping(value = "/dashboard/events")
public class DashboardEventController {

    private final EventService eventService;
    private final TicketService ticketService;


    public DashboardEventController(EventService eventService, TicketService ticketService) {
        this.eventService = eventService;
        this.ticketService = ticketService;
    }

    @GetMapping("/")
    public String eventsOverview(Model model) {
        model.addAttribute("events", eventService.getAllEvents());
        return "dashboard/events/index";
    }

    @GetMapping("/create/")
    public String createEventView(Model model) {
        model.addAttribute("event", new EventRequest());
        return "dashboard/events/create";
    }

    @GetMapping("/edit/{key}")
    public String editEventView(Model model, @PathVariable String key) {
        Event event = this.eventService.getEventByKey(key);
        if (event == null) {
            return "redirect:/dashboard/events/";
        }

        // TODO: Code clean up
        EventRequest request = new EventRequest();
        request.setTitle(event.getTitle());
        request.setDescription(event.getDescription());
        request.setLimit(event.getRegistrationLimit());
        request.setEventStart(event.getStart().toString());
        request.setEventEnd(event.getEnd().toString());
        request.setLocation(event.getLocation());
        request.setKey(event.getKey());

        AddTicketRequest ticket = new AddTicketRequest();
        ticket.setEventID(event.getId());
        ticket.setEventKey(event.getKey());

        model.addAttribute("event", request);
        model.addAttribute("tickets", event.getTickets());
        model.addAttribute("addticket", ticket);

        return "dashboard/events/edit";
    }

    @PostMapping("/add")
    public String createEvent(Model model, @ModelAttribute @Validated EventRequest eventRequest, RedirectAttributes
            redirectAttributes) {
        eventService.addEvent(eventRequest);

        redirectAttributes.addFlashAttribute("message", eventRequest.getTitle() + " successfully created!");

        return "redirect:/dashboard/events/";
    }

    @PostMapping("/add/ticket")
    public String addTicketToEvent(Model model, @ModelAttribute @Validated AddTicketRequest addTicketRequest,
                                   RedirectAttributes redirectAttributes) {
        try {
            eventService.addTicketToEvent(addTicketRequest);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/dashboard/events/edit/" + addTicketRequest.getEventKey();
    }

    @GetMapping("/delete/{eventKey}/ticket/{ticketId}")
    public String deleteTicketFromEvent(Model model, @PathVariable String eventKey, @PathVariable Long ticketId,
                                        RedirectAttributes redirectAttributes) {

        eventService.deleteTicketFromEvent(eventKey, ticketId);
        redirectAttributes.addFlashAttribute("message", "Ticket removed from Event!");


        return "redirect:/dashboard/events/edit/" + eventKey;
    }

    @PostMapping("/edit")
    public String editEvent(Model model, @ModelAttribute @Validated EventRequest eventRequest) {
        return "redirect:/dashboard/events/";
    }
}
