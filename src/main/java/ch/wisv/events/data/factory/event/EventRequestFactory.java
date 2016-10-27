package ch.wisv.events.data.factory.event;

import ch.wisv.events.data.model.event.Event;
import ch.wisv.events.data.request.event.EventRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by sven on 17/10/2016.
 */
public class EventRequestFactory {

    private static DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");


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
