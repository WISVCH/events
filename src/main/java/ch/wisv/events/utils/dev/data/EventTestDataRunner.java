package ch.wisv.events.utils.dev.data;

import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.event.EventStatus;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.repository.EventRepository;
import ch.wisv.events.core.repository.ProductRepository;
import ch.wisv.events.utils.LDAPGroup;
import org.json.simple.JSONObject;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * Copyright (c) 2016  W.I.S.V. 'Christiaan Huygens'
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
@Component
@Profile("dev")
@Order(value = 2)
public class EventTestDataRunner extends TestDataRunner {

    /**
     * Field eventRepository
     */
    private final EventRepository eventRepository;

    /**
     * Field productRepository
     */
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
     * Method loop
     *
     * @param jsonObject of type JSONObject
     */
    @Override
    protected void loop(JSONObject jsonObject) {
        Event event = this.createEvent(jsonObject);
        event = this.addProduct(event, jsonObject);

        this.eventRepository.save(event);
    }

    /**
     * Method createEvent.
     *
     * @param jsonObject of type JSONObject
     * @return Event
     */
    private Event createEvent(JSONObject jsonObject) {
        int days = df.getNumberBetween(-10, 10);

        Event event = new Event(
                (String) jsonObject.get("title"),
                (String) jsonObject.get("description"),
                (String) jsonObject.get("location"),
                ((Long) jsonObject.get("target")).intValue(),
                ((Long) jsonObject.get("maxSold")).intValue(),
                (String) jsonObject.get("imageUrl"),
                LocalDateTime.now().plusDays(days).truncatedTo(ChronoUnit.MINUTES),
                LocalDateTime.now().plusDays(days).plusHours(1).truncatedTo(ChronoUnit.MINUTES),
                (String) jsonObject.get("shortDescription")
        );
        event.setPublished(EventStatus.PUBLISHED);
        event.setOrganizedBy(LDAPGroup.AKCIE);

        return event;
    }

    /**
     * Method addProduct.
     *
     * @param event      of type Event
     * @param jsonObject of type JSONObject
     * @return Event
     */
    private Event addProduct(Event event, JSONObject jsonObject) {
        if (jsonObject.get("productNumber") != null) {
            int productNumber = ((Long) jsonObject.get("productNumber")).intValue();
            Optional<Product> optional = this.productRepository.findById(productNumber);

            if (optional.isPresent()) {
                Product product = optional.get();

                event.addProduct(product);
            }
        }

        return event;
    }
}
