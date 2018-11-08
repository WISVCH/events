package ch.wisv.events.services;

import ch.wisv.events.domain.model.event.Event;
import ch.wisv.events.domain.repository.EventRepository;
import static java.util.Objects.isNull;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * EventService class.
 */
@Service
@Transactional
public class EventService extends AbstractService<Event> {

    /**
     * EventRepository constructor.
     *
     * @param eventRepository of type EventRepository
     */
    @Autowired
    public EventService(EventRepository eventRepository) {
        super(eventRepository);
    }

    /**
     * Assert if a model is detetable.
     *
     * @param model of type T
     */
    @Override
    void assertIfDeletable(Event model) {
    }

    /**
     * Create of an AbstractModel.
     *
     * @param model of type AbstractModel
     *
     * @return AbstractModel
     */
    @Override
    protected Event create(Event model) {
        return model;
    }

    /**
     * Update of an Event.
     *
     * @param model         of type Event
     * @param existingModel of type Event
     *
     * @return Event
     */
    @Override
    protected Event update(Event model, Event existingModel) {
        if (isNull(model.getImage())) {
            model.setImage(existingModel.getImage());
        }

        return model;
    }
}
