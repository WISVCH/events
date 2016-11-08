package ch.wisv.events.data.model.product;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.ArrayList;
import java.util.Collection;

/**
 * ProductSearch Object.
 * Object for search API.
 */
@JsonAutoDetect
public class ProductSearch {

    /**
     * Query used for searching.
     */
    public String query;

    /**
     * Collection of suggested items.
     */
    public Collection<ProductSearchItem> suggestions;

    /**
     * Default Constructor.
     *
     * @param query Search query
     */
    public ProductSearch(String query) {
        this.query = query;
        this.suggestions = new ArrayList<>();
    }

    /**
     * Add suggestion item to ProductSearch.
     *
     * @param title Title of the Product
     * @param key   Key of the Product
     */
    public void addItem(String title, Long key) {
        ProductSearchItem temp = new ProductSearchItem(title, key);
        this.suggestions.add(temp);
    }
}