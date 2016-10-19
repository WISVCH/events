package ch.wisv.events.dashboard.request;

import ch.wisv.events.event.model.Event;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by sven on 17/10/2016.
 */
public class EventRequestFactory {
    public static DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

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
                event.getKey()
        );
    }

    public static Event create(EventRequest eventRequest) {
        return new Event(
                eventRequest.getTitle(),
                eventRequest.getDescription(),
                eventRequest.getLocation(),
                eventRequest.getTarget(),
                eventRequest.getLimit(),
                LocalDateTime.parse(eventRequest.getEventEnd(), format),
                eventRequest.getImage(),
                LocalDateTime.parse(eventRequest.getEventStart(), format));
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
