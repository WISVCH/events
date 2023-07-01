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
public class OrderVatTest {

    /** Products. */
    private Product productTaxFree;
    private Product productTaxLow;
    private Product productTaxHigh;
    private Product productTaxZero;

    /** Order. */
    private Order order;

    @Before
    public void setUp() throws Exception {
        productTaxFree = new Product();
        productTaxFree.setCost(13.0);
        productTaxFree.setVatRate(VatRate.VAT_FREE);

        productTaxLow = new Product();
        productTaxLow.setCost(15.0);
        productTaxLow.setVatRate(VatRate.VAT_LOW);

        productTaxHigh = new Product();
        productTaxHigh.setCost(17.0);
        productTaxHigh.setVatRate(VatRate.VAT_HIGH);

        productTaxZero = new Product();
        productTaxZero.setCost(19.0);
        productTaxZero.setVatRate(VatRate.VAT_ZERO);

        order = new Order();
        order.setOwner(mock(Customer.class));
        order.setCreatedBy("events-online");
        order.setPaymentMethod(PaymentMethod.CASH);
        order.setStatus(OrderStatus.PAID);
    }

    /**
     * Test the getVatRate on Products
     */
    @Test
    public void testGetVatRate() {
        assertEquals(VatRate.VAT_FREE, productTaxFree.getVatRate());
        assertEquals(VatRate.VAT_LOW, productTaxLow.getVatRate());
        assertEquals(VatRate.VAT_HIGH, productTaxHigh.getVatRate());
        assertEquals(VatRate.VAT_ZERO, productTaxZero.getVatRate());

        assertEquals("VAT Free", productTaxFree.getVatRate().getPercentage());
        assertEquals("9.0%", productTaxLow.getVatRate().getPercentage());
        assertEquals("21.0%", productTaxHigh.getVatRate().getPercentage());
        assertEquals("0.0%", productTaxZero.getVatRate().getPercentage());

        assertEquals(0.0, productTaxFree.getVatRate().getVatRate(), 0.0);
        assertEquals(9.0, productTaxLow.getVatRate().getVatRate(), 0.0);
        assertEquals(21.0, productTaxHigh.getVatRate().getVatRate(), 0.0);
        assertEquals(0.0, productTaxZero.getVatRate().getVatRate(), 0.0);
    }

    /**
     * Test order with 1 product with VAT Free
     */
    @Test
    public void testOrderWithOneProductWithVatFree() {
        OrderProduct orderProduct = new OrderProduct(productTaxFree, 13.d, 1L);

        order.addOrderProduct(orderProduct);
        order.updateOrderAmount();

        assertEquals(VatRate.VAT_FREE, orderProduct.getVatRate());
        assertEquals(0.0, orderProduct.getVat(), 0.0);
        assertEquals(13.0, order.getAmount(), 0.0);
        assertEquals(0.0, order.getVat(), 0.0);
    }

    /**
     * Test order with 3 product with VAT Low
     */
    @Test
    public void testOrderWithThreeProductWithVatLow() {
        OrderProduct orderProduct = new OrderProduct(productTaxLow, 15.d, 3L);

        order.addOrderProduct(orderProduct);
        order.updateOrderAmount();

        // Vat Low is 9%. Vat = 15/1.09 * 0.09 = 1.24 per product
        assertEquals(VatRate.VAT_LOW, orderProduct.getVatRate());
        assertEquals(1.24, orderProduct.getVat(), 0.0);


        assertEquals(45.0, order.getAmount(), 0.0);
        assertEquals(3*1.24, order.getVat(), 0.0001);
    }

    /**
     * Test order with different vat rates
     */
    @Test
    public void testOrderWithProductsWithDifferentVatRates() {
        OrderProduct orderProduct1 = new OrderProduct(productTaxLow, 15.d, 3L); // 15/1.09 * 0.09 = 1.24
        OrderProduct orderProduct2 = new OrderProduct(productTaxHigh, 17.d, 2L); // 17/1.21 * 0.21 = 2.95
        OrderProduct orderProduct3 = new OrderProduct(productTaxZero, 19.d, 1L);
        OrderProduct orderProduct4 = new OrderProduct(productTaxFree, 13.d, 1L);

        order.setOrderProducts(ImmutableList.of(orderProduct1, orderProduct2, orderProduct3, orderProduct4));
        order.updateOrderAmount();

        assertEquals(1.24, orderProduct1.getVat(), 0.0);
        assertEquals(2.95, orderProduct2.getVat(), 0.0);
        assertEquals(0.0, orderProduct3.getVat(), 0.0);
        assertEquals(0.0, orderProduct4.getVat(), 0.0);

        assertEquals(111.0, order.getAmount(), 0.0);
        assertEquals(3*1.24 + 2*2.95, order.getVat(), 0.0001);
    }

    /**
     * Test order with multiple products of same vat rate
     */
    @Test
    public void testOrderWithProductsWithSameVatRate() {
        OrderProduct orderProduct1 = new OrderProduct(productTaxLow, 15.d, 3L); // 15/1.09 * 0.09 = 1.24
        OrderProduct orderProduct2 = new OrderProduct(productTaxLow, 17.d, 2L); // 17/1.09 * 0.09 = 1.40

        order.setOrderProducts(ImmutableList.of(orderProduct1, orderProduct2));
        order.updateOrderAmount();

        assertEquals(1.24, orderProduct1.getVat(), 0.0);
        assertEquals(1.40, orderProduct2.getVat(), 0.0);

        assertEquals(75.0, order.getAmount(), 0.0);
        assertEquals(3*1.24 + 2*1.40, order.getVat(), 0.0001);
    }

}
