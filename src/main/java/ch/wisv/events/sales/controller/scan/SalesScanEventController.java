package ch.wisv.events.sales.controller.scan;

import ch.wisv.events.core.exception.normal.EventNotFoundException;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.service.event.EventService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * SalesScanEventController.
 */
@Controller
@RequestMapping(value = "/sales/scan/event/{key}")
@PreAuthorize("hasRole('USER')")
public class SalesScanEventController {

    /** Attribute event. */
    private static final String ATTR_EVENT = "event";

    /** Attribute error. */
    private static final String ATTR_ERROR = "error";

    /** Default return redirect on error. */
    private static final String ERROR_REDIRECT = "redirect:/sales/scan/";

    /** EventService. */
    private final EventService eventService;

    /**
     * SalesScanEventController.
     *
     * @param eventService of type EventService
     */
    public SalesScanEventController(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * View to scan a ticket/code for an event.
     *
     * @param model    of type Model
     * @param redirect of type RedirectAttributes
     * @param key      of type String
     * @param method   of type String
     *
     * @return String
     */
    @GetMapping({"/{method}","/{method}/"})
    public String scanner(Model model, RedirectAttributes redirect, @PathVariable String key, @PathVariable String method) {
        try {
            Event event = eventService.getByKey(key);
            model.addAttribute(ATTR_EVENT, event);

            return "sales/scan/event/" + method;
        } catch (EventNotFoundException e) {
            redirect.addFlashAttribute(ATTR_ERROR, e.getMessage());

            return ERROR_REDIRECT;
        }
    }

}
