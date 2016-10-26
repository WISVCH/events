package ch.wisv.events.data.model.event;

import ch.wisv.events.data.model.Model;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created by sven on 18/10/2016.
 */
public class EventOptions implements Serializable, Model {

    @Getter
    @Setter
    public EventStatus published;

    public EventOptions() {
        this.published = EventStatus.PUBLISHED;
    }
}
