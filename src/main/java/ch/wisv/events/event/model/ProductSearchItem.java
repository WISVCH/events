package ch.wisv.events.event.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Created by sven on 16/10/2016.
 */
@JsonAutoDetect
public class ProductSearchItem {

    public String value;

    public Long data;

    public ProductSearchItem(String title, Long key) {
        this.value = title;
        this.data = key;
    }

}
