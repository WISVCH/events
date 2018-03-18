package ch.wisv.events.sales.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/sales")
@PreAuthorize("hasRole('USER')")
public class SalesController {

    @GetMapping
    public String index(Model model) {
        return "sales/index";
    }

}
