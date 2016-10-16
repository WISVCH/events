package ch.wisv.events;

import ch.wisv.events.event.model.EventRequest;
import ch.wisv.events.event.service.EventService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Created by sven on 14/10/2016.
 */
@Controller
@RequestMapping(value = "/dashboard")
public class DashboardController {

    private final EventService eventService;

    public DashboardController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/")
    public String index(Model model) {
        return "dashboard/index";
    }

    @RequestMapping(value = "events/", method = RequestMethod.GET)
    public String eventsOverview(Model model) {
        model.addAttribute("events", eventService.getAllEvents());
        return "dashboard/events/index";
    }

    @RequestMapping(value = "events/create/", method = RequestMethod.GET)
    public String createEventView(Model model) {
        model.addAttribute("event", new EventRequest());
        return "dashboard/events/create";
    }

    @PostMapping(value = "events/add")
    public String createEvent(Model model, @ModelAttribute @Validated EventRequest eventRequest) {
        return "redirect:/dashboard/events/";
    }

}
