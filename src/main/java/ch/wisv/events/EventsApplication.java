package ch.wisv.events;

import ch.wisv.events.repository.event.EventRepository;
import ch.wisv.events.repository.order.CustomerRepository;
import ch.wisv.events.repository.order.OrderRepository;
import ch.wisv.events.repository.product.ProductRepository;
import ch.wisv.events.repository.sales.VendorRepository;
import ch.wisv.events.utils.TestDataRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.datatables.repository.DataTablesRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

// Jsr310JpaConverters.class is necessary for correctly persisting e.g. LocalDateTime objects
@EntityScan(basePackageClasses = {EventsApplication.class, Jsr310JpaConverters.class})
@EnableJpaRepositories(repositoryFactoryBeanClass = DataTablesRepositoryFactoryBean.class)
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
     * @param eventRepository   EventRepository
     * @param productRepository ProductRepository
     * @return TestData
     */
    @Bean
    @Profile("dev")
    CommandLineRunner init(EventRepository eventRepository, ProductRepository productRepository,
                           VendorRepository vendorRepository, CustomerRepository customerRepository,
                           OrderRepository orderRepository) {
        return new TestDataRunner(eventRepository, productRepository, vendorRepository, customerRepository,
                orderRepository);
    }
}