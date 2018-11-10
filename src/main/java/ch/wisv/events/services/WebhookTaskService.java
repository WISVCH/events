package ch.wisv.events.services;

import ch.wisv.events.domain.model.webhook.WebhookTask;
import ch.wisv.events.domain.repository.WebhookTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 */
@Service
@Transactional
public class WebhookTaskService extends AbstractService<WebhookTask> {

    /**
     * AbstractRepository constructor.
     *
     * @param publisher  of type ApplicationEventPublisher
     * @param repository of type AbstractRepository
     */
    @Autowired
    public WebhookTaskService(ApplicationEventPublisher publisher, WebhookTaskRepository repository) {
        super(publisher, repository);
    }

    /**
     * Something to do after the object has been saved.
     *
     * @param model of type AbstractModel
     */
    @Override
    void afterSave(WebhookTask model) {
    }

    /**
     * Assert if a model is deletable.
     *
     * @param model of type T
     */
    @Override
    void assertIfDeletable(WebhookTask model) {
    }

    /**
     * Something to do after the object has been deleted.
     *
     * @param model of type AbstractModel
     */
    @Override
    void afterDelete(WebhookTask model) {
    }

    /**
     * Create of an AbstractModel.
     *
     * @param model of type AbstractModel
     *
     * @return AbstractModel
     */
    @Override
    protected WebhookTask create(WebhookTask model) {
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
    protected WebhookTask update(WebhookTask model, WebhookTask existingModel) {
        return null;
    }
}
