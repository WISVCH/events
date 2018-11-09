package ch.wisv.events.services;

import ch.wisv.events.domain.exception.ModelNotFoundException;
import ch.wisv.events.domain.model.AbstractModel;
import ch.wisv.events.domain.model.event.Event;
import ch.wisv.events.domain.repository.AbstractRepository;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

/**
 * AbstractService class.
 *
 * @param <T> of type AbstractModel
 */
public abstract class AbstractService<T extends AbstractModel> {

    /**
     * AbstractRepository.
     */
    protected final AbstractRepository<T> repository;

    /**
     * AbstractRepository constructor.
     *
     * @param repository of type AbstractRepository
     */
    AbstractService(AbstractRepository<T> repository) {
        this.repository = repository;
    }

    /**
     * Get all the object of one sort.
     *
     * @return List
     */
    public List getAll() {
        return repository.findAll();
    }

    /**
     * Get an Object by its public reference.
     *
     * @param publicReference of type String
     *
     * @return AbstractModel
     */
    public T getByPublicReference(String publicReference) {
        Optional<T> abstractModel = repository.findByPublicReference(publicReference);
        if (abstractModel.isPresent()) {
            return abstractModel.get();
        }

        throw new ModelNotFoundException(Event.class, publicReference);
    }

    /**
     * Save an AbstractModel.
     *
     * @param model of type AbstractModel
     */
    public void save(T model) {
        Optional<T> optional = repository.findByPublicReference(model.getPublicReference());
        if (optional.isPresent()) {
            model.setItemId(optional.get().getItemId());
            model.setUpdatedAt(ZonedDateTime.now());

            model = this.update(model, optional.get());
        } else {
            model = this.create(model);
        }

        repository.saveAndFlush(model);
    }

    /**
     * Delete an AbstractModel.
     *
     * @param model of type T
     */
    public void delete(T model) {
        this.assertIfDeletable(model);
        repository.delete(model);
    }

    /**
     * Assert if a model is deletable.
     *
     * @param model of type T
     */
    abstract void assertIfDeletable(T model);

    /**
     * Create of an AbstractModel.
     *
     * @param model of type AbstractModel
     *
     * @return AbstractModel
     */
    protected abstract T create(T model);

    /**
     * Update of an AbstractModel.
     *
     * @param model         of type AbstractModel
     * @param existingModel of type AbstractModel
     *
     * @return AbstractModel
     */
    protected abstract T update(T model, T existingModel);

}
