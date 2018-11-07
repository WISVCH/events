package ch.wisv.events;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

@Configuration
@ComponentScan
@EnableAutoConfiguration(exclude = {FlywayAutoConfiguration.class})
@ActiveProfiles("test")
public class EventsApplicationTest {

    /**
     * Method main ...
     *
     * @param args of type String[]
     */
    public static void main(String[] args) {
        SpringApplication.run(EventsApplicationTest.class, args);
    }

}
