package ch.wisv.events.admin.controller;

import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.PaymentMethod;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.service.order.OrderService;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
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
@RequestMapping("/administrator/penningmeester")
@PreAuthorize("hasRole('ADMIN')")
public class DashboardPenningmeesterController extends DashboardController {

    /** OrderService. */
    private final OrderService orderService;

    /**
     * DashboardWebhookController constructor.
     *
     * @param orderService of type OrderService
     */
    @Autowired
    public DashboardPenningmeesterController(OrderService orderService) {
        this.orderService = orderService;
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

        return "admin/penningmeester/index";
    }

    /**
     * Generate product map on local date.
     *
     * @return HashMap
     */
    private Map<LocalDate, Map<Product, Integer>> generateProductMap() {
        Map<LocalDate, Map<Product, Integer>> map = new HashMap<>();

        for (Order order : orderService.getAllPaid()) {
            if (order.getPaymentMethod() == PaymentMethod.IDEAL || order.getPaymentMethod() == PaymentMethod.SOFORT) {
                LocalDate date = order.getPaidAt().toLocalDate();
                date = LocalDate.of(date.getYear(), date.getMonthValue(), 1);

                Map<Product, Integer> list = map.getOrDefault(date, new HashMap<>());
                order.getOrderProducts().stream().filter(orderProduct -> orderProduct.getPrice() > 0).forEach(orderProduct -> {
                    Product product = orderProduct.getProduct();
                    int value = orderProduct.getAmount().intValue();
                    if (!list.containsKey(product)) {
                        list.put(product, value);
                    } else {
                        list.put(product, list.get(product) + value);
                    }
                });

                map.put(date, list);
            }
        }

        return map;
    }
}
