package ch.wisv.events.webshop.models;

import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class CHPaymentResponse {
    private String transactionId;
    private String checkoutUrl;

    public CHPaymentResponse(String transactionId, String checkoutUrl) {
        this.transactionId = transactionId;
        this.checkoutUrl = checkoutUrl;
    }
}
