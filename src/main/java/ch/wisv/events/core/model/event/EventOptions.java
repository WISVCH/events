package ch.wisv.events.core.model.event;

import ch.wisv.events.utils.LDAPGroup;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * EventOption
 */
@Data
@AllArgsConstructor
public class EventOptions implements Serializable {

    /**
     * Status of the Event
     */
    public EventStatus published;

    /**
     * Field organizedBy
     */
    public LDAPGroup organizedBy;

    /**
     * Default constructor, with status not published.
     */
    public EventOptions() {
        this.published = EventStatus.NOT_PUBLISHED;
        this.organizedBy = LDAPGroup.BESTUUR;
    }

}
