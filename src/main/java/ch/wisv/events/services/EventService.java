package ch.wisv.events.services;

import ch.wisv.events.domain.model.event.Event;
import ch.wisv.events.domain.model.event.EventStatus;
import ch.wisv.events.domain.repository.EventRepository;
import ch.wisv.events.webhook.event.CreateUpdate;
import ch.wisv.events.webhook.event.Delete;
import java.time.ZonedDateTime;
import java.util.List;
import static java.util.Objects.isNull;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * EventService class.
 */
@Service
@Transactional
public class EventService extends AbstractService<Event> {

    /**
     * EventRepository.
     */
    private final EventRepository eventRepository;

    /**
     * EventRepository constructor.
     *
     * @param publisher of type ApplicationEventPublisher
     * @param eventRepository of type EventRepository
     */
    @Autowired
    public EventService(ApplicationEventPublisher publisher, EventRepository eventRepository) {
        super(publisher, eventRepository);
        this.eventRepository = eventRepository;
    }

    /**
     * Get all upcoming events.
     *
     * @return List<Event>
     */
    public List<Event> getAllUpcoming() {
        return eventRepository.findByStartingAfterOrderByStartingAsc(ZonedDateTime.now()).stream()
                .filter(x -> x.getStatus() == EventStatus.PUBLISHED)
                .collect(Collectors.toList());
    }

    /**
     * Something to do before the object has been saved.
     *
     * @param model of type AbstractModel
     */
    @Override
    void beforeSave(Event model) {

    }

    /**
     * Something to do after the object has been saved.
     *
     * @param model of type AbstractModel
     */
    @Override
    void afterSave(Event model) {
        publisher.publishEvent(new CreateUpdate(model));
    }

    /**
     * Assert if a model is deletable.
     *
     * @param model of type T
     */
    @Override
    void assertIfDeletable(Event model) {
    }

    /**
     * Something to do after the object has been deleted.
     *
     * @param model of type AbstractModel
     */
    @Override
    void afterDelete(Event model) {
        publisher.publishEvent(new Delete(model));
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
