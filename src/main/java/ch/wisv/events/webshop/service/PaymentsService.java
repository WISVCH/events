package ch.wisv.events.webshop.service;

import ch.wisv.events.core.exception.normal.PaymentsStatusUnknown;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderStatus;

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
