package ch.wisv.events.domain.model.order;

/**
 * The enum Order status.
 */
public enum OrderStatus {
    /**
     * Initial creating status.
     */
    ANONYMOUS,

    /**
     * Has a Customer assigned.
     */
    ASSIGNED,

    /**
     * Has been paid.
     */
    PAID;
}
