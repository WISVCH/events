package ch.wisv.events.dashboard;

import ch.wisv.events.event.service.EventService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @GetMapping("/login/")
    public String login(Model model) {
        return "dashboard/login";
    }

}
