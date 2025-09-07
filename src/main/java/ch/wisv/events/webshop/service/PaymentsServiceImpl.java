package ch.wisv.events.webshop.service;

import be.woutschoovaerts.mollie.Client;
import be.woutschoovaerts.mollie.ClientBuilder;
import be.woutschoovaerts.mollie.data.common.Amount;
import be.woutschoovaerts.mollie.data.payment.PaymentRequest;
import be.woutschoovaerts.mollie.data.payment.PaymentResponse;
import be.woutschoovaerts.mollie.exception.MollieException;
import ch.wisv.events.core.exception.normal.EventsException;
import ch.wisv.events.core.exception.normal.OrderNotFoundException;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderProduct;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.model.order.PaymentMethod;
import ch.wisv.events.core.service.mail.MailService;
import ch.wisv.events.core.service.order.OrderService;

import ch.wisv.events.webshop.models.CHPaymentRequest;
import ch.wisv.events.webshop.models.CHPaymentResponse;
import jakarta.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;


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
     * Payments client url for mollie.
     */
    @Value("${mollie.clientUri}")
    @NotNull
    private String clientUriMollie;

    /**
     * Payments client url for CHPay.
     */
    @Value("${wisvch.chpay.clientUri}")
    @NotNull
    private String clientUriCHPay;

    @Value("${wisvch.chpay.api-key}")
    private String chpayApiKey;

    @Value("${wisvch.chpay.uri}")
    @NotNull
    private String CHPayUri;

    private OAuth2AuthorizedClientService authorizedClientService;

    /**
     * Default constructor.
     *
     * @param orderService of type OrderService
     * @param apiKey       of type String
     * @param mailService  of type MailService
     */
    @Autowired
    public PaymentsServiceImpl(OrderService orderService, @Value("${mollie.apikey:null}") String apiKey, MailService mailService, OAuth2AuthorizedClientService authorizedClientService) {
        this.orderService = orderService;
        this.mailService = mailService;
        this.mollie = new ClientBuilder().withApiKey(apiKey).build();
        this.authorizedClientService = authorizedClientService;
    }

    public PaymentsServiceImpl(OrderService orderService, Client mollie, MailService mailService) {
        this.orderService = orderService;
        this.mailService = mailService;
        this.mollie = mollie;
    }

    @Override
    public String getCHpayUrl(Order order) {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> metadata = new HashMap<>();

        metadata.put("orderId", order.getPublicReference());

        String productString = "";
        // Add the names, quantities and prices of the products to the metadata.
        for (OrderProduct orderProduct : order.getOrderProducts()) {
            // Make string of product name, quantity, price and vat.
            productString +=  orderProduct.getProduct().getTitle()
                    + " (" + orderProduct.getAmount() + "x)"
                    + " - €" + orderProduct.getPrice()
                    + "(incl. " + orderProduct.getVat() + "VAT ; ";
        }

        metadata.put("products", productString);

        String chPayApi = CHPayUri;
        String returnUrl = clientUriCHPay + "/return/" + order.getPublicReference();
        String webhookUrl = clientUriCHPay + "/api/v1/orders/status";
        String fallbackUrl = clientUriCHPay + "/return/" + order.getPublicReference() + "/fallback";

        CHPaymentRequest request = new CHPaymentRequest();
        request.setAmount(BigDecimal.valueOf(order.getAmount()));
        request.setDescription("CH Events Payment for " + order.getOrderProducts().stream().map(op -> op.getProduct().getTitle()).reduce((a,b) -> a + ", " + b).orElse(""));
        request.setConsumerName(order.getOwner().getName());
        request.setConsumerEmail(order.getOwner().getEmail());
        request.setRedirectURL(returnUrl);
        request.setFallbackURL(fallbackUrl);
        request.setWebhookURL(webhookUrl);
        request.setMetadata(metadata);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("X-API-KEY", chpayApiKey);
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CHPaymentRequest> requestEntity = new HttpEntity<>(request, httpHeaders);

        ResponseEntity<CHPaymentResponse> response = restTemplate.postForEntity(chPayApi, requestEntity, CHPaymentResponse.class);

        if(response.getStatusCode().is2xxSuccessful()){
            CHPaymentResponse body = response.getBody();
            if (body != null) {
                order.setChPaymentsReference(body.getTransactionId());
            }
            order.setStatus(OrderStatus.PENDING);
            orderService.saveAndFlush(order);

            assert body != null;
            return body.getCheckoutUrl();
        } else {
            throw new RuntimeException("Something went wrong with the CHPay API");
        }
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
            // Make string of product name, quantity, price and vat.
            productString +=  orderProduct.getProduct().getTitle()
                            + " (" + orderProduct.getAmount() + "x)"
                            + " - €" + orderProduct.getPrice()
                            + "(incl. " + orderProduct.getVat() + "VAT ; ";
        }

        metadata.put("products", productString);

        String returnUrl = clientUriMollie + "/return/" + order.getPublicReference();
        String webhookUrl = clientUriMollie + "/api/v1/orders/status";

        double value = order.getAmount();

        value = order.getPaymentMethod().calculateCostIncludingTransaction(value);
        Amount paymentAmount = Amount.builder().value(BigDecimal.valueOf(value).setScale(2, RoundingMode.CEILING)).currency("EUR").build();

        return PaymentRequest.builder()
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
     * updates the order status with the given provider reference.
     *
     * @param providerOrderReference reference of the order used by mollie
     */
    @Override
    public void updateStatusByProviderReference(String providerOrderReference) {
        try {
            Order order = orderService.getByChPaymentsReference(providerOrderReference);
            PaymentMethod method = order.getPaymentMethod();

            switch (method) {
                case CHPAY -> updateCHPayOrder(order);
                case MOLLIE -> updateOrder(order);
                default -> throw new RuntimeException("Order with providerReference " + providerOrderReference + " has an unknown payment method");
            }
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

    @Override
    public void updateCHOrderByProviderReference(String CHPayReference) {
        try{
            Order order = orderService.getByChPaymentsReference(CHPayReference);
            updateCHPayOrder(order);
        } catch (OrderNotFoundException e) {
            throw new RuntimeException("Order with providerReference " + CHPayReference + " not found");
        }
    }

    public enum TransactionStatus {
        SUCCESSFUL,
        FAILED,
        PENDING
    }

    private Order updateCHPayOrder(Order order) {
        //Totally not ripping off the code above for this one, could not even imagine that.
        try {
            String chPayApi = "http://localhost:8080/api/events";

            UUID paymentID = UUID.fromString(order.getChPaymentsReference());

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(chPayApi + "/status").queryParam("PaymentId", paymentID);

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("X-API-KEY", chpayApiKey);
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Void> entity = new HttpEntity<>(httpHeaders);

            ResponseEntity<TransactionStatus> requestEntity = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.GET,
                    entity,
                    TransactionStatus.class
            );

            TransactionStatus response = requestEntity.getBody();

            // There are a couple of possible statuses. Enum would have been nice. We select a couple of relevant
            // statuses to translate to our own status.

            switch (response) {
                case PENDING -> orderService.updateOrderStatus(order,OrderStatus.PENDING);
                case FAILED -> orderService.updateOrderStatus(order,OrderStatus.CANCELLED);
                case SUCCESSFUL -> orderService.updateOrderStatus(order,OrderStatus.PAID);
                default -> orderService.updateOrderStatus(order,order.getStatus());
            }
            return orderService.saveAndFlush(order);

        } catch (Exception e) {
            e.printStackTrace();
            return order;
        }
    }
}
