package ch.wisv.events.sales.controller.scan;

import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.service.auth.AuthenticationService;
import ch.wisv.events.sales.service.SalesService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * SalesScanMainController class.
 */
@Controller
@RequestMapping(value = "/sales/scan")
@PreAuthorize("hasRole('USER')")
public class SalesScanMainController {

    /** AuthenticationService. */
    private final AuthenticationService authenticationService;

    /** SalesService. */
    private final SalesService salesService;

    /**
     * SalesScanMainController.
     *
     * @param authenticationService of type AuthenticationService.
     * @param salesService          of type SalesService.
     */
    public SalesScanMainController(AuthenticationService authenticationService, SalesService salesService) {
        this.authenticationService = authenticationService;
        this.salesService = salesService;
    }

    /**
     * Index view of the scan app.
     *
     * @param model of type Model.
     *
     * @return String
     */
    @GetMapping
    public String index(Model model) {
        Customer currentUser = authenticationService.getCurrentCustomer();
        model.addAttribute("events", salesService.getAllGrantedEventByCustomer(currentUser));

        return "sales/scan/index";
    }

    /**
     * Results of the scanning.
     *
     * @param model of type Model
     *
     * @return String
     */
    @GetMapping("/result/")
    public String result(Model model) {
        if (!model.containsAttribute("result") && !model.containsAttribute("product")) {
            return "redirect:/sales/scan/";
        }

        return "scan/result";
    }

}
