package ch.wisv.events.core.model.webhook;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;
import org.json.simple.JSONObject;

@Entity
@Data
public class WebhookTask {

    /**
     * ID of the product, getter only so it can not be changed.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "webhook_task_seq")
    private Integer id;

    /**
     * Field trigger.
     */
    private WebhookTrigger trigger;

    /**
     * Field webhook.
     */
    @ManyToOne
    private Webhook webhook;

    /**
     * Field object.
     */
    private JSONObject object;

    /**
     * Field createdAt.
     */
    private LocalDateTime createdAt;

    /**
     * Field webhookTaskStatus.
     */
    private WebhookTaskStatus webhookTaskStatus;

    /**
     * Field webhookError.
     */
    @Column(columnDefinition = "TEXT")
    private String webhookError;

    /**
     * Constructor WebhookTask creates a new WebhookTask instance.
     */
    public WebhookTask() {
        this.createdAt = LocalDateTime.now();
        this.webhookTaskStatus = WebhookTaskStatus.PENDING;
    }

    /**
     * Method toString ...
     *
     * @return String
     */
    @Override
    public String toString() {
        return "WebhookTask{" + "trigger=" + trigger + ", webhook=" + webhook.getPayloadUrl() + ", object=" + object.toString() + '}';
    }
}
