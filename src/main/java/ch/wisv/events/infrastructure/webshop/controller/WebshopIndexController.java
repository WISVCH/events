package ch.wisv.events.infrastructure.webshop.controller;

import ch.wisv.events.domain.model.event.Event;
import ch.wisv.events.infrastructure.webshop.dto.FilterDto;
import ch.wisv.events.infrastructure.webshop.dto.OrderDto;
import ch.wisv.events.services.EventService;
import java.util.HashMap;
import java.util.List;
import static java.util.Objects.isNull;
import java.util.stream.Collectors;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import static org.springframework.util.CollectionUtils.isEmpty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * WebshopIndexController class.
 */
@Controller
@RequestMapping("/webshop")
public class WebshopIndexController extends AbstractWebshopController {

    /** Model attr events. */
    private static final String MODEL_ATTR_EVENTS = "events";

    /** Model attr event. */
    private static final String MODEL_ATTR_EVENT = "event";

    /** Model attr filter. */
    private static final String MODEL_ATTR_FILTER = "filterDto";

    /** Model attr orderDto. */
    private static final String MODEL_ATTR_ORDER_DTO = "orderDto";

    /** EventService. */
    private final EventService eventService;

    /**
     * WebshopController constructor.
     *
     * @param eventService of type EventService
     */
    protected WebshopIndexController(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Front page of the webshop.
     *
     * @param model     of type Model
     * @param filterDto of type FilterDto
     *
     * @return String
     */
    @GetMapping
    public String index(Model model, @ModelAttribute FilterDto filterDto) {
        if (!model.containsAttribute(MODEL_ATTR_ERRORS)) {
            model.addAttribute(MODEL_ATTR_ERRORS, new HashMap<String, String>());
        }

        if (isNull(filterDto)) {
            model.addAttribute(MODEL_ATTR_FILTER, new FilterDto());
        }

        List<Event> allUpcoming = eventService.getAllUpcoming();
        allUpcoming = allUpcoming.stream()
                .filter(event -> isEmpty(filterDto.getCategories()) || event.getCategories().stream().anyMatch(filterDto.getCategories()::contains))
                .filter(event -> isNull(filterDto.getSearch()) || event.getTitle().toLowerCase().contains(filterDto.getSearch().toLowerCase()))
                .collect(Collectors.toList());
        model.addAttribute(MODEL_ATTR_EVENTS, allUpcoming);
        model.addAttribute(MODEL_ATTR_ORDER_DTO, new OrderDto());

        return "webshop/webshop-index";
    }

    /**
     * Front page of the webshop.
     *
     * @param model           of type Model
     * @param publicReference of type String
     *
     * @return String
     */
    @GetMapping("/{publicReference}")
    public String viewEvent(Model model, @PathVariable String publicReference) {
        if (!model.containsAttribute(MODEL_ATTR_ERRORS)) {
            model.addAttribute(MODEL_ATTR_ERRORS, new HashMap<String, String>());
        }

        model.addAttribute(MODEL_ATTR_EVENT, eventService.getByPublicReference(publicReference));
        model.addAttribute(MODEL_ATTR_ORDER_DTO, new OrderDto());

        return "webshop/webshop-single-event";
    }
}
