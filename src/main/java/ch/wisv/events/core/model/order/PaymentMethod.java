package ch.wisv.events.core.model.order;

import lombok.Getter;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

/**
 * Payment method enum.
 */
public enum PaymentMethod {

    /**
     * User paid his order with cash.
     */
    CASH("cash", new ExpressionBuilder("x")),

    /**
     * User paid his order by card.
     */
    CARD("card", new ExpressionBuilder("x")),

    /**
     * User paid his order via Mollie iDeal.
     */
    IDEAL("ideal", new ExpressionBuilder("x + 0.35")),

    /**
     * User paid his order via Mollie SOFORT.
     */
    SOFORT("sofort", new ExpressionBuilder("1.01089 * x + 0.3025")),

    /**
     * User paid his order via another method.
     */
    OTHER("other", new ExpressionBuilder("x"));

    /** gets the name. */
    @Getter
    private final String name;

    /** gets the expressionbuilder. */
    @Getter
    private final ExpressionBuilder transactionCost;

    PaymentMethod(String name, ExpressionBuilder transactionCost) {
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
        Expression e = this.getTransactionCost().variables("x")
                .build()
                .setVariable("x", cost);

        return Math.round(e.evaluate() * 100.d) / 100.d;
    }

}
