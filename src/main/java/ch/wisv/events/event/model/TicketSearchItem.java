package ch.wisv.events.event.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Created by sven on 16/10/2016.
 */
@JsonAutoDetect
public class TicketSearchItem {

    public String value;

    public Long data;

    public TicketSearchItem(String title, Long key) {
        this.value = title;
        this.data = key;
    }

}
