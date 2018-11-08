package ch.wisv.events.services;

import ch.wisv.events.domain.model.product.Product;
import ch.wisv.events.domain.repository.ProductRepository;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ProductService class.
 */
@Service
@Transactional
public class ProductService extends AbstractService<Product> {

    /**
     * ProductRepository constructor.
     *
     * @param productRepository of type ProductRepository
     */
    @Autowired
    public ProductService(ProductRepository productRepository) {
        super(productRepository);
    }

    /**
     * Assert if a model is detetable.
     *
     * @param model of type T
     */
    @Override
    void assertIfDeletable(Product model) {
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
        return model;
    }
}
