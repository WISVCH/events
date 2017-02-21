package ch.wisv.events.app.controller.dashboard;

import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.service.event.EventService;
import ch.wisv.events.core.service.product.SoldProductService;
import org.springframework.beans.factory.annotation.Autowired;
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
     * @param eventService EventService
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
        List<Event> events = eventService.getAllEvents();
        List<Event> upcoming = eventService.soldFiveUpcoming();
        List<Event> previous = eventService.soldFivePrevious();

//        // Needed info for the count stuff;
        model.addAttribute("countEvent", eventService.getAllEvents().size());
//        model.addAttribute("countProduct", .count());
//        model.addAttribute("countOrder", orderRepository.count());
//        model.addAttribute("countCustomer", customerRepository.count());
//        model.addAttribute("countSold", soldProductRepository.count());
//        model.addAttribute("countSoldScanned", soldProductRepository.findAll().stream().filter(x -> x.getStatus() ==
//                SoldProductStatus.SCANNED).count());
//
//        // Needed info for the upcoming events chart.
//        model.addAttribute("upcoming", upcoming.stream().map(Event::getTitle).collect(Collectors.toList()));
//        model.addAttribute("upcomingTarget", upcoming.stream().map(Event::getTarget).collect(Collectors.toList()));
//        model.addAttribute("upcomingSold", upcoming.stream().map(Event::getSold).collect(Collectors.toList()));
//
//        // Needed info for the previous events chart.
//        model.addAttribute("previous", previous.stream().map(Event::getTitle).collect(Collectors.toList()));
//        model.addAttribute("previousTarget", previous.stream().map(Event::getTarget).collect(Collectors.toList()));
//        model.addAttribute("previousSold", previous.stream().map(Event::getSold).collect(Collectors.toList()));
//        model.addAttribute("previousScanned",
//                previous.stream().mapToInt(event -> event.getProducts().stream().mapToInt(
//                        product -> (int) soldProductService.getByProduct(product).stream()
//                                                           .filter(soldProduct -> soldProduct
//                                                                   .getStatus() == SoldProductStatus.SCANNED).count())
//                                                         .sum()).boxed()
//                        .collect(Collectors.toCollection(ArrayList::new)));
//
//
//        // Need info for all the things
//        List<String> dates = new ArrayList<>();
//        LocalDateTime today = LocalDateTime.now();
//        today = today.truncatedTo(ChronoUnit.DAYS);
//        for (int i = 20; i >= 1; i--) {
//            dates.add(today.minusDays(i).format(DateTimeFormatter.ISO_DATE));
//        }
//        model.addAttribute("events", dates);
////        model.addAttribute("events", events.stream().map(Event::getTitle).collect(Collectors.toList()));
//        model.addAttribute("eventsTarget", events.stream().map(Event::getTarget).collect(Collectors.toList()));
//        model.addAttribute("eventsSold", events.stream().map(Event::getSold).collect(Collectors.toList()));

        return "dashboard/index";
    }

}
