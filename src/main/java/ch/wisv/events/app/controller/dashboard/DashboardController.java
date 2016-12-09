package ch.wisv.events.app.controller.dashboard;

import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.order.SoldProduct;
import ch.wisv.events.core.model.order.SoldProductStatus;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.service.event.EventService;
import ch.wisv.events.core.service.product.SoldProductService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * DashboardController.
 */
@Controller
@RequestMapping(value = "/dashboard")
@PreAuthorize("hasRole('ADMIN')")
public class DashboardController {

    private final EventService eventService;

    private final SoldProductService soldProductService;

    /**
     * Default constructor
     * @param eventService
     * @param soldProductService
     */
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
        List<Event> upcoming = eventService.soldFiveUpcoming();
        model.addAttribute("upcoming", new String[] {
                upcoming.get(0).getTitle(),
                upcoming.get(1).getTitle(),
                upcoming.get(2).getTitle(),
                upcoming.get(3).getTitle(),
                upcoming.get(4).getTitle()
        });
        model.addAttribute("upcomingTarget", new int[] {
                upcoming.get(0).getTarget(),
                upcoming.get(1).getTarget(),
                upcoming.get(2).getTarget(),
                upcoming.get(3).getTarget(),
                upcoming.get(4).getTarget()
        });
        model.addAttribute("upcomingSold", new int[] {
                upcoming.get(0).getSold(),
                upcoming.get(1).getSold(),
                upcoming.get(2).getSold(),
                upcoming.get(3).getSold(),
                upcoming.get(4).getSold()
        });

        List<Event> previous = eventService.soldFivePrevious();
        model.addAttribute("previous", new String[] {
                previous.get(0).getTitle(),
                previous.get(1).getTitle(),
                previous.get(2).getTitle(),
                previous.get(3).getTitle(),
                previous.get(4).getTitle()
        });
        model.addAttribute("previousTarget", new int[] {
                previous.get(0).getTarget(),
                previous.get(1).getTarget(),
                previous.get(2).getTarget(),
                previous.get(3).getTarget(),
                previous.get(4).getTarget()
        });
        model.addAttribute("previousSold", new int[] {
                previous.get(0).getSold(),
                previous.get(1).getSold(),
                previous.get(2).getSold(),
                previous.get(3).getSold(),
                previous.get(4).getSold()
        });
        int[] scanned = new int[5];
        for (int i = 0; i < previous.size(); i++) {
            int sum = 0;
            for (Product product : previous.get(i).getProducts()) {
                for (SoldProduct soldProduct : soldProductService.getByProduct(product)) {
                    if (soldProduct.getStatus() == SoldProductStatus.SCANNED) sum++;
                }
            }
            scanned[i] = sum;
        }
        model.addAttribute("previousScanned", scanned);


        return "dashboard/index";
    }

}
