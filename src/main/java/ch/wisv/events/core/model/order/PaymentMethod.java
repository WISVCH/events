package ch.wisv.events.core.model.order;

public enum PaymentMethod {

    CASH,       // User paid his order with cash
    CARD,       // User paid his order by card
    IDEAL,      // User paid his order via mollie
    OTHER       // User paid his order via another method
}
