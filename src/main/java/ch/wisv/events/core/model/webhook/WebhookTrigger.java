package ch.wisv.events.core.model.webhook;

import lombok.Getter;

/**
 * Copyright (c) 2017 Can i spend it
 *
 * @author svenp
 * @since 20170725
 */
public enum WebhookTrigger {

    EVENT_CREATE("Event creation", "On create of a new event."),
    EVENT_UPDATE("Event update", "On change of an existing event."),
    EVENT_DELETE("Event delete", "Delete of an existing event.");

    /**
     * Field name
     */
    @Getter
    private final String name;

    /**
     * Field description
     */
    @Getter
    private final String description;

    /**
     * Constructor WebhookTrigger creates a new WebhookTrigger instance.
     *
     * @param name        of type String
     * @param description of type String
     */
    WebhookTrigger(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
