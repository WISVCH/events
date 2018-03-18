package ch.wisv.events.core.model.webhook;

import lombok.Getter;

public enum WebhookTaskStatus {

    PENDING("badge-warning"), SUCCESS("badge-success"), ERROR("badge-danger");

    /**
     * Badge class.
     */
    @Getter
    private final String badgeClass;

    /**
     * WebhookTaskStatus constructor.
     *
     * @param badgeClass of type String
     */
    WebhookTaskStatus(String badgeClass) {
        this.badgeClass = badgeClass;
    }
}
