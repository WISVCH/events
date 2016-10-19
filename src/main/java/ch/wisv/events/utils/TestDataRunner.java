package ch.wisv.events.utils;

import ch.wisv.events.event.model.Event;
import ch.wisv.events.event.model.Product;
import ch.wisv.events.event.repository.EventRepository;
import ch.wisv.events.event.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;

import java.time.LocalDateTime;
import java.time.Month;

/**
 * Created by sven on 12/10/2016.
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
        for (int i = 1; i < 100; i++) {
            product = new Product();
            product.setTitle("Product " + i);
            product.setCost(10.0f);
            product.setDescription(
                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque vitae lectus est. Nam ultrices sapien felis, hendrerit pulvinar tortor lobortis a. Nunc mauris est, fermentum in neque sed, consectetur aliquam justo. Etiam nec feugiat mi. Aliquam sed.");
            product.setStart(LocalDateTime.of(2016, Month.OCTOBER, Math.floorMod(i, 11) + 1, 12, 45));
            product.setEnd(LocalDateTime.of(2016, Month.OCTOBER, Math.floorMod(i, 11) + 1, 13, 30));
            product.setMaxSold(100);
            productRepository.save(product);
        }

        Event event;
        for (int i = 1; i < 20; i++) {
            event = new Event("Event " + i,
                    "Phasellus eget mauris fringilla, tincidunt enim eget, luctus massa. Suspendisse ultricies in neque " +
                            "at cursus. Duis viverra nunc in volutpat pellentesque. Ut eu finibus urna, a posuere nulla. Fusce vel nulla nibh. Curabitur gravida ante sed tellus posuere.",
                    "Lecture hall A",
                    80,
                    100,
                    LocalDateTime.of(2016, Month.OCTOBER, i, 13, 30),
                    "http://placehold.it/300x300", LocalDateTime.of(2016, Month.OCTOBER, i, 12, 45));

            event.addProduct(productRepository.findById((long) i));
            eventRepository.save(event);
        }
    }
}
