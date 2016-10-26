package ch.wisv.events;

import ch.wisv.events.repository.EventRepository;
import ch.wisv.events.repository.ProductRepository;
import ch.wisv.events.utils.TestDataRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

// Jsr310JpaConverters.class is necessary for correctly persisting e.g. LocalDateTime objects
@EntityScan(basePackageClasses = {EventsApplication.class, Jsr310JpaConverters.class})
@SpringBootApplication
public class EventsApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventsApplication.class, args);
    }

    @Bean
    @Profile("dev")
    CommandLineRunner init(EventRepository eventRepository, ProductRepository productRepository) {
        return new TestDataRunner(eventRepository, productRepository);
    }
}