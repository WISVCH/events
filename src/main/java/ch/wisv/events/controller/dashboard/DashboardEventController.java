package ch.wisv.events.controller.dashboard;

import ch.wisv.events.data.factory.event.EventOptionRequestFactory;
import ch.wisv.events.data.factory.event.EventProductRequestFactory;
import ch.wisv.events.data.factory.event.EventRequestFactory;
import ch.wisv.events.data.model.event.Event;
import ch.wisv.events.data.request.event.EventOptionsRequest;
import ch.wisv.events.data.request.event.EventProductRequest;
import ch.wisv.events.data.request.event.EventRequest;
import ch.wisv.events.service.EventService;
import ch.wisv.events.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collection;
import java.util.Random;

/**
 * Created by sven on 15/10/2016.
 */
@Controller
@RequestMapping(value = "/dashboard/events")
public class DashboardEventController {

    private final EventService eventService;
    private final ProductService productService;


    public DashboardEventController(EventService eventService, ProductService productService) {
        this.eventService = eventService;
        this.productService = productService;
    }

    @GetMapping("/")
    public String eventsOverview(Model model) {
        // TODO: For testing
        Random random = new Random();
        Collection<Event> events = eventService.getAllEvents();
        events.forEach(e -> e.setSold(random.nextInt(100)));

        model.addAttribute("events", events);
        return "dashboard/events/index";
    }

    @GetMapping("/create/")
    public String createEventView(Model model) {
        model.addAttribute("event", new EventRequest());
        return "dashboard/events/create";
    }

    @GetMapping("/edit/{key}")
    public String editEventView(Model model, @PathVariable String key) {
        Event event = eventService.getEventByKey(key);
        if (event == null) {
            return "redirect:/dashboard/events/";
        }

        model.addAttribute("event", EventRequestFactory.create(event));
        model.addAttribute("products", event.getProducts());
        model.addAttribute("options", EventOptionRequestFactory.create(event));
        model.addAttribute("eventProduct", EventProductRequestFactory.create(event));

        return "dashboard/events/edit";
    }

    @GetMapping("/delete/{key}")
    public String deleteEvent(RedirectAttributes redirectAttributes, @PathVariable String key) {
        Event event = eventService.getEventByKey(key);
        eventService.deleteEvent(event);

        redirectAttributes.addFlashAttribute("message", "Event " + event.getTitle() + " has been deleted!");

        return "redirect:/dashboard/events/";
    }

    @PostMapping("/add")
    public String createEvent(@ModelAttribute @Validated EventRequest eventRequest, RedirectAttributes
            redirectAttributes) {
        try {
            eventService.addEvent(eventRequest);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        redirectAttributes.addFlashAttribute("message", eventRequest.getTitle() + " successfully created!");

        return "redirect:/dashboard/events/";
    }


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

    @PostMapping("/delete/product")
    public String deleteProductFromEvent(@ModelAttribute @Validated EventProductRequest eventProductRequest,
                                         RedirectAttributes redirectAttributes) {
        eventService.deleteProductFromEvent(eventProductRequest.getEventID(), eventProductRequest.getProductID());
        redirectAttributes.addFlashAttribute("message", "Product removed from Event!");

        return "redirect:/dashboard/events/edit/" + eventProductRequest.getEventKey();
    }

    @PostMapping("/update")
    public String editEvent(@ModelAttribute @Validated EventRequest eventRequest,
                            RedirectAttributes redirectAttributes) {
        eventService.updateEvent(eventRequest);
        redirectAttributes.addFlashAttribute("message", "Autosaved!");

        return "redirect:/dashboard/events/edit/" + eventRequest.getKey();
    }

    @PostMapping("/update/options")
    public String updateEventOptions(@ModelAttribute @Validated EventOptionsRequest request,
                                     RedirectAttributes redirectAttributes) {
        eventService.updateEventOptions(request);
        redirectAttributes.addFlashAttribute("message", "Autosaved!");

        return "redirect:/dashboard/events/edit/" + request.getKey();
    }
}
