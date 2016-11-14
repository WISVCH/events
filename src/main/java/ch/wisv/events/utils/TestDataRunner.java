package ch.wisv.events.utils;

import ch.wisv.events.data.model.event.Event;
import ch.wisv.events.data.model.product.Product;
import ch.wisv.events.repository.event.EventRepository;
import ch.wisv.events.repository.product.ProductRepository;
import org.springframework.boot.CommandLineRunner;

import java.time.LocalDateTime;
import java.time.Month;

/**
 * TestDataRunner.
 * <p>
 * Adds some data into the Repositories.
 */
public class TestDataRunner implements CommandLineRunner {

    private final EventRepository eventRepository;
    private final ProductRepository productRepository;

    public TestDataRunner(EventRepository eventRepository, ProductRepository productRepository) {
        this.eventRepository = eventRepository;
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Product product;
        for (int i = 1; i < 10; i++) {
            product = new Product();
            product.setTitle("Product " + i);
            product.setCost(10.0f);
            product.setDescription(
                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque vitae lectus est. Nam ultrices sapien felis, hendrerit pulvinar tortor lobortis a. Nunc mauris est, fermentum in neque sed, consectetur aliquam justo. Etiam nec feugiat mi. Aliquam sed.");
            product.setSellStart(LocalDateTime.of(2020, Month.DECEMBER, Math.floorMod(i, 11) + 1, 12, 45));
            product.setSellEnd(LocalDateTime.of(2020, Month.DECEMBER, Math.floorMod(i, 11) + 1, 13, 30));
            product.setMaxSold(100);
            productRepository.save(product);
        }

        Event event;
        for (int i = 1; i < 3; i++) {
            event = new Event("Event " + i,
                    "Phasellus eget mauris fringilla, tincidunt enim eget, luctus massa. Suspendisse ultricies in neque " +
                            "at cursus. Duis viverra nunc in volutpat pellentesque. Ut eu finibus urna, a posuere nulla. Fusce vel nulla nibh. Curabitur gravida ante sed tellus posuere.",
                    "Lecture hall A",
                    80,
                    100,
                    "http://placehold.it/300x300", LocalDateTime.of(2020, Month.DECEMBER, i, 12, 45),
                    LocalDateTime.of(2020, Month.DECEMBER, i, 13, 30)
            );

            event.addProduct(productRepository.findById((long) i));
            eventRepository.save(event);
        }
    }
}
