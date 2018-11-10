package ch.wisv.events.services;

import ch.wisv.events.domain.model.webhook.Webhook;
import ch.wisv.events.domain.model.webhook.WebhookEvent;
import ch.wisv.events.domain.repository.WebhookRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * WebhookService.
 */
@Service
@Transactional
public class WebhookService extends AbstractService<Webhook> {

    /**
     * UserRepository.
     */
    private final WebhookRepository webhookRepository;

    /**
     * AbstractRepository constructor.
     *
     * @param publisher  of type ApplicationEventPublisher
     * @param repository of type WebhookRepository
     */
    @Autowired
    public WebhookService(ApplicationEventPublisher publisher, WebhookRepository repository) {
        super(publisher, repository);
        webhookRepository = repository;
    }

    /**
     * Get all by a WebhookEvent.
     *
     * @param webhookEvent of type WebhookEvent
     *
     * @return List
     */
    public List<Webhook> getAllByEvent(WebhookEvent webhookEvent) {
        return webhookRepository.findAllByEventsContainsAndActiveIsTrue(webhookEvent);
    }

    /**
     * Something to do after the object has been saved.
     *
     * @param model of type AbstractModel
     */
    @Override
    void afterSave(Webhook model) {
    }

    /**
     * Assert if a model is deletable.
     *
     * @param model of type T
     */
    @Override
    void assertIfDeletable(Webhook model) {
    }

    /**
     * Something to do after the object has been deleted.
     *
     * @param model of type AbstractModel
     */
    @Override
    void afterDelete(Webhook model) {
    }

    /**
     * Create of an AbstractModel.
     *
     * @param model of type AbstractModel
     *
     * @return AbstractModel
     */
    @Override
    protected Webhook create(Webhook model) {
        return null;
    }

    /**
     * Update of an AbstractModel.
     *
     * @param model         of type AbstractModel
     * @param existingModel of type AbstractModel
     *
     * @return AbstractModel
     */
    @Override
    protected Webhook update(Webhook model, Webhook existingModel) {
        return null;
    }
}
