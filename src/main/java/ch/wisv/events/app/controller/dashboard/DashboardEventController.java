package ch.wisv.events.app.controller.dashboard;

import ch.wisv.events.api.request.EventOptionsRequest;
import ch.wisv.events.api.request.EventProductRequest;
import ch.wisv.events.api.request.EventRequest;
import ch.wisv.events.core.data.factory.event.EventOptionRequestFactory;
import ch.wisv.events.core.data.factory.event.EventProductRequestFactory;
import ch.wisv.events.core.data.factory.event.EventRequestFactory;
import ch.wisv.events.core.exception.EventNotFound;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.order.SoldProduct;
import ch.wisv.events.core.service.event.EventService;
import ch.wisv.events.core.service.product.SoldProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

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
     * Field productService
     */
    private final SoldProductService soldProductService;

    /**
     * Default constructor
     *
     * @param eventService       EventService
     * @param soldProductService SoldProductService
     */
    @Autowired
    public DashboardEventController(EventService eventService, SoldProductService soldProductService) {
        this.eventService = eventService;
        this.soldProductService = soldProductService;
    }

    /**
     * Get request on "/dashboard/events/" will show overview of all Events
     *
     * @param model SpringUI model
     * @return path to Thymeleaf template
     */
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("events", eventService.getAllEvents());
        return "dashboard/events/index";
    }

    /**
     * Get request on "/dashboard/events/create/" will show page to create Event
     *
     * @param model SpringUI model
     * @return path to Thymeleaf template
     */
    @GetMapping("/create/")
    public String create(Model model) {
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
    public String edit(Model model, @PathVariable String key) {
        try {
            Event event = eventService.getByKey(key);

            model.addAttribute("event", EventRequestFactory.create(event));
            model.addAttribute("products", event.getProducts());
            model.addAttribute("options", EventOptionRequestFactory.create(event));
            model.addAttribute("eventProduct", EventProductRequestFactory.create(event));

            return "dashboard/events/edit";
        } catch (EventNotFound e) {
            return "redirect:/dashboard/events/";
        }
    }

    @GetMapping("/overview/{key}/")
    public String overview(Model model, @PathVariable String key) {
        try {
            Event event = eventService.getByKey(key);

            List<SoldProduct> soldProduct = new ArrayList<>();
            event.getProducts().forEach(x -> soldProduct.addAll(soldProductService.getByProduct(x)));

            model.addAttribute("event", event);
            model.addAttribute("soldProducts", soldProduct);

            return "dashboard/events/overview";
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
            Event event = eventService.getByKey(key);
            eventService.delete(event);

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
            Event event = eventService.add(eventRequest);
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
        eventService.update(eventRequest);
        redirectAttributes.addFlashAttribute("message", "Auto saved!");

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
        redirectAttributes.addFlashAttribute("message", "Auto saved!");

        return "redirect:/dashboard/events/edit/" + request.getKey() + "/";
    }
}
