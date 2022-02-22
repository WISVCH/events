package ch.wisv.events.webshop.service;

import ch.wisv.events.core.model.order.Order;

/**
 * PaymentsService interface.
 */
public interface PaymentsService {


    /**
     * Get a Mollie Url via Payments.
     *
     * @param order of type Order
     *
     * @return String
     */
    String getMollieUrl(Order order);

    /**
     * used to process a webhook request made by mollie to signal that the order status has changed
     *
     * @param providerOrderReference reference of the order used by mollie
     * @return the updated order
     */
    Order updateStatusByProviderReference(String providerOrderReference);

}
