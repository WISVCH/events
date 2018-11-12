package ch.wisv.events.domain.model.webhook;

import ch.wisv.events.domain.model.AbstractModel;
import ch.wisv.events.domain.model.user.LdapGroup;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;

/**
 * Webhook entity.
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Webhook extends AbstractModel {

    /**
     * Payload URL of the Webhook.
     */
    @NotEmpty(message = "Payload URL cannot be empty")
    @URL(message = "Payload URL is a malformed URL")
    public String payloadUrl;

    /**
     * Secret of the Webhook.
     */
    @NotEmpty(message = "Secret cannot be empty")
    public String secret;

    /**
     * Events the Webhook will respond to.
     */
    @ElementCollection
    @NotNull(message = "Events cannot be null")
    @Size(min = 1, message = "At least one event should be selected")
    public List<WebhookEvent> events = new ArrayList<>();

    /**
     * LDAP group of the Webhook.
     */
    @NotNull(message = "LDAP group cannot be null")
    public LdapGroup authLdapGroup;

    /**
     * Flag is Webhook is active.
     */
    public boolean active;
}
