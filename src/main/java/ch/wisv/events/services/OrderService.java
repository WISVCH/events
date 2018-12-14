package ch.wisv.events.services;

import ch.wisv.events.domain.exception.InvalidDeletableException;
import ch.wisv.events.domain.model.order.Order;
import ch.wisv.events.domain.model.order.OrderItem;
import ch.wisv.events.domain.model.product.ProductOption;
import ch.wisv.events.domain.repository.OrderItemRepository;
import ch.wisv.events.domain.repository.OrderRepository;
import ch.wisv.events.infrastructure.webshop.dto.OrderDto;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * OrderService.
 */
@Service
@Transactional
public class OrderService extends AbstractService<Order> {

    /** OrderItemRepository. */
    private final OrderItemRepository orderItemRepository;

    /** ProductService. */
    private final ProductService productService;

    /**
     * AbstractRepository constructor.
     *
     * @param publisher           of type ApplicationEventPublisher
     * @param repository          of type OrderRepository
     * @param orderItemRepository of type OrderItemRepository
     * @param productService      of type ProductService
     */
    public OrderService(
            ApplicationEventPublisher publisher,
            OrderRepository repository,
            OrderItemRepository orderItemRepository,
            ProductService productService
    ) {
        super(publisher, repository);
        this.orderItemRepository = orderItemRepository;
        this.productService = productService;
    }

    /**
     * Create Order by DTO.
     *
     * @param orderDto of type OrderDto.
     *
     * @return Order
     */
    public Order createByOrderDto(OrderDto orderDto) {
        Order order = new Order();
        orderDto.getProducts().forEach(product -> {
            ProductOption productOptionByPublicReference = null;
            if (isNotEmpty(product.getProductOptionKey()) && !product.getProductOptionKey().equals("undefined")) {
                productOptionByPublicReference = this.productService.getProductOptionByPublicReference(product.getProductOptionKey());
            }

            order.addItem(new OrderItem(
                    this.productService.getByPublicReference(product.getProductKey()),
                    productOptionByPublicReference,
                    product.getAmount()
            ));
        });

        this.save(order);

        return order;
    }

    /**
     * Something to do before the object has been saved.
     *
     * @param model of type AbstractModel
     */
    @Override
    void beforeSave(Order model) {
        this.orderItemRepository.save(model.getItems());

        // Set total price.
        model.setTotalPrice(model.getItems().stream().mapToDouble(item -> {
            Double price = item.getProduct().getPrice();
            if (nonNull(item.getProductOption())) {
                price += item.getProductOption().getAdditionalPrice();
            }
            return price;
        }).sum());
    }

    /**
     * Something to do after the object has been saved.
     *
     * @param model of type AbstractModel
     */
    @Override
    void afterSave(Order model) {
    }

    /**
     * Assert if a model is deletable.
     *
     * @param model of type T
     */
    @Override
    void assertIfDeletable(Order model) {
        throw new InvalidDeletableException("Orders cannot be deleted!");
    }

    /**
     * Something to do after the object has been deleted.
     *
     * @param model of type AbstractModel
     */
    @Override
    void afterDelete(Order model) {
    }

    /**
     * Create of an AbstractModel.
     *
     * @param model of type AbstractModel
     *
     * @return AbstractModel
     */
    @Override
    protected Order create(Order model) {
        return model;
    }

    /**
     * Update of an AbstractModel.
     *
     * @param model         of type AbstractModel
     * @param existingModel of type AbstractModel
     *
     * @return AbstractModel
     */
    @Override
    protected Order update(Order model, Order existingModel) {
        return model;
    }
}
