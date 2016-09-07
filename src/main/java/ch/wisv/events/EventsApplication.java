package ch.wisv.events;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

// Jsr310JpaConverters.class is necessary for correctly persisting e.g. LocalDateTime objects
@EntityScan(basePackageClasses = {EventsApplication.class, Jsr310JpaConverters.class})
@SpringBootApplication
public class EventsApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventsApplication.class, args);
    }
}
