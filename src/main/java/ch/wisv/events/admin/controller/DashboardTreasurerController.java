package ch.wisv.events.admin.controller;

import ch.wisv.events.core.admin.TreasurerData;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import ch.wisv.events.core.repository.OrderRepository;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
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
     * The treasurerRepository returns a list of TreasurerData that contains 5 parameters:
     *  - the PaidAt date
     *  - The title of the product
     *  - The price of the product
     *  - The amount bought of the product
     *  - The vatRate of the product
     *
     *  This is all the data required to create the treasurer data page.
     *
     * @return HashMap
     */
    private Map<LocalDate, Map<String, Triple<Double, Integer, String>>> generateProductMap() {
        List<TreasurerData> treasurerData = orderRepository.findallPayments();
        ListIterator<TreasurerData> listIterator = treasurerData.listIterator(treasurerData.size());
        Map<LocalDate, Map<String, Triple<Double, Integer, String>>> map = new TreeMap<>();

        while (listIterator.hasPrevious()) {
            TreasurerData data = listIterator.previous();
            LocalDateTime paidAt = data.getPaidAt();
            if (paidAt == null) {
                continue;
            }
            
            // Set date to first of the month,
            // in this way all orders that are paid in the same month will have the same date
            LocalDate date = paidAt.toLocalDate();
            date = LocalDate.of(date.getYear(), date.getMonthValue(), 1);            

            Map<String, Triple<Double, Integer, String>> list = map.getOrDefault(date, new HashMap<>());
            if (!list.containsKey(data.getProductTitle())) {
                list.put(data.getProductTitle(), new ImmutableTriple<>(data.getPrice(), data.getAmount(), data.getVatRate()));
            } else {
                list.put(data.getProductTitle(),
                        new ImmutableTriple<>(data.getPrice(), list.get(data.getProductTitle()).getMiddle()+data.getAmount(), data.getVatRate())
                );
            }

            map.put(date, list);
        }

        return map;
    }
}
