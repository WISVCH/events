package ch.wisv.events.webhook.event;

import ch.wisv.events.domain.model.AbstractModel;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Delete event.
 */
public class Delete extends ApplicationEvent {

    /**
     * Model concerning the Event.
     */
    @Getter
    AbstractModel model;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public Delete(AbstractModel source) {
        super(source);
        this.model = source;
    }
}
