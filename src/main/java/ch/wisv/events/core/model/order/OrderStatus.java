package ch.wisv.events.core.model.order;

public enum OrderStatus {

    ANONYMOUS,   // Initial creating status
    ASSIGNED,    // Has a Customer assigned
    PENDING,     // Sent to the payment provider
    PAID,        // Confirmed paid by payment provider
    EXPIRED,     // Payment attempted, but expired
    CANCELLED,   // Order has been cancelled by the Customer
    ERROR,       // An error happened with the Order
    RESERVATION, // Order is a reservation
    REJECTED     // Order has been rejected by an Admin

}
