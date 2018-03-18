package ch.wisv.events.webshop.service;

import ch.wisv.events.core.model.order.Order;
import org.apache.http.client.methods.HttpPost;

public interface PaymentsService {

    /**
     * Get the Order status of Payments.
     *
     * @param paymentsReference of type String
     *
     * @return String
     */
    String getPaymentsOrderStatus(String paymentsReference);

    /**
     * Get a Mollie Url via Payments.
     *
     * @param order of type Order
     *
     * @return String
     */
    String getPaymentsMollieUrl(Order order);

    /**
     * Create a HttpPost to create a Payments Order request.
     *
     * @param order of type Order
     *
     * @return HttpPost
     */
    HttpPost createPaymentsOrderHttpPost(Order order);
}
