package ch.wisv.events.app.controller.dashboard;

import ch.wisv.events.core.exception.EventNotFound;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.event.EventStatus;
import ch.wisv.events.core.model.order.SoldProduct;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.model.webhook.WebhookTrigger;
import ch.wisv.events.core.service.event.EventService;
import ch.wisv.events.core.service.product.SoldProductService;
import ch.wisv.events.core.webhook.WebhookPublisher;
import ch.wisv.events.utils.FormMode;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
     * Field webhookPublisher
     */
    private final WebhookPublisher webhookPublisher;

    /**
     * Default constructor
     *
     * @param eventService       EventService
     * @param soldProductService SoldProductService
     * @param webhookPublisher   WebhookPublisher
     */
    public DashboardEventController(EventService eventService, SoldProductService soldProductService, WebhookPublisher webhookPublisher) {
        this.eventService = eventService;
        this.soldProductService = soldProductService;
        this.webhookPublisher = webhookPublisher;
    }

    /**
     * Get request on "/dashboard/events/" will show overview of all Events
     *
     * @param model SpringUI model
     * @return path to Thymeleaf template
     */
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("events", this.eventService.getAllEvents());

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
        model.addAttribute("mode", FormMode.CREATE);
        model.addAttribute("product", new Product());

        if (!model.containsAttribute("event")) {
            model.addAttribute("event", new Event());
        }

        return "dashboard/events/form";
    }

    /**
     * Get request on "/dashboard/events/edit/{key}" will show the edit page to edit Event with requested key
     *
     * @param model SpringUI model
     * @return path to Thymeleaf template
     */
    @GetMapping("/edit/{key}/")
    public String edit(Model model, @PathVariable String key) {
        try {
            model.addAttribute("mode", FormMode.UPDATE);
            if (!model.containsAttribute("event")) {
                model.addAttribute("event", this.eventService.getByKey(key));
            }

            return "dashboard/events/form";
        } catch (EventNotFound e) {
            return "redirect:/dashboard/events/";
        }
    }

    /**
     * Method overview ...
     *
     * @param model of type Model
     * @param key   of type String
     * @return String
     */
    @GetMapping("/overview/{key}/")
    public String overview(Model model, @PathVariable String key) {
        try {
            Event event = this.eventService.getByKey(key);

            List<SoldProduct> soldProduct = new ArrayList<>();
            event.getProducts().forEach(x -> soldProduct.addAll(this.soldProductService.getByProduct(x)));

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
     * @param redirect Spring RedirectAttributes
     * @param key      PathVariable key of the Event
     * @return redirect
     */
    @GetMapping("/delete/{key}")
    public String deleteEvent(RedirectAttributes redirect, @PathVariable String key) {
        try {
            Event event = this.eventService.getByKey(key);
            this.eventService.delete(event);
            redirect.addFlashAttribute("message", "Event " + event.getTitle() + " has been deleted!");
            this.webhookPublisher.event(WebhookTrigger.EVENT_DELETE, event);

            return "redirect:/dashboard/events/";
        } catch (EventNotFound e) {
            redirect.addFlashAttribute("message", "Event has not been deleted, because it does not exists!");

            return "redirect:/dashboard/events/";
        }
    }

    /**
     * Post request to create a new Event
     *
     * @param event    EventRequest model attr.
     * @param redirect Spring RedirectAttributes
     * @return redirect
     */
    @PostMapping("/create")
    public String create(RedirectAttributes redirect, @ModelAttribute Event event) {
        try {
            this.eventService.create(event);
            redirect.addFlashAttribute("message", event.getTitle() + " successfully created!");

            if (event.getOptions().getPublished() == EventStatus.PUBLISHED) {
                this.webhookPublisher.event(WebhookTrigger.EVENT_CREATE_UPDATE, event);
            }

            return "redirect:/dashboard/events/edit/" + event.getKey() + "/";
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            redirect.addFlashAttribute("event", event);

            return "redirect:/dashboard/events/create/";
        }
    }

    /**
     * Post request to update an Event
     *
     * @param event    EventRequest model attr.
     * @param redirect Spring RedirectAttributes
     * @return redirect
     */
    @PostMapping("/update")
    public String update(RedirectAttributes redirect, @ModelAttribute Event event) {
        try {
            this.eventService.update(event);
            redirect.addFlashAttribute("message", "Event changes saved!");

            if (event.getOptions().getPublished() == EventStatus.PUBLISHED) {
                this.webhookPublisher.event(WebhookTrigger.EVENT_CREATE_UPDATE, event);
            } else {
                this.webhookPublisher.event(WebhookTrigger.EVENT_DELETE, event);
            }

        } catch (RuntimeException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            redirect.addFlashAttribute("event", event);
        }

        return "redirect:/dashboard/events/edit/" + event.getKey() + "/";
    }
}
