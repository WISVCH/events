package ch.wisv.events.controller.dashboard;

import ch.wisv.events.data.factory.event.EventOptionRequestFactory;
import ch.wisv.events.data.factory.event.EventProductRequestFactory;
import ch.wisv.events.data.factory.event.EventRequestFactory;
import ch.wisv.events.data.model.event.Event;
import ch.wisv.events.data.request.event.EventOptionsRequest;
import ch.wisv.events.data.request.event.EventProductRequest;
import ch.wisv.events.data.request.event.EventRequest;
import ch.wisv.events.exception.EventNotFound;
import ch.wisv.events.service.event.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collection;

/**
 * DashboardEventController.
 */
@Controller
@RequestMapping(value = "/dashboard/events")
@PreAuthorize("hasRole('ADMIN')")
public class DashboardEventController {

    /**
     * EventService.
     */
    private final EventService eventService;

    /**
     * Default constructor
     *
     * @param eventService EventService
     */
    @Autowired
    public DashboardEventController(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Get request on "/dashboard/events/" will show overview of all Events
     *
     * @param model SpringUI model
     * @return path to Thymeleaf themplate
     */
    @GetMapping("/")
    public String eventsOverview(Model model) {
        Collection<Event> events = eventService.getAllEvents();
        events.forEach(x -> x.getProducts().forEach(y -> x.setSold(x.getSold() + y.getSold())));

        model.addAttribute("events", events);
        return "dashboard/events/index";
    }

    /**
     * Get request on "/dashboard/events/create/" will show page to create Event
     *
     * @param model SpringUI model
     * @return path to Thymeleaf template
     */
    @GetMapping("/create/")
    public String createEventView(Model model) {
        model.addAttribute("event", new EventRequest());
        model.addAttribute("eventStatus", new EventOptionsRequest());

        return "dashboard/events/create";
    }

    /**
     * Get request on "/dashboard/events/edit/{key}" will show the edit page to edit Event with requested key
     *
     * @param model SpringUI model
     * @return path to Thymeleaf template
     */
    @GetMapping("/edit/{key}")
    public String editEventView(Model model, @PathVariable String key) {
        try {
            Event event = eventService.getEventByKey(key);


            model.addAttribute("event", EventRequestFactory.create(event));
            model.addAttribute("products", event.getProducts());
            model.addAttribute("options", EventOptionRequestFactory.create(event));
            model.addAttribute("eventProduct", EventProductRequestFactory.create(event));

            return "dashboard/events/edit";
        } catch (EventNotFound e) {
            return "redirect:/dashboard/events/";
        }
    }

    /**
     * Get request to delete event by Key
     *
     * @param redirectAttributes Spring RedirectAttributes
     * @param key                PathVariable key of the Event
     * @return redirect
     */
    @GetMapping("/delete/{key}")
    public String deleteEvent(RedirectAttributes redirectAttributes, @PathVariable String key) {
        try {
            Event event = eventService.getEventByKey(key);
            eventService.deleteEvent(event);

            redirectAttributes.addFlashAttribute("message", "Event " + event.getTitle() + " has been deleted!");

            return "redirect:/dashboard/events/";
        } catch (EventNotFound e) {
            redirectAttributes.addFlashAttribute("message", "Event has not been deleted, because it does not exists!");

            return "redirect:/dashboard/events/";
        }
    }

    /**
     * Post request to create a new Event
     *
     * @param eventRequest       EventRequest model attr.
     * @param redirectAttributes Spring RedirectAttributes
     * @return redirect
     */
    @PostMapping("/add")
    public String createEvent(@ModelAttribute @Validated EventRequest eventRequest, @ModelAttribute @Validated
            EventOptionsRequest eventOptionsRequest, RedirectAttributes
                                      redirectAttributes) {
        try {
            Event event = eventService.addEvent(eventRequest);
            eventOptionsRequest.setKey(event.getKey());

            eventService.updateEventOptions(eventOptionsRequest);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        redirectAttributes.addFlashAttribute("message", eventRequest.getTitle() + " successfully created!");

        return "redirect:/dashboard/events/";
    }

    /**
     * Post request to add a Product to an Event
     *
     * @param eventProductRequest EventProductRequest model attr.
     * @param redirectAttributes  Spring RedirectAttributes
     * @return redirect
     */
    @PostMapping("/add/product")
    public String addProductToEvent(@ModelAttribute @Validated EventProductRequest eventProductRequest,
                                    RedirectAttributes redirectAttributes) {
        try {
            eventService.addProductToEvent(eventProductRequest);
            redirectAttributes.addFlashAttribute("message", "Product added to Event!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/dashboard/events/edit/" + eventProductRequest.getEventKey();
    }

    /**
     * Post request to delete a Product from an Event
     *
     * @param eventProductRequest EventProductRequest model attr.
     * @param redirectAttributes  Spring RedirectAttributes
     * @return redirect
     */
    @PostMapping("/delete/product")
    public String deleteProductFromEvent(@ModelAttribute @Validated EventProductRequest eventProductRequest,
                                         RedirectAttributes redirectAttributes) {
        eventService.deleteProductFromEvent(eventProductRequest.getEventID(), eventProductRequest.getProductID());
        redirectAttributes.addFlashAttribute("message", "Product removed from Event!");

        return "redirect:/dashboard/events/edit/" + eventProductRequest.getEventKey();
    }

    /**
     * Post request to update an Event
     *
     * @param eventRequest       EventRequest model attr.
     * @param redirectAttributes Spring RedirectAttributes
     * @return redirect
     */
    @PostMapping("/update")
    public String editEvent(@ModelAttribute @Validated EventRequest eventRequest,
                            RedirectAttributes redirectAttributes) {
        eventService.updateEvent(eventRequest);
        redirectAttributes.addFlashAttribute("message", "Autosaved!");

        return "redirect:/dashboard/events/edit/" + eventRequest.getKey();
    }

    /**
     * Post request to update the options of an Event
     *
     * @param request            EventOptionsRequest request
     * @param redirectAttributes Spring RedirectAttributes
     * @return redirect
     */
    @PostMapping("/update/options")
    public String updateEventOptions(@ModelAttribute @Validated EventOptionsRequest request,
                                     RedirectAttributes redirectAttributes) {
        eventService.updateEventOptions(request);
        redirectAttributes.addFlashAttribute("message", "Autosaved!");

        return "redirect:/dashboard/events/edit/" + request.getKey();
    }
}
