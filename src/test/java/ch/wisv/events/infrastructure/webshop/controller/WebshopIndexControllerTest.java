package ch.wisv.events.infrastructure.webshop.controller;

import ch.wisv.events.ControllerTest;
import ch.wisv.events.EventsApplicationTest;
import ch.wisv.events.domain.model.event.Event;
import ch.wisv.events.domain.model.event.EventCategory;
import static ch.wisv.events.infrastructure.webshop.controller.AbstractWebshopController.MODEL_ATTR_ERRORS;
import static ch.wisv.events.infrastructure.webshop.controller.WebshopIndexController.MODEL_ATTR_EVENT;
import static ch.wisv.events.infrastructure.webshop.controller.WebshopIndexController.MODEL_ATTR_EVENTS;
import static ch.wisv.events.infrastructure.webshop.controller.WebshopIndexController.MODEL_ATTR_ORDER_DTO;
import static ch.wisv.events.infrastructure.webshop.controller.WebshopIndexController.VIEW_WEBSHOP_INDEX;
import static ch.wisv.events.infrastructure.webshop.controller.WebshopIndexController.VIEW_WEBSHOP_SINGLE_EVENT;
import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * WebshopIndexControllerTest class.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = EventsApplicationTest.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class WebshopIndexControllerTest extends ControllerTest {

    /**
     * Test view index.
     *
     * @throws Exception on AssertionError
     */
    @Test
    public void testIndex() throws Exception {
        mockMvc.perform(get("/webshop/"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name(VIEW_WEBSHOP_INDEX))
                .andExpect(model().attributeExists(MODEL_ATTR_ORDER_DTO))
                .andExpect(model().attributeExists(MODEL_ATTR_ERRORS))
                .andExpect(model().attribute(MODEL_ATTR_EVENTS, eventRepository.findAll()));
    }

    /**
     * Test view index, filter by search.
     *
     * @throws Exception on AssertionError
     */
    @Test
    public void testIndexFilterBySearch() throws Exception {
        Event event = eventRepository.findAll().get(0);

        mockMvc.perform(
                get("/webshop/")
                        .param("search", "Career College"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name(VIEW_WEBSHOP_INDEX))
                .andExpect(model().attributeExists(MODEL_ATTR_ORDER_DTO))
                .andExpect(model().attributeExists(MODEL_ATTR_ERRORS))
                .andExpect(model().attribute(MODEL_ATTR_EVENTS, ImmutableList.of(event)));
    }

    /**
     * Test view index, filter by category.
     *
     * @throws Exception on AssertionError
     */
    @Test
    public void testIndexFilterByCategory() throws Exception {
        Event event = eventRepository.findAll().get(1);

        mockMvc.perform(
                get("/webshop/")
                        .param("categories", EventCategory.SOCIAL.toString()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name(VIEW_WEBSHOP_INDEX))
                .andExpect(model().attributeExists(MODEL_ATTR_ORDER_DTO))
                .andExpect(model().attributeExists(MODEL_ATTR_ERRORS))
                .andExpect(model().attribute(MODEL_ATTR_EVENTS, ImmutableList.of(event)));
    }

    /**
     * @throws Exception on AssertionError
     */
    @Test
    public void testViewSingleEvent() throws Exception {
        Event event = eventRepository.findAll().get(0);

        mockMvc.perform(get("/webshop/" + event.getPublicReference()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name(VIEW_WEBSHOP_SINGLE_EVENT))
                .andExpect(model().attributeExists(MODEL_ATTR_ORDER_DTO))
                .andExpect(model().attribute(MODEL_ATTR_EVENT, event));
    }
}