package ch.wisv.events.utils.dev.data;

import ch.wisv.events.core.model.webhook.Webhook;
import ch.wisv.events.core.model.webhook.WebhookTrigger;
import ch.wisv.events.core.repository.WebhookRepository;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@Order(value = 5)
public class WebhookTestDataRunner extends TestDataRunner {

    /** WebhookRepository. */
    private final WebhookRepository webhookRepository;

    /**
     * Constructor EventTestDataRunner creates a new EventTestDataRunner instance.
     *
     * @param webhookRepository of type WebhookRepository
     */
    public WebhookTestDataRunner(WebhookRepository webhookRepository) {
        this.webhookRepository = webhookRepository;

        this.setJsonFileName("webhooks.json");
    }

    /**
     * Method loop.
     *
     * @param jsonObject of type JSONObject
     */
    @Override
    protected void loop(JSONObject jsonObject) {
        Webhook webhook = this.createWebhook(jsonObject);

        this.webhookRepository.save(webhook);
    }

    /**
     * Method createProduct ...
     *
     * @param jsonObject of type JSONObject
     *
     * @return Product
     */
    private Webhook createWebhook(JSONObject jsonObject) {
        Webhook webhook = new Webhook();
        webhook.setLdapGroup(ch.wisv.events.utils.LdapGroup.valueOf((String) jsonObject.get("ldapGroup")));
        webhook.setPayloadUrl((String) jsonObject.get("payloadUrl"));
        webhook.setActive(true);
        webhook.setSecret("secret");

        return this.addTriggers(webhook, (JSONArray) jsonObject.get("triggers"));
    }

    /**
     * Method addTriggers.
     *
     * @param webhook   of type Webhook
     * @param jsonArray of type JSONArray
     *
     * @return Webhook
     */
    private Webhook addTriggers(Webhook webhook, JSONArray jsonArray) {
        List<WebhookTrigger> triggers = new ArrayList<>();
        for (Object o : jsonArray) {
            triggers.add(WebhookTrigger.valueOf((String) o));
        }
        webhook.setWebhookTriggers(triggers);

        return webhook;
    }
}
