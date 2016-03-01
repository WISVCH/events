package ch.wisv.events;

import ch.wisv.events.exception.EventNotFoundException;
import ch.wisv.events.model.Event;
import ch.wisv.events.repository.EventRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller for creating and getting events.
 */
@Controller
@RequestMapping(value = "/events")
public class EventsController {

    @Autowired
    private EventRepository eventRepository;

    private final Logger log = org.slf4j.LoggerFactory.getLogger(EventsController.class);

    @ExceptionHandler(EventNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Event not found")
    public void eventNotFound(Exception e) {
        log.warn("Event not found", e);
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

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ADMIN')")
    public String saveEvent(@ModelAttribute Event event, RedirectAttributes redirectAttributes) {
        event = eventRepository.save(event);

        redirectAttributes.addFlashAttribute("message", "Event '" + event.getTitle() + "' saved!");
        return "redirect:/events/";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String getEvent(Model model, @PathVariable("id") long id, RedirectAttributes redirectAttributes) {
        Event event = eventRepository.findOne(id);
        if (event != null) {
            model.addAttribute("event", event);
            return "events/detail";
        } else {
            redirectAttributes.addFlashAttribute("message", "That event does not exist.");
            return "redirect:/events/";
        }
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ADMIN')")
    public String removeEvent(@PathVariable("id") long id, RedirectAttributes redirectAttributes) throws EventNotFoundException {
        Event event = eventRepository.findOne(id);
        if (event != null) {
            eventRepository.delete(event);
            redirectAttributes.addFlashAttribute("message", "Event '" + event.getTitle() + "' deleted!");
            return "redirect:/events/";
        } else {
            throw new EventNotFoundException("This event does not exist");
        }
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ADMIN')")
    public String updateEvent(@PathVariable("id") long id, Model model, RedirectAttributes redirectAttributes) {
        Event event = eventRepository.findOne(id);
        if (event != null) {
            model.addAttribute("event", event);
            return "events/edit";
        } else {
            redirectAttributes.addFlashAttribute("message", "That event does not exist.");
            return "redirect:/events/";
        }
    }
}
