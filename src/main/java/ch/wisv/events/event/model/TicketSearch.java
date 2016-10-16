package ch.wisv.events.event.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by sven on 16/10/2016.
 */
@JsonAutoDetect
public class TicketSearch {

    public String query;

    public Collection<TicketSearchItem> suggestions;

    public TicketSearch(String query) {
        this.query = query;
        this.suggestions = new ArrayList<>();
    }

    public void addItem(String title, String key) {
        TicketSearchItem temp = new TicketSearchItem(title, key);
        this.suggestions.add(temp);
    }
}
