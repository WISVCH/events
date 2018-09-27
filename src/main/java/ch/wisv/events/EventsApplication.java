package ch.wisv.events;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;

/**
 * EventsApplication class.
 * Jsr310JpaConverters.class is necessary for correctly persisting e.g. LocalDateTime objects.
 */
@EntityScan(basePackageClasses = {EventsApplication.class, Jsr310JpaConverters.class})
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

    /**
     * Enables Time formating in thymeleaf.
     *
     * @return Java8TimeDialect
     */
    @Bean
    public Java8TimeDialect java8TimeDialect() {
        return new Java8TimeDialect();
    }

}
