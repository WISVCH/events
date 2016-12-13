package ch.wisv.events;

import ch.wisv.events.core.repository.*;
import ch.wisv.events.core.service.order.OrderService;
import ch.wisv.events.core.service.product.SoldProductService;
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

    /**
     * Application runner default
     *
     * @param args args
     */
    public static void main(String[] args) {
        SpringApplication.run(EventsApplication.class, args);
    }

    /**
     * Profile "dev" for adding data to the Repositories for development purpose.
     *
     * @param eventRepository       EventRepository
     * @param productRepository     ProductRepository
     * @param soldProductService
     * @param soldProductRepository
     * @return TestData
     */
    @Bean
    @Profile("dev")
    CommandLineRunner init(EventRepository eventRepository, ProductRepository productRepository,
                           VendorRepository vendorRepository, CustomerRepository customerRepository,
                           OrderRepository orderRepository, OrderService orderService,
                           SoldProductService soldProductService, SoldProductRepository soldProductRepository) {
        return new TestDataRunner(eventRepository, productRepository, vendorRepository, customerRepository,
                orderRepository, orderService, soldProductService, soldProductRepository);
    }
}