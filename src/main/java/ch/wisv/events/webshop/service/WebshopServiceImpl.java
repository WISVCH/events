package ch.wisv.events.webshop.service;

import ch.wisv.events.core.exception.normal.OrderInvalidException;
import ch.wisv.events.core.exception.normal.PaymentsStatusUnknown;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.service.order.OrderService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class WebshopServiceImpl implements WebshopService {

    /**
     * PaymentsService paymentsService.
     */
    private final PaymentsService paymentsService;

    /**
     * OrderService orderService.
     */
    private final OrderService orderService;

    /**
     * Constructor.
     *
     * @param paymentsService of type PaymentsService
     * @param orderService    of type OrderService
     */
    public WebshopServiceImpl(PaymentsService paymentsService, OrderService orderService) {
        this.paymentsService = paymentsService;
        this.orderService = orderService;
    }

    /**
     * Remove Event Products when there are not salable.
     *
     * @param events of type List
     *
     * @return List
     */
    @Override
    public List<Event> filterNotSalableProducts(List<Event> events) {
        events.forEach(event -> {
            List<Product> filterSalableProducts = event.getProducts()
                    .stream()
                    .filter(this.filterProductBySellInterval())
                    .collect(Collectors.toList());
            event.setProducts(filterSalableProducts);
        });

        return events.stream().filter(event -> event.getProducts().size() > 0).collect(Collectors.toList());
    }

    /**
     * Check if Product is in sell interval.
     *
     * @return Predicate
     */
    private Predicate<Product> filterProductBySellInterval() {
        return product -> LocalDateTime.now().isAfter(product.getSellStart()) && LocalDateTime.now().isBefore(product.getSellEnd());
    }

    /**
     * Update the status of the Order via the Payments API.
     *
     * @param order             of type Order
     * @param paymentsReference of type String
     */
    @Override
    public void updateOrderStatus(Order order, String paymentsReference) throws PaymentsStatusUnknown, OrderInvalidException {
        String status = paymentsService.getPaymentsOrderStatus(paymentsReference);

        switch (status) {
            case "WAITING":
                orderService.updateOrderStatus(order, OrderStatus.PENDING);
                break;
            case "PAID":
                orderService.updateOrderStatus(order, OrderStatus.PAID);
                break;
            case "CANCELLED":
                orderService.updateOrderStatus(order, OrderStatus.CANCELLED);
                break;
            default:
                throw new PaymentsStatusUnknown(status);
        }
    }
}
