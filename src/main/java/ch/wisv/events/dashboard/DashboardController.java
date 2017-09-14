package ch.wisv.events.dashboard;

import ch.wisv.events.core.service.event.EventService;
import ch.wisv.events.core.service.product.SoldProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * DashboardController.
 */
@Controller
@RequestMapping(value = "/dashboard")
@PreAuthorize("hasRole('ADMIN')")
public class DashboardController {

    /**
     * Field eventService
     */
    private final EventService eventService;

    /**
     * Field soldProductService
     */
    private final SoldProductService soldProductService;

    /**
     * DashboardController
     *
     * @param eventService       EventService
     * @param soldProductService SoldProductService
     */
    @Autowired
    public DashboardController(EventService eventService, SoldProductService soldProductService) {
        this.eventService = eventService;
        this.soldProductService = soldProductService;
    }

    /**
     * Get request on "/dashboard/" will show index.
     *
     * @param model SpringUI Model
     * @return path to Thymeleaf template
     */
    @GetMapping("/")
    public String index(Model model) {
        // TODO: create nice looking and good working dashboard with relevant information.

        return "dashboard/index";
    }

}
