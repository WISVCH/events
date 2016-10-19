package ch.wisv.events.event.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by sven on 16/10/2016.
 */
@JsonAutoDetect
public class ProductSearch {

    public String query;

    public Collection<ProductSearchItem> suggestions;

    public ProductSearch(String query) {
        this.query = query;
        this.suggestions = new ArrayList<>();
    }

    public void addItem(String title, Long key) {
        ProductSearchItem temp = new ProductSearchItem(title, key);
        this.suggestions.add(temp);
    }
}
