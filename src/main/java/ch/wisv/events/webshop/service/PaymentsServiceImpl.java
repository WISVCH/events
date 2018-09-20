package ch.wisv.events.webshop.service;

import ch.wisv.events.core.exception.normal.OrderInvalidException;
import ch.wisv.events.core.exception.normal.OrderNotFoundException;
import ch.wisv.events.core.exception.normal.PaymentsStatusUnknown;
import ch.wisv.events.core.exception.runtime.PaymentsConnectionException;
import ch.wisv.events.core.exception.runtime.PaymentsInvalidException;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.service.mail.MailService;
import ch.wisv.events.core.service.order.OrderService;
import javax.validation.constraints.NotNull;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * PaymentsService implementation.
 */
@Validated
@Service
public class PaymentsServiceImpl implements PaymentsService {

    /** HTTP success status code of CH Payments. */
    private static final int SUCCESS_PAYMENT_STATUS_CODE = 201;

    /** OrderService. */
    private final OrderService orderService;

    /** MailService. */
    private final MailService mailService;

    /** Payments issuer url. */
    @Value("${wisvch.payments.issuerUri}")
    @NotNull
    private String issuerUri;

    /** Payments client url. */
    @Value("${wisvch.payments.clientUri}")
    @NotNull
    private String clientUri;

    /** HTTP client. */
    private HttpClient httpClient;

    /**
     * Default constructor.
     *
     * @param orderService of type OrderService
     * @param mailService  of type MailService
     */
    @Autowired
    public PaymentsServiceImpl(OrderService orderService, MailService mailService) {
        this.orderService = orderService;
        this.mailService = mailService;
        this.httpClient = HttpClients.createDefault();
    }

    /**
     * Constructor with HttpClient.
     *
     * @param orderService of type OrderService
     * @param httpClient   of type HttpClient
     * @param mailService  of type MailService
     */
    public PaymentsServiceImpl(OrderService orderService, HttpClient httpClient, MailService mailService) {
        this.orderService = orderService;
        this.httpClient = httpClient;
        this.mailService = mailService;
    }

    /**
     * Get the Order status of Payments.
     *
     * @param paymentsReference of type String
     *
     * @return String
     */
    @Override
    public String getPaymentsOrderStatus(String paymentsReference) {
        try {
            HttpGet httpGet = new HttpGet(issuerUri + "/api/orders/" + paymentsReference);
            httpGet.setHeader("Accept", "application/json");

            HttpResponse response = this.httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                JSONObject responseObject = (JSONObject) JSONValue.parse(EntityUtils.toString(entity));

                if (responseObject.containsKey("status")) {
                    return (String) responseObject.get("status");
                }
            }
        } catch (Exception e) {
            mailService.sendError("Payment provider is not responding", e);
        }

        throw new PaymentsConnectionException("Payment provider is not responding");
    }

    /**
     * Get a Mollie Url via Payments.
     *
     * @param order of type Order
     *
     * @return String
     */
    @Override
    public String getPaymentsMollieUrl(Order order) {
        HttpPost httpPost = this.createPaymentsOrderHttpPost(order);

        try {
            HttpResponse response = this.httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                int statusCode = response.getStatusLine().getStatusCode();
                String responseString = EntityUtils.toString(entity);
                JSONObject responseObject = (JSONObject) JSONValue.parse(responseString);

                if (statusCode == SUCCESS_PAYMENT_STATUS_CODE) {
                    this.setChPaymentsReference(order, responseObject);
                    return this.getRedirectUrl(responseObject);
                } else {
                    throw new PaymentsInvalidException((String) responseObject.get("message"));
                }
            }
        } catch (Exception e) {
            mailService.sendError("Can't fetch mollie url", e);
        }

        throw new PaymentsConnectionException("Payment provider is not responding");
    }

    /**
     * Map a CH Payments status to a OrderStatus.
     *
     * @param status of type String
     *
     * @return OrderStatus
     */
    @Override
    public OrderStatus mapStatusToOrderStatus(String status) throws PaymentsStatusUnknown {
        switch (status) {
            case "WAITING":
                return OrderStatus.PENDING;
            case "PAID":
                return OrderStatus.PAID;
            case "CANCELLED":
                return OrderStatus.CANCELLED;
            case "EXPIRED":
                return OrderStatus.EXPIRED;
            default:
                throw new PaymentsStatusUnknown(status);
        }
    }

    /**
     * Create payments HTTP Post Body.
     *
     * @param order of type Order
     *
     * @return JSONObject
     */
    private JSONObject createPaymentsHttpPostBody(Order order) {
        JSONObject object = new JSONObject();
        object.put("name", order.getOwner().getName());
        object.put("email", order.getOwner().getEmail());
        object.put("method", order.getPaymentMethod().toString());
        object.put("returnUrl", clientUri + "/return/" + order.getPublicReference());
        object.put("webhookUrl", clientUri + "/api/v1/orders/status");
        object.put("mailConfirmation", false);

        JSONArray jsonArray = new JSONArray();
        order.getOrderProducts().forEach(orderProduct -> {
            for (int i = 0; i < orderProduct.getAmount(); i++) {
                jsonArray.add(orderProduct.getProduct().getKey());
            }
        });
        object.put("productKeys", jsonArray);

        return object;
    }

    /**
     * Create a HttpPost to create a Payments Order request.
     *
     * @param order of type Order
     *
     * @return HttpPost
     */
    private HttpPost createPaymentsOrderHttpPost(Order order) {
        HttpPost httpPost = new HttpPost(issuerUri + "/api/orders");

        JSONObject object = this.createPaymentsHttpPostBody(order);

        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("Accept", "application/json");
        httpPost.setEntity(new StringEntity(object.toJSONString(), "UTF8"));

        return httpPost;
    }

    /**
     * Get redirect url.
     *
     * @param responseObject of type JSONObject
     *
     * @return String
     */
    private String getRedirectUrl(JSONObject responseObject) {
        if (responseObject.containsKey("url")) {
            return (String) responseObject.get("url");
        } else {
            throw new PaymentsInvalidException("Redirect url is missing");
        }
    }

    /**
     * Set the CH Payments public reference.
     *
     * @param order          of type Order
     * @param responseObject of type JSONObject
     */
    private void setChPaymentsReference(Order order, JSONObject responseObject) {
        try {
            if (responseObject.containsKey("publicReference")) {
                order.setChPaymentsReference((String) responseObject.get("publicReference"));

                orderService.update(order);
            } else {
                throw new PaymentsInvalidException("Missing public reference");
            }
        } catch (OrderNotFoundException | OrderInvalidException e) {
            throw new PaymentsInvalidException(e.getMessage());
        }
    }
}
