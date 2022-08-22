package ch.wisv.events.webshop.service;

import be.woutschoovaerts.mollie.Client;
import be.woutschoovaerts.mollie.ClientBuilder;
import be.woutschoovaerts.mollie.data.common.Amount;
import be.woutschoovaerts.mollie.data.payment.PaymentMethod;
import be.woutschoovaerts.mollie.data.payment.PaymentRequest;
import be.woutschoovaerts.mollie.data.payment.PaymentResponse;
import be.woutschoovaerts.mollie.exception.MollieException;
import ch.wisv.events.core.exception.normal.EventsException;
import ch.wisv.events.core.exception.normal.OrderNotFoundException;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderProduct;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.service.mail.MailService;
import ch.wisv.events.core.service.order.OrderService;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * PaymentsService implementation.
 */
@Validated
@Service
public class PaymentsServiceImpl implements PaymentsService {

    /**
     * OrderService.
     */
    private final OrderService orderService;

    /**
     * Mollie Client.
     */
    private final Client mollie;

    /**
     * MailService.
     */
    private final MailService mailService;

    /**
     * Payments client url.
     */
    @Value("${mollie.clientUri}")
    @NotNull
    private String clientUri;

    /**
     * Default constructor.
     *
     * @param orderService of type OrderService
     * @param apiKey       of type String
     * @param mailService  of type MailService
     */
    @Autowired
    public PaymentsServiceImpl(OrderService orderService, @Value("${mollie.apikey:null}") String apiKey, MailService mailService) {
        this.orderService = orderService;
        this.mailService = mailService;
        this.mollie = new ClientBuilder().withApiKey(apiKey).build();
    }

    public PaymentsServiceImpl(OrderService orderService, Client mollie, MailService mailService) {
        this.orderService = orderService;
        this.mailService = mailService;
        this.mollie = mollie;
    }

    /**
     * Creates a payment at mollie and gets the checkout url from the response body.
     * @param order of type Order
     *
     * @return the checkout url to redirect the user to
     */
    @Override
    public String getMollieUrl(Order order) {

        PaymentRequest paymentRequest = createMolliePaymentRequestFromOrder(order);

        //First try is for IOExceptions coming from the Mollie Client.
        try {
            // Create the payment over at Mollie
            PaymentResponse molliePayment = mollie.payments().createPayment(paymentRequest);

            // All good, update the order and return the payment url
            updateOrderWithPaymentResponse(order, molliePayment);

            return molliePayment.getLinks().getCheckout().getHref();

        } catch (MollieException e) {
            mailService.sendError("Can't fetch mollie url", e);
            handleMollieError(e);
            return null;
        }
    }

    /**
     * Creates a payment request to use with the mollie client in order to start a payment interaction with Mollie.
     *
     * @param order with type Order
     * @return a payment request that corresponds to the data in order
     */
    protected PaymentRequest createMolliePaymentRequestFromOrder(Order order) {
        Map<String, Object> metadata = new HashMap<>();

        metadata.put("orderId", order.getPublicReference());

        String productString = "";
        // Add the names, quantities and prices of the products to the metadata.
        for (OrderProduct orderProduct : order.getOrderProducts()) {
            // Make string of product name, quantity and price
            productString +=  orderProduct.getProduct().getTitle() + " (" + orderProduct.getAmount() + "x)" + " - €" + orderProduct.getPrice() + "; ";
        }

        metadata.put("products", productString);

        PaymentMethod method;

        if (order.getPaymentMethod() == ch.wisv.events.core.model.order.PaymentMethod.IDEAL) {
            method = PaymentMethod.IDEAL;
        } else {
            method = PaymentMethod.SOFORT;
        }

        String returnUrl = clientUri + "/return/" + order.getPublicReference();
        String webhookUrl = clientUri + "/api/v1/orders/status";

        double value = order.getOrderProducts().stream()
                .mapToDouble(op -> op.getPrice() * op.getAmount())
                .sum();

        value = order.getPaymentMethod().calculateCostIncludingTransaction(value);  
        Amount paymentAmount = Amount.builder().value(BigDecimal.valueOf(value).setScale(2, RoundingMode.CEILING)).currency("EUR").build();

        return PaymentRequest.builder()
                .method(Optional.of(List.of(method)))
                .amount(paymentAmount)
                .description("W.I.S.V. 'Christiaan Huygens'")
                .consumerName(Optional.of(order.getOwner().getName()))
                .billingEmail(Optional.of(order.getOwner().getEmail()))
                .redirectUrl(Optional.of(returnUrl))
                .webhookUrl(Optional.of(webhookUrl))
                .metadata(metadata)
                .build();
    }


    /**
     *  updates the order status with the given provider reference.
     * @param providerOrderReference reference of the order used by mollie
     * @return the updated order
     */
    @Override
    public Order updateStatusByProviderReference(String providerOrderReference) {
        try {
            Order order = orderService.getByChPaymentsReference(providerOrderReference);
            return updateOrder(order);
        } catch (OrderNotFoundException e) {
            throw new RuntimeException("Order with providerReference " + providerOrderReference + " not found");
        }
    }

    private void updateOrderWithPaymentResponse(Order order, PaymentResponse molliePayment) {
        // change ChPaymentsReference to ProviderReference someday
        order.setChPaymentsReference(molliePayment.getId());
        order.setStatus(OrderStatus.PENDING);
        orderService.saveAndFlush(order);
    }


    private Order updateOrder(Order order) {
        // This try is for the Mollie API internal HttpClient
        try {
            // Request a payment from Mollie
            PaymentResponse paymentResponse = mollie.payments().getPayment(order.getChPaymentsReference());

            // There are a couple of possible statuses. Enum would have been nice. We select a couple of relevant
            // statuses to translate to our own status.

            switch (paymentResponse.getStatus()) {
                case PENDING -> orderService.updateOrderStatus(order,OrderStatus.PENDING);
                case CANCELED -> orderService.updateOrderStatus(order,OrderStatus.CANCELLED);
                case EXPIRED -> orderService.updateOrderStatus(order,OrderStatus.EXPIRED);
                case PAID -> orderService.updateOrderStatus(order,OrderStatus.PAID);
                default -> orderService.updateOrderStatus(order,order.getStatus());
            }
            return orderService.saveAndFlush(order);

        } catch (MollieException e) {
            mailService.sendError("Payment provider is not responding", e);
            handleMollieError(e);
            return order;
        } catch (EventsException e){
            throw new RuntimeException(e);
        }
    }

    private void handleMollieError(MollieException mollieException) {
        // Some error occured, but connection to Mollie succeeded, which means they have something to say.
        Map molliePaymentError = mollieException.getDetails();

        // Make the compiler shut up, this is something stupid in the Mollie API Client
        Map error = (Map) molliePaymentError.get("error");
        throw new RuntimeException((String) error.get("message"));
    }
}
