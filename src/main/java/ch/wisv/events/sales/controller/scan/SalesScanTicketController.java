package ch.wisv.events.sales.controller.scan;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * SalesScanEventController.
 */
@Controller
@RequestMapping(value = "/sales/scan/ticket")
@PreAuthorize("hasRole('USER')")
public class SalesScanTicketController {

    /** Attribute redirect. */
    private static final String ATTR_REDIRECT = "redirect";

    /** Attribute error. */
    private static final String ATTRIBUTE_ERROR = "error";

    /** Attribute ticket. */
    private static final String ATTR_TICKET = "ticket";

    /** Default return redirect. */
    private static final String DEFAULT_REDIRECT = "/sales/scan/";

    /** Default return redirect on error. */
    private static final String ERROR_REDIRECT = "redirect:/sales/scan/";

    /**
     * Ticket index view.
     *
     * @param model of type Model
     *
     * @return String
     */
    @GetMapping("/error")
    public String error(Model model) {
        if (!model.containsAttribute(ATTRIBUTE_ERROR)) {
            return ERROR_REDIRECT;
        }

        if (!model.containsAttribute(ATTR_REDIRECT)) {
            model.addAttribute(ATTR_REDIRECT, DEFAULT_REDIRECT);
        }

        return "sales/scan/ticket/error";
    }

    /**
     * Ticket index view.
     *
     * @param model  of type Model
     * @param status of type String
     *
     * @return String
     */
    @GetMapping("/{status}")
    public String index(Model model, @PathVariable String status) {
        if (!model.containsAttribute(ATTR_TICKET)) {
            return ERROR_REDIRECT;
        }

        if (!model.containsAttribute(ATTR_REDIRECT)) {
            model.addAttribute(ATTR_REDIRECT, DEFAULT_REDIRECT);
        }

        return "sales/scan/ticket/" + status;
    }
}
