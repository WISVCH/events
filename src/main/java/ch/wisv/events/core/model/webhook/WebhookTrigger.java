package ch.wisv.events.core.model.webhook;

import lombok.Getter;

public enum WebhookTrigger {

    EVENT_CREATE_UPDATE("EVENT_CREATE_UPDATE", "Event create or update", "Creation or update of an Event."),
    EVENT_DELETE("EVENT_DELETE", "Event delete", "Deletion of an existing event."),
    PRODUCT_CREATE_UPDATE("PRODUCT_CREATE_UPDATE", "Product create or update", "Creation or update of an Product."),
    PRODUCT_DELETE("PRODUCT_DELETE", "Product delete", "Deletion of an existing product.");

    /** Tag of the trigger. */
    @Getter
    private final String tag;

    /** Human readable name of the trigger. */
    @Getter
    private final String name;

    /** Description of the trigger, when it will happen. */
    @Getter
    private final String description;

    /**
     * Constructor WebhookTrigger creates a new WebhookTrigger instance.
     *
     * @param tag         of type String
     * @param name        of type String
     * @param description of type String
     */
    WebhookTrigger(String tag, String name, String description) {
        this.tag = tag;
        this.name = name;
        this.description = description;
    }
}
