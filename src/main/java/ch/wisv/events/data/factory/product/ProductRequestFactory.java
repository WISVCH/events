package ch.wisv.events.data.factory.product;

import ch.wisv.events.data.model.product.Product;
import ch.wisv.events.data.request.product.ProductRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * ProductRequestFactory.
 */
public class ProductRequestFactory {

    /**
     * DateTimeFormatter.
     */
    private static DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    /**
     * Create new ProductRequest from Product.
     *
     * @param product Event
     * @return ProductRequest
     */
    public static ProductRequest create(Product product) {
        return new ProductRequest(
                product.getId(),
                product.getKey(),
                product.getTitle(),
                product.getSellStart().toString(),
                (product.getSellEnd() == null) ? "" : product.getSellEnd().toString(),
                product.getDescription(),
                product.getCost(),
                product.getMaxSold()
        );
    }

    /**
     * Create new Product from ProductRequest
     *
     * @param request ProductRequest
     * @return Product
     */
    public static Product create(ProductRequest request) {
        return new Product(
                request.getTitle(),
                request.getDescription(),
                request.getCost(),
                request.getMaxSold(),
                LocalDateTime.parse(request.getSellStart(), format),
                (Objects.equals(request.getSellEnd(), "")) ? null : LocalDateTime.parse(request.getSellEnd(), format)
        );
    }

    /**
     * Update Product by ProductRequest
     *
     * @param product Product
     * @param request ProductRequest
     * @return Product
     */
    public static Product update(Product product, ProductRequest request) {
        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setSellStart(LocalDateTime.parse(request.getSellStart(), format));
        product.setSellEnd(LocalDateTime.parse(request.getSellEnd(), format));
        product.setCost(request.getCost());
        product.setMaxSold(request.getMaxSold());

        return product;
    }

}
