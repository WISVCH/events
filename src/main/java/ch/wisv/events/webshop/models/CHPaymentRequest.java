package ch.wisv.events.webshop.models;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Data
@NoArgsConstructor
public class CHPaymentRequest {
    private BigDecimal amount;
    private String description;
    private String consumerName;
    private String billingEmail;
    private String redirectURL;
    private String webhookURL;
    private String fallbackURL;
    private Map<String, Object> metadata;

    public CHPaymentRequest(BigDecimal amount, String description, String consumerName, String billingEmail, String redirectURL, String webhookURL, Map<String, Object> metadata, String fallbackRedirectURL) {
        this.amount = amount;
        this.description = description;
        this.consumerName = consumerName;
        this.billingEmail = billingEmail;
        this.redirectURL = redirectURL;
        this.webhookURL = webhookURL;
        this.redirectURL = fallbackRedirectURL;
        this.metadata = metadata;
    }


}
