package ch.wisv.events.data.model.product;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * SearchItem Object
 */
@JsonAutoDetect
public class SearchItem {

    /**
     * Title of the Product, for API purpose called value.
     */
    public String value;

    /**
     * Key of the Product, for API purpose called data.
     */
    public Long data;

    /**
     * Default constructor.
     *
     * @param title Title of the Product
     * @param key   Key of the Product
     */
    public SearchItem(String title, Long key) {
        this.value = title;
        this.data = key;
    }

}
