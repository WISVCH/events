package ch.wisv.events.core.model.webhook;

import java.util.List;
import java.util.UUID;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Webhook {

    /**
     * ID of the product, getter only so it can not be changed.
     */
    @Id
    @GeneratedValue
    private Integer id;

    /**
     * Key of the product, getter only so it can not be changed.
     */
    private String key;

    /**
     * Field payloadUrl.
     */
    private String payloadUrl;

    /**
     * Field secret.
     */
    private String secret;

    /**
     * Field active.
     */
    private boolean active;

    /**
     * Field ldapGroup.
     */
    private ch.wisv.events.utils.LdapGroup ldapGroup;

    /**
     * Field webhookTriggers.
     */
    @ElementCollection(targetClass = WebhookTrigger.class)
    private List<WebhookTrigger> webhookTriggers;

    /**
     * Constructor Webhook creates a new Webhook instance.
     */
    public Webhook() {
        this.key = UUID.randomUUID().toString();
    }
}
