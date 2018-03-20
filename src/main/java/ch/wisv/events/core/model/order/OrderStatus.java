package ch.wisv.events.core.model.order;

/**
 * OrderStatus class.
 */
public enum OrderStatus {

    /** Initial Order status. */
    ANONYMOUS,

    /** When the Customer has been assigned. */
    ASSIGNED,

    /** When the Order has been cancelled. */
    CANCELLED,

    /** iDeal payment in progress. */
    PENDING,

    /** Order is a reservation. */
    RESERVATION,

    /* FINAL STATUSES. */
    /** Order has been paid. */
    PAID,

    /** Order has closed due to an error. */
    ERROR,

    /** Order has been rejected by an admin. */
    REJECTED,

    /** Order has been expired. */
    EXPIRED,
}
