package ch.wisv.events.webshop.service;

import ch.wisv.events.core.exception.runtime.PaymentsConnectionException;
import ch.wisv.events.core.exception.runtime.PaymentsInvalidException;
import ch.wisv.events.core.model.order.Order;
import java.io.IOException;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
public class PaymentsServiceImpl implements PaymentsService {

    /** HTTP success status code of CH Payments. */
    private static final int SUCCESS_PAYMENT_STATUS_CODE = 201;

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
     */
    public PaymentsServiceImpl() {
        this.httpClient = HttpClients.createDefault();
    }

    /**
     * Constructor with own HttpClient.
     *
     * @param httpClient of type HttpClient
     */
    public PaymentsServiceImpl(HttpClient httpClient) {
        this.httpClient = httpClient;
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
        } catch (IOException ignored) {
        }

        throw new PaymentsConnectionException();
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
        try {
            HttpPost httpPost = this.createPaymentsOrderHttpPost(order);

            HttpResponse response = this.httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                int statusCode = response.getStatusLine().getStatusCode();

                String responseString = EntityUtils.toString(entity);
                JSONObject responseObject = (JSONObject) JSONValue.parse(responseString);

                return this.getRedirectUrl(statusCode, responseObject);
            }
        } catch (IOException ignored) {
        }

        throw new PaymentsConnectionException();
    }

    /**
     * Get redirect url.
     *
     * @param statusCode     of type int
     * @param responseObject of type JSONObject
     *
     * @return String
     */
    private String getRedirectUrl(int statusCode, JSONObject responseObject) {
        if (statusCode == SUCCESS_PAYMENT_STATUS_CODE) {
            if (responseObject.containsKey("url")) {
                return (String) responseObject.get("url");
            } else {
                throw new PaymentsInvalidException("Redirect url is missing");
            }
        } else {
            throw new PaymentsInvalidException((String) responseObject.get("message"));
        }
    }

    /**
     * Create a HttpPost to create a Payments Order request.
     *
     * @param order of type Order
     *
     * @return HttpPost
     */
    public HttpPost createPaymentsOrderHttpPost(Order order) {
        HttpPost httpPost = new HttpPost(issuerUri + "/api/orders");

        JSONObject object = this.createPaymentsHttpPostBody(order);

        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("Accept", "application/json");
        httpPost.setEntity(new StringEntity(object.toJSONString(), "UTF8"));

        return httpPost;
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
        object.put("returnUrl", clientUri + "/checkout/" + order.getPublicReference() + "/payment/return");
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
}
