package ch.wisv.events.core.model.order;

/**
 * Payment method enum.
 */
public enum PaymentMethod {

    /**
     * User paid his order with cash.
     */
    CASH,

    /**
     * User paid his order by card.
     */
    CARD,

    /**
     * User paid his order via Mollie iDeal.
     */
    IDEAL,

    /**
     * User paid his order via Mollie SOFORT.
     */
    SOFORT,
    /**
     * User paid his order via another method.
     */
    OTHER

}
