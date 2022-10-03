package ch.wisv.events.webshop.controller;

import ch.wisv.events.ControllerTest;
import ch.wisv.events.EventsApplicationTest;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.event.EventStatus;
import ch.wisv.events.core.model.product.Product;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = EventsApplicationTest.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class WebshopIndexControllerTest extends ControllerTest {

    @Test
    public void testIndex() throws Exception {
        Event event = new Event(
                "title event",
                "description",
                "location",
                10,
                10,
                "",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                "short description"
        );
        Product product = new Product();
        product.setSellStart(LocalDateTime.now().minusDays(2));
        product.setSellEnd(LocalDateTime.now().plusHours(1));
        productRepository.saveAndFlush(product);

        event.setProducts(Collections.singletonList(product));
        event.setPublished(EventStatus.PUBLISHED);
        eventRepository.saveAndFlush(event);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("webshop/index"))
                .andExpect(model().attribute("orderProduct", hasProperty("products", is(new HashMap<String, Long>()))))
                .andExpect(model().attribute("events", hasSize(1)))
                .andExpect(model().attribute("events", hasItem(hasProperty("key", is(event.getKey())))))
                .andExpect(content().string(containsString("action=\"/checkout\"")));
    }

    @Test
    public void testIndexNoEvents() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("webshop/index"))
                .andExpect(model().attribute("orderProduct", hasProperty("products", is(new HashMap<String, Long>()))))
                .andExpect(model().attribute("events", hasSize(0)))
                .andExpect(content().string(containsString("action=\"/checkout\"")));
    }

    @Test
    public void testIndexSoldOutProduct() throws Exception {
        Event event = new Event(
                "Event",
                "With sold out products",
                "location",
                10,
                100,
                "",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                "Event Description"
        );
        Product product = new Product();
        product.setSellStart(LocalDateTime.now().minusDays(2));
        product.setSellEnd(LocalDateTime.now().plusHours(1));
        product.setSold(10);
        product.setMaxSold(10);
        productRepository.saveAndFlush(product);

        event.setProducts(Collections.singletonList(product));
        event.setPublished(EventStatus.PUBLISHED);
        eventRepository.saveAndFlush(event);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("webshop/index"))
                .andExpect(model().attribute("orderProduct", hasProperty("products", is(new HashMap<String, Long>()))))
                .andExpect(model().attribute("events", hasSize(1)))
                .andExpect(model().attribute("events", hasItem(hasProperty("key", is(event.getKey())))))
                .andExpect(content().string(containsString("Sold out")));
    }

    @Test
    public void testIndexSoldOutEvent() throws Exception {
        Event event = new Event(
                "Sold Out Event",
                "Sold out..",
                "location",
                13,
                13,
                "",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                "Sold Out Event Description"
        );
        Product product = new Product();
        product.setSellStart(LocalDateTime.now().minusDays(2));
        product.setSellEnd(LocalDateTime.now().plusHours(1));
        product.setSold(10);
        product.setMaxSold(100);
        productRepository.saveAndFlush(product);

        Product product2 = new Product();
        product2.setSellStart(LocalDateTime.now().minusDays(2));
        product2.setSellEnd(LocalDateTime.now().plusHours(1));
        product2.setSold(3);
        product2.setMaxSold(30);
        productRepository.saveAndFlush(product2);

        event.setProducts(Arrays.asList(product, product2));
        event.setPublished(EventStatus.PUBLISHED);
        eventRepository.saveAndFlush(event);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("webshop/index"))
                .andExpect(model().attribute("orderProduct", hasProperty("products", is(new HashMap<String, Long>()))))
                .andExpect(model().attribute("events", hasSize(1)))
                .andExpect(model().attribute("events", hasItem(hasProperty("key", is(event.getKey())))))
                .andExpect(content().string(containsString("Sold out")));
    }
}