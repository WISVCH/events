package ch.wisv.events.webshop.service;

import ch.wisv.events.core.exception.normal.EventsException;
import ch.wisv.events.core.exception.normal.PaymentsStatusUnknown;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.service.mail.MailService;
import ch.wisv.events.core.service.order.OrderService;
import static java.lang.Thread.sleep;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * WebshopServiceImpl class.
 */
@Service
public class WebshopServiceImpl implements WebshopService {

    /** Max retry attempt to fetch payment status. */
    private static final int MAX_PAYMENT_FETCH_ATTEMPT = 5;

    /** Amount of milli secs between status fetch attempt. */
    private static final int WAIT_BETWEEN_STATUS_FETCH_ATTEMPT = 500;

    /** PaymentsService paymentsService. */
    private final PaymentsService paymentsService;

    /** OrderService orderService. */
    private final OrderService orderService;

    /** MailService mailService. */
    private final MailService mailService;

    /**
     * Constructor.
     *
     * @param paymentsService of type PaymentsService
     * @param orderService    of type OrderService
     * @param mailService     of type MailService
     */
    public WebshopServiceImpl(PaymentsService paymentsService, OrderService orderService, MailService mailService) {
        this.paymentsService = paymentsService;
        this.orderService = orderService;
        this.mailService = mailService;
    }

    /**
     * Filter the products in a Event which are not sold now.
     *
     * @param event of type Event
     *
     * @return Event
     */
    @Override
    public Event filterEventProductNotSalable(Event event) {
        List<Product> salableProducts = event.getProducts().stream()
                .filter(this.filterProductBySellInterval())
                .filter(this.filterProductSoldOut())
                .collect(Collectors.toList());
        event.setProducts(salableProducts);

        return event;
    }

    /**
     * Filter the products by events if they can be sold or not.
     *
     * @param events of type List
     *
     * @return List
     */
    @Override
    public List<Event> filterEventProductNotSalable(List<Event> events) {
        return events.stream().map(this::filterEventProductNotSalable).filter(event -> event.getProducts().size() > 0).collect(Collectors.toList());
    }

    /**
     * Update the status of the Order via the Payments API.
     *
     * @param order             of type Order
     * @param paymentsReference of type String
     *
     * @throws EventsException when Order status update is invalid.
     */
    @Override
    public void updateOrderStatus(Order order, String paymentsReference) throws EventsException {
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
            case "EXPIRED":
                orderService.updateOrderStatus(order, OrderStatus.EXPIRED);
                break;
            default:
                throw new PaymentsStatusUnknown(status);
        }
    }

    /**
     * Fetch OrderStatus from CH Payments and retry if it fails the first time.
     *
     * @param order             of type Order
     * @param paymentsReference of type String
     *
     * @throws EventsException when Order status update is invalid.
     */
    public void fetchOrderStatus(Order order, String paymentsReference) throws EventsException {
        int count = 0;
        int maxCount = MAX_PAYMENT_FETCH_ATTEMPT;

        while (order.getStatus() == OrderStatus.PENDING && count < maxCount) {
            try {
                this.updateOrderStatus(order, paymentsReference);
                sleep(WAIT_BETWEEN_STATUS_FETCH_ATTEMPT);
            } catch (InterruptedException ignored) {
            }

            count++;
        }

        if (count == maxCount) {
            orderService.updateOrderStatus(order, OrderStatus.ERROR);
            mailService.sendErrorPaymentOrder(order);
        }
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
     * Check if Product is not sold out.
     *
     * @return Predicate
     */
    private Predicate<Product> filterProductSoldOut() {
        return product -> product.getMaxSold() == null || product.getSold() != product.getMaxSold();
    }
}
