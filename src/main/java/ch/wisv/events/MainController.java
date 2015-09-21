package ch.wisv.events;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Main Spring MVC controller
 *
 * @author Mark Janssen
 */
@Controller
public class MainController {
    @RequestMapping("/")
    String index() {
        return "index";
    }
}
