package ch.wisv.events.admin.controller;

import ch.wisv.events.core.model.treasurer.TreasurerData;

import java.time.LocalDate;
import java.util.*;

import ch.wisv.events.core.repository.OrderRepository;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * DashboardWebhookController class.
 */
@Controller
@RequestMapping("/administrator/treasurer")
@PreAuthorize("hasRole('ADMIN')")
public class DashboardTreasurerController extends DashboardController {

    /** TreasurerRepository */
    private final OrderRepository orderRepository;

    /**
     * DashboardWebhookController constructor.
     *
     * @param orderRepository of type OrderRepository
     */
    @Autowired
    public DashboardTreasurerController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Index of vendor [GET "/"].
     *
     * @param model String model
     *
     * @return path to Thymeleaf template location
     */
    @GetMapping
    public String index(Model model) {
        model.addAttribute("productMap", this.generateProductMap());

        return "admin/treasurer/index";
    }

    /**
     * Generate product map on local date.
     *
     * This method uses an intensive SQL query in the order repository. It filters order such that
     * all returned orders use IDEAL/SOFORT and were actually paid.
     *
     * The treasurerRepository returns a list of TreasurerData that contains 4 parameters:
     *  - the PaidAt date
     *  - The title of the product
     *  - The price of the product
     *  - The amount bought of the product
     *
     *  This is all the data required to create the treasurer data page.
     *
     * @return HashMap
     */
    private Map<LocalDate, Map<String, Pair<Double, Integer>>> generateProductMap() {
        List<TreasurerData> treasurerData = orderRepository.findallPayments();
        Map<LocalDate, Map<String, Pair<Double, Integer>>> map = new TreeMap<>();

        for (TreasurerData data : treasurerData) {
            LocalDate date = data.getPaidAt().toLocalDate();
            date = LocalDate.of(date.getYear(), date.getMonthValue(), 1);

            Map<String, Pair<Double, Integer>> list = map.getOrDefault(date, new HashMap<>());
            if (!list.containsKey(data.getTitle())) {
                list.put(data.getTitle(), new ImmutablePair<>(data.getPrice(), data.getAmount()));
            } else {
                list.put(data.getTitle(), 
                        new ImmutablePair<>(data.getPrice(), list.get(data.getTitle()).getRight()+data.getAmount())
                );
            }

            map.put(date, list);
        }

        return map;
    }
}
