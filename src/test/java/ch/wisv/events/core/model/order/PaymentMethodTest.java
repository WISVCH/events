package ch.wisv.events.core.model.order;

import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.util.VatRate;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class PaymentMethodTest {

    @Test
    public void testCalculateCostIncludingTransaction_Cash() {
        double cost = 50.0;
        PaymentMethod paymentMethod = PaymentMethod.CASH;

        double expected = 50.0;
        double actual = paymentMethod.calculateCostIncludingTransaction(cost);

        assertEquals(expected, actual, 0.001);
    }

    @Test
    public void testCalculateCostIncludingTransaction_Card() {
        double cost = 50.0;
        PaymentMethod paymentMethod = PaymentMethod.CARD;

        double expected = 50.0;
        double actual = paymentMethod.calculateCostIncludingTransaction(cost);

        assertEquals(expected, actual, 0.001);
    }
    @Test
    public void testCalculateCostIncludingTransaction_IDEAL() {
        double cost = 50.0;
        PaymentMethod paymentMethod = PaymentMethod.IDEAL;

        double expected = 50.35;
        double actual = paymentMethod.calculateCostIncludingTransaction(cost);

        assertEquals(expected, actual, 0.001);
    }

    @Test
    public void testCalculateCostIncludingTransaction_SOFORT() {
        double cost = 50.0;
        PaymentMethod paymentMethod = PaymentMethod.SOFORT;

        double expected = 50.85; // Rounded 50.847
        double actual = paymentMethod.calculateCostIncludingTransaction(cost);

        assertEquals(expected, actual, 0.001);
    }

    @Test
    public void testCalculateCostIncludingTransaction_OTHER() {
        double cost = 50.0;
        PaymentMethod paymentMethod = PaymentMethod.OTHER;

        double expected = 50.0; // Since OTHER method has no transaction cost, the expected value should be same as the cost
        double actual = paymentMethod.calculateCostIncludingTransaction(cost);

        assertEquals(expected, actual, 0.001);
    }
}
