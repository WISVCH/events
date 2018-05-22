package ch.wisv.events.core.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;

/**
 * SearchItem class.
 */
@Data
@JsonAutoDetect
public class SearchItem {

    /**
     * Title of the Product, for API purpose called value.
     */
    public String value;

    /**
     * Key of the Product, for API purpose called data.
     */
    public Integer data;

    /**
     * Default constructor.
     *
     * @param title Title of the Product
     * @param key   Key of the Product
     */
    SearchItem(String title, Integer key) {
        this.value = title;
        this.data = key;
    }

}
