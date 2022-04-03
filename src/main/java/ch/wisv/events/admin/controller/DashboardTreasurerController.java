package ch.wisv.events.admin.controller;

import ch.wisv.events.admin.model.TreasurerData;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.PaymentMethod;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.repository.TreasurerRepository;
import ch.wisv.events.core.service.order.OrderService;
import java.time.LocalDate;
import java.util.*;

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
    private final TreasurerRepository treasurerRepository;

    /**
     * DashboardWebhookController constructor.
     *
     * @param treasurerRepository of type TreasurerRepository
     */
    @Autowired
    public DashboardTreasurerController(TreasurerRepository treasurerRepository) {
        this.treasurerRepository = treasurerRepository;
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
        List<TreasurerData> treasurerData = treasurerRepository.findallPayments();
        Map<LocalDate, Map<String, Pair<Double, Integer>>> map = new TreeMap<>();

        for (TreasurerData data : treasurerData) {
            LocalDate date = data.paidAt.toLocalDate();
            date = LocalDate.of(date.getYear(), date.getMonthValue(), 1);

            Map<String, Pair<Double, Integer>> list = map.getOrDefault(date, new HashMap<>());
            if (!list.containsKey(data.title)) {
                list.put(data.title, new ImmutablePair<>(data.price, data.amount));
            } else {
                list.put(data.title, new ImmutablePair<>(data.price, list.get(data.title).getRight()+data.amount));
            }

            map.put(date, list);
        }

        return map;
    }
}
