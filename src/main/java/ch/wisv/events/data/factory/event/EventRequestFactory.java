package ch.wisv.events.data.factory.event;

import ch.wisv.events.data.model.event.Event;
import ch.wisv.events.data.request.event.EventRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * EventRequestFactory
 */
public class EventRequestFactory {

    /**
     * DateTimeFormatter.
     */
    private static DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    /**
     * Create new EvenRequest from Event.
     *
     * @param event Event
     * @return EventRequest
     */
    public static EventRequest create(Event event) {
        return new EventRequest(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getLocation(),
                event.getTarget(),
                event.getLimit(),
                event.getStart().toString(),
                event.getEnd().toString(),
                event.getImageURL(),
                event.getKey(),
                event.getOptions()
        );
    }

    /**
     * Create new Event from EventRequest
     *
     * @param request EventRequest
     * @return Event
     */
    public static Event create(EventRequest request) {
        return new Event(
                request.getTitle(),
                request.getDescription(),
                request.getLocation(),
                request.getTarget(),
                request.getLimit(),
                request.getImage(), LocalDateTime.parse(request.getEventStart(), format),
                LocalDateTime.parse(request.getEventEnd(), format)
        );
    }

    /**
     * Update Event by EventRequest
     *
     * @param event   Event
     * @param request EventRequest
     * @return Event
     */
    public static Event update(Event event, EventRequest request) {
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setLocation(request.getLocation());
        event.setTarget(request.getTarget());
        event.setLimit(request.getLimit());
        event.setStart(LocalDateTime.parse(request.getEventStart(), format));
        event.setEnd(LocalDateTime.parse(request.getEventEnd(), format));
        event.setImageURL(request.getImage());

        return event;
    }
}
