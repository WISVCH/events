package ch.wisv.events.event.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created by sven on 18/10/2016.
 */
public class EventOptions implements Serializable {

    @Getter
    @Setter
    public EventStatus published;

    public EventOptions() {
        this.published = EventStatus.NOT_PUBLISHED;
    }
}
