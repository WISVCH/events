package ch.wisv.events.domain.model.webhook;

import ch.wisv.events.domain.converter.JSONObjectConverter;
import ch.wisv.events.domain.model.AbstractModel;
import java.net.URL;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.json.simple.JSONObject;

/**
 * WebhookTask.
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class WebhookTask extends AbstractModel {

    /**
     * Payload URL of the Webhook.
     */
    public URL payloadUrl;

    /**
     * Body of the Webhook request.
     */
    @NotNull(message = "Body cannot be null")
    @Convert(converter = JSONObjectConverter.class)
    public JSONObject body;

    /**
     * WebhookTask constructor.
     *
     * @param payloadUrl of type URL
     * @param body       of type JSONObject
     */
    public WebhookTask(URL payloadUrl, JSONObject body) {
        super();
        this.payloadUrl = payloadUrl;
        this.body = body;
    }

}
