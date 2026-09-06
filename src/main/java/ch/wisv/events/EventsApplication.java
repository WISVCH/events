package ch.wisv.events;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * EventsApplication class.
 */
@EntityScan(basePackageClasses = EventsApplication.class)
@SpringBootApplication
@EnableScheduling
public class EventsApplication {

    /**
     * Application runner default.
     *
     * @param args args
     */
    public static void main(String[] args) {
        SpringApplication.run(EventsApplication.class, args);
    }
}
