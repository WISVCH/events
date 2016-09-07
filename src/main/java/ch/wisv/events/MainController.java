package ch.wisv.events;

import ch.wisv.events.model.Event;
import ch.wisv.events.repository.EventRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Main Spring MVC controller
 */
@Controller
public class MainController {

    private final EventRepository eventRepository;

    public MainController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @RequestMapping("/")
    String index(Model model) {
        List<Event> events = eventRepository.findByEndAfter(LocalDateTime.now());
        model.addAttribute("events", events.subList(0, Integer.min(3, events.size())));

        return "index";
    }
}
