package ch.wisv.events.admin.controller;

import ch.wisv.events.core.exception.normal.EventInvalidException;
import ch.wisv.events.core.exception.normal.EventNotFoundException;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.event.EventStatus;
import ch.wisv.events.core.model.ticket.Ticket;
import ch.wisv.events.core.model.webhook.WebhookTrigger;
import ch.wisv.events.core.service.document.DocumentService;
import ch.wisv.events.core.service.event.EventService;
import ch.wisv.events.core.service.ticket.TicketService;
import ch.wisv.events.core.webhook.WebhookPublisher;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * DashboardEventController class.
 */
@Controller
@RequestMapping(value = "/administrator/events")
@PreAuthorize("hasRole('ADMIN')")
public class DashboardEventController extends DashboardController {

    /** EventService. */
    private final EventService eventService;

    /** TicketService. */
    private final TicketService ticketService;

    /** WebhookPublisher. */
    private final WebhookPublisher webhookPublisher;

    /** DocumentService. */
    private final DocumentService documentService;

    /**
     * DashboardEventController constructor.
     *
     * @param eventService     of type EventService
     * @param ticketService    of type TicketService
     * @param webhookPublisher of type WebhookPublisher
     * @param documentService  of type DocumentService
     */
    public DashboardEventController(
            EventService eventService,
            TicketService ticketService,
            WebhookPublisher webhookPublisher,
            DocumentService documentService
    ) {
        this.eventService = eventService;
        this.ticketService = ticketService;
        this.webhookPublisher = webhookPublisher;
        this.documentService = documentService;
    }

    /**
     * Get request on "/administrator/events/" will show overview of all Events.
     *
     * @param model of type Model
     *
     * @return path to Thymeleaf template
     */
    @GetMapping()
    public String index(Model model) {
        model.addAttribute(OBJ_EVENTS, eventService.getAll());

        return "admin/events/index";
    }

    /**
     * Get request on "/administrator/events/view/{key}" will show page to view an Event.
     *
     * @param model    of type Model
     * @param redirect of type RedirectAttributes
     * @param key      of type String
     *
     * @return String
     */
    @GetMapping({"/view/{key}","/view/{key}/"})
    public String view(Model model, RedirectAttributes redirect, @PathVariable String key) {
        try {
            model.addAttribute(OBJ_EVENT, eventService.getByKey(key));

            return "admin/events/view";
        } catch (EventNotFoundException e) {
            redirect.addFlashAttribute(FLASH_ERROR, e.getMessage());

            return "redirect:/administrator/events/";
        }
    }

    /**
     * Get request on "/administrator/events/create/" will show page to create Event.
     *
     * @param model of type Model
     *
     * @return path to Thymeleaf template
     */
    @GetMapping({"/create","/create/"})
    public String create(Model model) {
        if (!model.containsAttribute(OBJ_EVENT)) {
            model.addAttribute(OBJ_EVENT, new Event());
        }

        return "admin/events/event";
    }

    /**
     * Post request to create a new Event.
     *
     * @param redirect of type RedirectAttributes
     * @param event    of type Event
     * @param file     of type MultipartFile
     *
     * @return redirect
     */
    @PostMapping({"/create","/create/"})
    public String create(RedirectAttributes redirect, @ModelAttribute Event event, @RequestParam("file") MultipartFile file) {
        try {
            if (file.getSize() > 0) {
                eventService.addDocumentImage(event, documentService.storeDocument(file));
            }
            if (event.getExternalProductUrl() != null && event.getExternalProductUrl().length() == 0){
               event.setExternalProductUrl(null);
            }
            eventService.create(event);
            redirect.addFlashAttribute(FLASH_SUCCESS, "Event " + event.getTitle() + " has been created!");

            if (event.getPublished() == EventStatus.PUBLISHED) {
                this.webhookPublisher.createWebhookTask(WebhookTrigger.EVENT_CREATE_UPDATE, event);
            }

            return "redirect:/administrator/events/";
        } catch (EventInvalidException | IOException e) {
            redirect.addFlashAttribute(OBJ_EVENT, event);
            redirect.addFlashAttribute(FLASH_ERROR, e.getMessage());

            return "redirect:/administrator/events/create/";
        }
    }

    /**
     * Get request on "/administrator/events/edit/{key}" will show the edit page to edit Event with requested key.
     *
     * @param model of type Model
     * @param redirect of type RedirectAttributes
     * @param key   of type String
     *
     * @return path to Thymeleaf template
     */
    @GetMapping({"/edit/{key}","/edit/{key}/"})
    public String edit(Model model, RedirectAttributes redirect, @PathVariable String key) {
        try {
            if (!model.containsAttribute(OBJ_EVENT)) {
                model.addAttribute(OBJ_EVENT, eventService.getByKey(key));
            }

            return "admin/events/event";
        } catch (EventNotFoundException e) {
            redirect.addFlashAttribute(FLASH_ERROR, e.getMessage());

            return "redirect:/administrator/events/";
        }
    }

    /**
     * Post request to update an Event.
     *
     * @param redirect of type RedirectAttributes
     * @param event    of type Event
     * @param key      of type String
     * @param file     of type MultipartFile
     *
     * @return redirect
     */
    @PostMapping({"/edit/{key}","/edit/{key}/"})
    public String update(
            RedirectAttributes redirect,
            @ModelAttribute Event event,
            @PathVariable String key,
            @RequestParam("file") MultipartFile file
    ) {
        try {
            if (file.getSize() > 0) {
                eventService.addDocumentImage(event, documentService.storeDocument(file));
            }
            event.setKey(key);
            if (event.getExternalProductUrl() != null && event.getExternalProductUrl().length() == 0){
               event.setExternalProductUrl(null);
            }
            eventService.update(event);
            redirect.addFlashAttribute(FLASH_SUCCESS, "Event changes saved!");

            if (event.getPublished() == EventStatus.PUBLISHED) {
                this.webhookPublisher.createWebhookTask(WebhookTrigger.EVENT_CREATE_UPDATE, event);
            } else {
                this.webhookPublisher.createWebhookTask(WebhookTrigger.EVENT_DELETE, event);
            }

            return "redirect:/administrator/events/view/" + event.getKey();
        } catch (EventNotFoundException | EventInvalidException | IOException e) {
            redirect.addFlashAttribute(OBJ_EVENT, event);
            redirect.addFlashAttribute(FLASH_ERROR, e.getMessage());

            return "redirect:/administrator/events/edit/" + event.getKey();
        }
    }

    /**
     * Method overview ...
     *
     * @param model    of type Model
     * @param redirect of type RedirectAttributes
     * @param key      of type String
     *
     * @return String
     */
    @GetMapping({"/overview/{key}","/overview/{key}/"})
    public String overview(Model model, RedirectAttributes redirect, @PathVariable String key) {
        try {
            Event event = eventService.getByKey(key);
            List<Ticket> tickets = event.getProducts().stream()
                    .flatMap(product -> ticketService.getAllByProduct(product).stream())
                    .collect(Collectors.toList());

            model.addAttribute(OBJ_EVENT, event);
            model.addAttribute(OBJ_TICKETS, tickets);
            model.addAttribute("attendance", eventService.getAttendance(event));
            return "admin/events/overview";
        } catch (EventNotFoundException e) {
            redirect.addFlashAttribute(FLASH_ERROR, e.getMessage());

            return "redirect:/administrator/events/";
        }
    }

    /**
     *
     */
    @GetMapping(value = "/overview/csv/{key}", produces = "text/csv")
    public HttpEntity<? extends Object> csvExport(@PathVariable String key) {
        try {
            Event event = eventService.getByKey(key);
            List<Ticket> tickets = event.getProducts().stream()
                    .flatMap(product -> ticketService.getAllByProduct(product).stream())
                    .collect(Collectors.toList());
            String csvData = tickets.stream()
                    .map(t -> t.getOwner().getName() + ";" + t.getOwner().getEmail() + ";" + t.getProduct().title)
                    .collect(Collectors.joining("\n"));
            csvData = "Name;Email;Product\n" + csvData;
            InputStream bufferedInputStream = new ByteArrayInputStream(csvData.getBytes(StandardCharsets.UTF_8));
            InputStreamResource fileInputStream = new InputStreamResource(bufferedInputStream);

            String filename = event.getTitle() + "_export.csv";

            // setting HTTP headers
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
            // defining the custom Content-Type
            headers.set(HttpHeaders.CONTENT_TYPE, "text/csv");

            return new ResponseEntity<>(
                    fileInputStream,
                    headers,
                    HttpStatus.OK
            );
        } catch (EventNotFoundException e) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Location", "/administrator/events/");
            return new ResponseEntity<String>(headers,HttpStatus.FOUND);
        }
    }

    /**
     * Get request to delete event by Key.
     *
     * @param redirect of type RedirectAttributes
     * @param key      PathVariable key of the Event
     *
     * @return redirect
     */
    @GetMapping({"/delete/{key}","/delete/{key}/"})
    public String deleteEvent(RedirectAttributes redirect, @PathVariable String key) {
        try {
            Event event = eventService.getByKey(key);
            eventService.delete(event);
            webhookPublisher.createWebhookTask(WebhookTrigger.EVENT_DELETE, event);
            redirect.addFlashAttribute(FLASH_SUCCESS, "Event " + event.getTitle() + " has been deleted!");
        } catch (EventNotFoundException e) {
            redirect.addFlashAttribute(FLASH_ERROR, "Event with key not-found not found!");
        }

        return "redirect:/administrator/events/";
    }
}
