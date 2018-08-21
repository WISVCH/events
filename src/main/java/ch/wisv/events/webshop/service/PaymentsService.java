package ch.wisv.events.webshop.service;

import ch.wisv.events.core.exception.normal.PaymentsStatusUnknown;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderStatus;
import org.apache.http.client.methods.HttpPost;

/**
 * PaymentsService interface.
 */
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

    /**
     * Map a CH Payments status to a OrderStatus.
     *
     * @param status of type String
     *
     * @return OrderStatus
     *
     * @throws PaymentsStatusUnknown when the PaymentsStatus is unknown.
     */
    OrderStatus mapStatusToOrderStatus(String status) throws PaymentsStatusUnknown;
}
