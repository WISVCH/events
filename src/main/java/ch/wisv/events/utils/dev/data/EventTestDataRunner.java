package ch.wisv.events.utils.dev.data;

import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.event.EventCategory;
import ch.wisv.events.core.model.event.EventStatus;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.repository.EventRepository;
import ch.wisv.events.core.repository.ProductRepository;
import ch.wisv.events.utils.LdapGroup;
import com.google.common.collect.ImmutableList;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.json.simple.JSONObject;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Profile({"dev", "devcontainer"})
@Order(value = 2)
public class EventTestDataRunner extends TestDataRunner {

    /** EventRepository. */
    private final EventRepository eventRepository;

    /** ProductRepository. */
    private final ProductRepository productRepository;

    /**
     * Constructor EventTestDataRunner creates a new EventTestDataRunner instance.
     *
     * @param eventRepository   of type EventRepository
     * @param productRepository of type ProductRepository
     */
    public EventTestDataRunner(EventRepository eventRepository, ProductRepository productRepository) {
        this.eventRepository = eventRepository;
        this.productRepository = productRepository;

        this.setJsonFileName("events.json");
    }

    /**
     * Method addProduct.
     *
     * @param event      of type Event
     * @param jsonObject of type JSONObject
     *
     * @return Event
     */
    private Event addProduct(Event event, JSONObject jsonObject) {
        if (jsonObject.get("productNumber") != null) {
            int productNumber = ((Long) jsonObject.get("productNumber")).intValue();
            Optional<Product> optional = this.productRepository.findById(productNumber);

            optional.ifPresent(product -> {
                product.setLinked(true);
                this.productRepository.saveAndFlush(product);

                event.addProduct(product);
            });
        }

        return event;
    }

    /**
     * Method createEvent.
     *
     * @param jsonObject of type JSONObject
     *
     * @return Event
     */
    private Event createEvent(JSONObject jsonObject) {
        int days = df.getNumberBetween(-10, 10);
        int years = df.getNumberBetween(0, 2);

        Event event = new Event(
                (String) jsonObject.get("title"),
                (String) jsonObject.get("description"),
                (String) jsonObject.get("location"),
                ((Long) jsonObject.get("target")).intValue(),
                ((Long) jsonObject.get("maxSold")).intValue(),
                (String) jsonObject.get("imageUrl"),
                LocalDateTime.now().minusYears(years).plusDays(days).truncatedTo(ChronoUnit.MINUTES),
                LocalDateTime.now().minusYears(years).plusDays(days).plusHours(1).truncatedTo(ChronoUnit.MINUTES),
                (String) jsonObject.get("shortDescription")
        );
        event.setPublished(EventStatus.PUBLISHED);
        event.setOrganizedBy(LdapGroup.FLITCIE);
        event.setCategories(ImmutableList.of(EventCategory.CAREER));

        return event;
    }

    /**
     * Method loop.
     *
     * @param jsonObject of type JSONObject
     */
    @Override
    protected void loop(JSONObject jsonObject) {
        Event event = this.createEvent(jsonObject);
        event = this.addProduct(event, jsonObject);

        this.eventRepository.save(event);
    }
}
