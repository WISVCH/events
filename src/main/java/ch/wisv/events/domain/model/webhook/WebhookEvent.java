package ch.wisv.events.domain.model.webhook;

import javax.validation.constraints.NotNull;
import lombok.Getter;

/**
 * WebhookEvent enum.
 */
public enum WebhookEvent {

    EVENT_CREATE_EDIT("EVENT_CREATE_UPDATE", "Event create or update", "Creation or update of an Event."),
    EVENT_DELETE("EVENT_DELETE", "", ""),
    PRODUCT_CREATE_EDIT("PRODUCT_CREATE_EDIT", "", ""),
    PRODUCT_DELETE("PRODUCT_DELETE", "", ""),
    ORDER_PAID("ORDER_PAID", "", "");

    /**
     * Tag of the WebhookEvent.
     */
    @NotNull
    @Getter
    private final String tag;

    /**
     * Name of the WebhookEvent.
     */
    @NotNull
    @Getter
    private final String name;

    /**
     * Description of the WebhookEvent.
     */
    @NotNull
    @Getter
    private final String description;

    /**
     * WebhookEvent constructor.
     *
     * @param tag         of type String
     * @param name        of type String
     * @param description of type String
     */
    WebhookEvent(String tag, String name, String description) {
        this.tag = tag;
        this.name = name;
        this.description = description;
    }
}
