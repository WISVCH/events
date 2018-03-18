package ch.wisv.events.sales.controller.scan;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/sales/scan")
@PreAuthorize("hasRole('USER')")
public class SalesScanMainController {

    @GetMapping
    public String index(Model model) {
        return "sales/scan/index";
    }

}
