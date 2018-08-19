package ch.wisv.events.core.model.order;

/**
 * Payment method enum.
 */
public enum PaymentMethod {

    CASH,        // User paid his order with cash
    CARD,        // User paid his order by card
    IDEAL,       // User paid his order via Mollie iDeal
    CREDIT_CARD, // User paid his order via Mollie Credit Card
    OTHER        // User paid his order via another method
}
