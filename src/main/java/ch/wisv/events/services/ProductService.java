package ch.wisv.events.services;

import ch.wisv.events.domain.model.product.Product;
import ch.wisv.events.domain.model.product.ProductOption;
import ch.wisv.events.domain.repository.ProductOptionRepository;
import ch.wisv.events.domain.repository.ProductRepository;
import ch.wisv.events.webhook.event.CreateUpdate;
import ch.wisv.events.webhook.event.Delete;
import java.util.List;
import static java.util.Objects.isNull;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * ProductService class.
 */
@Service
@Transactional
public class ProductService extends AbstractService<Product> {

    /**
     * ProductOptionRepository.
     */
    private final ProductOptionRepository productOptionRepository;

    /**
     * ProductRepository constructor.
     *
     * @param publisher               of type ApplicationEventPublisher
     * @param productRepository       of type ProductRepository
     * @param productOptionRepository of type ProductOptionRepository
     */
    @Autowired
    public ProductService(ApplicationEventPublisher publisher, ProductRepository productRepository, ProductOptionRepository productOptionRepository) {
        super(publisher, productRepository);
        this.productOptionRepository = productOptionRepository;
    }

    /**
     * Save the ProductOptions.
     *
     * @param model of type Product
     *
     * @return Product
     */
    private Product saveProductOptions(Product model) {
        // Remove items without a title
        List<ProductOption> optionList = model.getProductOptions().stream()
                .filter(productOption -> isNotEmpty(productOption.getTitle()))
                .collect(Collectors.toList());

        // Fetch item ids of the existing products
        for (ProductOption productOption : optionList) {
            Optional<ProductOption> optional = productOptionRepository.findByPublicReference(productOption.getPublicReference());
            optional.ifPresent(product -> productOption.setItemId(product.getItemId()));
        }

        optionList.forEach(productOptionRepository::saveAndFlush);
        model.setProductOptions(optionList);

        return model;
    }

    /**
     * Something to do after the object has been saved.
     *
     * @param model of type AbstractModel
     */
    @Override
    void afterSave(Product model) {
        publisher.publishEvent(new CreateUpdate(model));
    }

    /**
     * Assert if a model is deletable.
     *
     * @param model of type T
     */
    @Override
    void assertIfDeletable(Product model) {
    }

    /**
     * Something to do after the object has been deleted.
     *
     * @param model of type AbstractModel
     */
    @Override
    void afterDelete(Product model) {
        publisher.publishEvent(new Delete(model));
    }

    /**
     * Create of an AbstractModel.
     *
     * @param model of type AbstractModel
     *
     * @return AbstractModel
     */
    @Override
    protected Product create(Product model) {
        model = this.saveProductOptions(model);

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
    protected Product update(Product model, Product existingModel) {
        model = this.saveProductOptions(model);
        if (isNull(model.getEvent())) {
            model.setEvent(existingModel.getEvent());
        }

        return model;
    }
}
