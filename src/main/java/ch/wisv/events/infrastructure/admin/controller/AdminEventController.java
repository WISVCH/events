package ch.wisv.events.infrastructure.admin.controller;

import ch.wisv.events.domain.model.event.Event;
import ch.wisv.events.domain.validator.EventValidator;
import ch.wisv.events.services.DocumentService;
import ch.wisv.events.services.EventService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

/**
 * AdminEventController.
 */
@Controller
@RequestMapping("/administrator/events")
public class AdminEventController extends AbstractAdminController<Event> {

    /** DocumentService. */
    private final DocumentService documentService;

    /**
     * AdminEventController constructor.
     *
     * @param eventService    of type EventService
     * @param documentService of type DocumentService
     */
    @Autowired
    public AdminEventController(EventService eventService, DocumentService documentService) {
        super(eventService, new Event(), "events", "event");
        this.documentService = documentService;
    }

    /**
     * Add the specific EventValidator
     *
     * @param binder of type WebDataBinder
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(new EventValidator());
    }

    /**
     * Save a file.
     *
     * @param model of type AbstractModel
     * @param file  of type MultipartFile
     *
     * @return T
     */
    @Override
    Event saveFile(Event model, MultipartFile file) {
        if (!file.isEmpty()) {
            String imageLocation = documentService.saveFile(file);
            model.setImage(imageLocation);
        }

        return model;
    }

    /**
     * Add Model to the index page.
     *
     * @return Map
     */
    @Override
    Map<String, ?> beforeIndex() {
        return null;
    }

    /**
     * Add Model to the view page.
     *
     * @return Map
     */
    @Override
    Map<String, ?> beforeView() {
        return null;
    }

    /**
     * Add Model to the edit page.
     *
     * @return Map
     */
    @Override
    Map<String, ?> beforeCreateEdit() {
        return null;
    }
}
