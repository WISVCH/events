package ch.wisv.events.services;

import ch.wisv.events.domain.model.order.Order;
import ch.wisv.events.domain.model.user.User;
import ch.wisv.events.domain.repository.OrderItemRepository;
import ch.wisv.events.domain.repository.OrderRepository;
import java.util.List;
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

    public OrderValidationService(OrderRepository orderRepository, ProductService productService) {
        this.orderRepository = orderRepository;
        this.productService = productService;
    }

    public void assertOrderIsValidForCustomer(Order order, User customer) {
        order.getItems().forEach(item -> {
            if (item.getProduct().getMaxNumberOfTicketPerUser() > 0) {
                orderRepository.findAllByCustomerAndItemsContainsProduct(customer, item.getProduct());
            }
        });
    }
}
