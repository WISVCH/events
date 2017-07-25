package ch.wisv.events.core.model.webhook;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.List;
import java.util.UUID;

/**
 * Copyright (c) 2017 Can i spend it
 *
 * @author svenp
 * @since 20170725
 */
@Entity
@Data
@AllArgsConstructor
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
     * Field secret
     */
    private String secret;

    /**
     * Field active
     */
    private boolean active;

    /**
     * Field webhookTriggers
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
