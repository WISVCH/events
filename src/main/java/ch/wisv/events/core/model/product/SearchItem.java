package ch.wisv.events.core.model.product;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Getter;
import lombok.Setter;

/**
 * SearchItem Object
 */
@JsonAutoDetect
public class SearchItem {

    /**
     * Title of the Product, for API purpose called value.
     */
    @Getter
    @Setter
    public String value;

    /**
     * Key of the Product, for API purpose called data.
     */
    @Getter
    @Setter
    public Long data;

    /**
     * Default constructor.
     *
     * @param title Title of the Product
     * @param key   Key of the Product
     */
    SearchItem(String title, Long key) {
        this.value = title;
        this.data = key;
    }

}
