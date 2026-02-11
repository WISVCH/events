package ch.wisv.events.core.model.order;

import lombok.Getter;

import java.util.function.Function;

/**
 * Payment method enum.
 */
public enum PaymentMethod {

    /**
     * User paid his order with cash.
     */
    CASH("cash", cost -> cost),

    /**
     * User paid his order by card.
     */
    CARD("card", cost -> cost),

    /**
     * User paid his order via Mollie iDeal.
     */
    IDEAL("ideal", cost -> cost + 0.35),

    /**
     * User paid his order via Mollie SOFORT.
     */
    SOFORT("sofort", cost -> 1.01089 * cost + 0.3025),

    /**
     * User paid his order via Mollie.
     */
    MOLLIE("mollie", cost -> cost + 0.39),

    /**
     * User paid his order via CHpay.
     */
    CHPAY("CHpay", cost -> cost),

    /**
     * User paid his order via another method.
     */
    OTHER("other", cost -> cost);

    /** gets the name. */
    @Getter
    private final String name;

    /** gets the transaction cost function. */
    @Getter
    private final Function<Double, Double> transactionCost;

    PaymentMethod(String name, Function<Double, Double> transactionCost) {
        this.name = name;
        this.transactionCost = transactionCost;
    }

    /**
     * Calculate the cost including transaction cost.
     *
     * @param cost of type double
     *
     * @return double
     */
    public double calculateCostIncludingTransaction(double cost) {
        return Math.round(transactionCost.apply(cost) * 100.d) / 100.d;
    }

    /**
     * Return corresponding integer
     *
     * @return int
     */
    public int toInt() {
        return this.ordinal();
    }
}
