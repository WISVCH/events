package ch.wisv.events.admin.controller;

import ch.wisv.events.core.exception.normal.EventInvalidException;
import ch.wisv.events.core.exception.normal.EventNotFoundException;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.event.EventStatus;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.model.webhook.WebhookTrigger;
import ch.wisv.events.core.service.event.EventService;
import ch.wisv.events.core.service.ticket.TicketService;
import ch.wisv.events.core.webhook.WebhookPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DashboardEventController.
 */
@Controller
@RequestMapping(value = "/administrator/events")
@PreAuthorize("hasRole('ADMIN')")
public class DashboardEventController {

    /**
     * EventService.
     */
    private final EventService eventService;

    /**
     * Field productService
     */
    private final TicketService ticketService;

    /**
     * Field webhookPublisher
     */
    private final WebhookPublisher webhookPublisher;

    /**
     * Default constructor
     *
     * @param eventService     EventService
     * @param ticketService    TicketService
     * @param webhookPublisher WebhookPublisher
     */
    public DashboardEventController(EventService eventService, TicketService ticketService, WebhookPublisher webhookPublisher) {
        this.eventService = eventService;
        this.ticketService = ticketService;
        this.webhookPublisher = webhookPublisher;
    }

    /**
     * Get request on "/admin/events/" will show overview of all Events
     *
     * @param model SpringUI model
     * @return path to Thymeleaf template
     */
    @GetMapping()
    public String index(Model model) {
        model.addAttribute("events", eventService.getAll());

        return "admin/events/index";
    }

    /**
     * Method view ...
     *
     * @param model    of type Model
     * @param redirect of type RedirectAttributes
     * @param key      of type String
     * @return String
     */
    @GetMapping("/view/{key}")
    public String view(Model model, RedirectAttributes redirect, @PathVariable String key) {
        try {
            model.addAttribute("event", eventService.getByKey(key));

            return "admin/events/view";
        } catch (EventNotFoundException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/administrator/events/";
        }
    }

    /**
     * Get request on "/admin/events/create/" will show page to create Event
     *
     * @param model SpringUI model
     * @return path to Thymeleaf template
     */
    @GetMapping("/create")
    public String create(Model model) {
        if (!model.containsAttribute("event")) {
            model.addAttribute("event", new Event());
        }

        return "admin/events/event";
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
            eventService.create(event);
            redirect.addFlashAttribute("success", event.getTitle() + " successfully created!");

            if (event.getPublished() == EventStatus.PUBLISHED) {
                this.webhookPublisher.createWebhookTask(WebhookTrigger.EVENT_CREATE_UPDATE, event);
            }

            return "redirect:/administrator/events/";
        } catch (EventInvalidException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            redirect.addFlashAttribute("event", event);

            return "redirect:/administrator/events/create/";
        }
    }

    /**
     * Get request on "/admin/events/edit/{key}" will show the edit page to edit Event with requested key
     *
     * @param model SpringUI model
     * @return path to Thymeleaf template
     */
    @GetMapping("/edit/{key}")
    public String edit(Model model, @PathVariable String key) {
        try {
            if (!model.containsAttribute("event")) {
                model.addAttribute("event", eventService.getByKey(key));
            }

            return "admin/events/event";
        } catch (EventNotFoundException e) {
            return "redirect:/administrator/events/";
        }
    }

    /**
     * Post request to update an Event
     *
     * @param event    EventRequest model attr.
     * @param redirect Spring RedirectAttributes
     * @return redirect
     */
    @PostMapping("/edit/{key}")
    public String update(RedirectAttributes redirect, @ModelAttribute Event event) {
        try {
            eventService.update(event);
            redirect.addFlashAttribute("success", "Event changes saved!");

            if (event.getPublished() == EventStatus.PUBLISHED) {
                this.webhookPublisher.createWebhookTask(WebhookTrigger.EVENT_CREATE_UPDATE, event);
            } else {
                this.webhookPublisher.createWebhookTask(WebhookTrigger.EVENT_DELETE, event);
            }

            return "redirect:/administrator/events/view/" + event.getKey() + "/";
        } catch (EventNotFoundException | EventInvalidException e) {
            redirect.addFlashAttribute("error", e.getMessage());
            redirect.addFlashAttribute("event", event);

            return "redirect:/administrator/events/edit/" + event.getKey() + "/";
        }
    }

    /**
     * Method overview ...
     *
     * @param model of type Model
     * @param key   of type String
     * @return String
     */
    @GetMapping("/overview/{key}")
    public String overview(Model model, @PathVariable String key) {
        try {
            Event event = eventService.getByKey(key);

            model.addAttribute("event", event);
            model.addAttribute("tickets", event.getProducts().stream()
                    .flatMap(product -> ticketService.getAllByProduct(product).stream())
                    .collect(Collectors.toList()));

            return "admin/events/overview";
        } catch (EventNotFoundException e) {
            return "redirect:/administrator/events/";
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
            Event event = eventService.getByKey(key);
            eventService.delete(event);
            webhookPublisher.createWebhookTask(WebhookTrigger.EVENT_DELETE, event);
            redirect.addFlashAttribute("message", "Event " + event.getTitle() + " has been deleted!");

            return "redirect:/administrator/events/";
        } catch (EventNotFoundException e) {
            redirect.addFlashAttribute("message", "Event has not been deleted, because it does not exists!");

            return "redirect:/administrator/events/";
        }
    }
}
