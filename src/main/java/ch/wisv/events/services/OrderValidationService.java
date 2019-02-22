package ch.wisv.events.services;

import ch.wisv.events.domain.exception.OrderInvalidException;
import ch.wisv.events.domain.model.order.Order;
import ch.wisv.events.domain.model.user.User;
import ch.wisv.events.domain.repository.OrderRepository;
import java.time.ZonedDateTime;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * OrderValidationService.
 */
@Service
@Transactional
public class OrderValidationService {

    /** OrderRepository. */
    private final OrderRepository orderRepository;

    /** ProductService. */
    private final ProductService productService;

    /**
     * @param orderRepository of type OrderRepository
     * @param productService  of type ProductService
     */
    public OrderValidationService(OrderRepository orderRepository, ProductService productService) {
        this.orderRepository = orderRepository;
        this.productService = productService;
    }

    /**
     *
     * @param order
     * @param customer
     */
    public void assertOrderIsValidForCustomer(Order order, User customer) {
        order.getItems().forEach(item -> {
            if (item.getProduct().getMaxNumberOfTicketPerUser() > 0) {
                orderRepository.findAllByCustomerAndItemsContainsProduct(customer, item.getProduct());
            }
        });
    }

    /**
     * Assert if an Order is valid for checkout.
     *
     * @param order of type Order
     * @throws OrderInvalidException Invalid order exception when order is invalid.
     */
    public void assertIsValidForCheckout(Order order) {
        order.getItems().forEach(orderItem -> {
            if (orderItem.getProduct().getEvent() == null) {
                throw new OrderInvalidException("One or more invalid products in this order");
            }

            if (orderItem.getProduct().isMandatoryProductOption()) {
                if (isNull(orderItem.getProductOption())) {
                    throw new OrderInvalidException("One or more invalid products in this order");
                }
            }

            if (orderItem.getProduct().getTicketLimit() > 0) {
                if (orderItem.getProduct().getSold() == orderItem.getProduct().getTicketLimit()) {
                    throw new OrderInvalidException("One or more products in out of stock");
                }
            }

            if (orderItem.getProduct().getEvent().getEnding().isBefore(ZonedDateTime.now())) {
                throw new OrderInvalidException("One or more products are no longer be sold");
            }

            if (nonNull(orderItem.getProductOption())) {
                if (!orderItem.getProduct().getProductOptions().contains(orderItem.getProductOption())) {
                    throw new OrderInvalidException("Invalid combination of product and additional option");
                }
            }
        });
    }
}
