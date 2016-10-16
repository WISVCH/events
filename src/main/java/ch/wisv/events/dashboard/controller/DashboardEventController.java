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

        EventRequest request = new EventRequest();
        request.setTitle(event.title());
        request.setDescription(event.description());
        request.setLimit(event.registrationLimit());
        request.setEventStart(event.start().toString());
        request.setEventEnd(event.end().toString());
        request.setLocation(event.location());
        request.setKey(event.key());

        AddTicketRequest ticket = new AddTicketRequest();
        ticket.setEventKey(event.key());

        model.addAttribute("event", request);
        model.addAttribute("tickets", event.tickets());
        model.addAttribute("addticket", ticket);

        return "dashboard/events/edit";
    }

    @PostMapping("/add")
    public String createEvent(Model model, @ModelAttribute @Validated EventRequest eventRequest) {
        eventService.addEvent(eventRequest);

        return "redirect:/dashboard/events/";
    }

    @PostMapping("/add/ticket")
    public String addTicketToEvent(Model model, @ModelAttribute @Validated AddTicketRequest addTicketRequest) {
        eventService.addTicketToEvent(addTicketRequest);

        return "redirect:/dashboard/events/edit/" + addTicketRequest.getEventKey();
    }

    @PostMapping("/edit")
    public String editEvent(Model model, @ModelAttribute @Validated EventRequest eventRequest) {
        return "redirect:/dashboard/events/";
    }
}
