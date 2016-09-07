package ch.wisv.events;

import ch.wisv.events.model.Event;
import ch.wisv.events.repository.EventRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller for creating and getting events.
 */
@Controller
@RequestMapping(value = "/events")
public class EventsController {

    private final EventRepository eventRepository;

    public EventsController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getEvents(Model model, @ModelAttribute("message") String message) {
        List<Event> events = eventRepository.findByEndAfter(LocalDateTime.now());
        model.addAttribute("events", events);

        return "events/index";
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ADMIN')")
    public String createEvent(Model model) {
        model.addAttribute("event", new Event());
        return "events/create";
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ADMIN')")
    public String createEvent(@ModelAttribute Event event, RedirectAttributes redirectAttributes) {
        event = eventRepository.save(event);

        redirectAttributes.addFlashAttribute("message", "Event '" + event.getTitle() + "' created!");
        return "redirect:/events/";
    }
}
