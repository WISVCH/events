package ch.wisv.events.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Lob;
import java.time.LocalDateTime;

/**
 * Created by sven on 20/10/2016.
 */
@AllArgsConstructor
public class EventsDefaultResponse {

    @Getter
    @Setter
    private String key;

    @Getter
    @Setter
    private String title;

    @Lob
    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private String location;

    @Getter
    @Setter
    private String imageURL;

    @Getter
    @Setter
    private LocalDateTime startEvent;

    @Getter
    @Setter
    private LocalDateTime endEvent;

}
