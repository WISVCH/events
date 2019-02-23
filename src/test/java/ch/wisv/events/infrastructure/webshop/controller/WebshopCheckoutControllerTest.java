package ch.wisv.events.infrastructure.webshop.controller;

import ch.wisv.events.ControllerTest;
import ch.wisv.events.EventsApplicationTest;
import ch.wisv.events.domain.model.event.Event;
import ch.wisv.events.domain.model.product.Product;
import ch.wisv.events.domain.model.product.ProductOption;
import ch.wisv.events.infrastructure.webshop.dto.OrderProductDto;
import com.google.common.collect.ImmutableMap;
import java.time.ZonedDateTime;
import static junit.framework.TestCase.fail;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * WebshopCheckoutControllerTest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = EventsApplicationTest.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class WebshopCheckoutControllerTest extends ControllerTest {

    /** Product. */
    private Product product;

    /** ProductOption. */
    private ProductOption productOption;

    /**
     * Before all tests.
     */
    @Before
    public void before() {
        this.product = productRepository.findAll().get(0);

        Event event = product.getEvent();
        event.setStarting(ZonedDateTime.now().plusMonths(1));
        event.setEnding(ZonedDateTime.now().plusMonths(1).plusHours(1));
        eventRepository.saveAndFlush(event);

        this.productOption = this.product.getProductOptions().get(0);
    }

    /**
     * Check a Product without mandatory ProductOption, the checkout contains a ProductOption.
     *
     * @throws Exception on AssertionError
     */
    @Test
    public void testCheckoutProductWithoutMandatoryProductOptionContainsOption() throws Exception {
        product.setMandatoryProductOption(false);
        productRepository.saveAndFlush(product);

        mockMvc.perform(
                post("/webshop/checkout")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("products[0].productKey", product.getPublicReference())
                        .param("products[0].productOptionKey", productOption.getPublicReference())
                        .param("products[0].amount", "1")
                        .sessionAttr("orderProduct", new OrderProductDto()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/webshop/order/*"));
    }

    /**
     * Check a Product without mandatory ProductOption, the checkout does not contain a ProductOption.
     *
     * @throws Exception on AssertionError
     */
    @Test
    public void testCheckoutProductWithoutMandatoryProductOptionContainsNoOption() throws Exception {
        product.setMandatoryProductOption(false);
        productRepository.saveAndFlush(product);

        mockMvc.perform(
                post("/webshop/checkout")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("products[0].productKey", product.getPublicReference())
                        .param("products[0].productOptionKey", "")
                        .param("products[0].amount", "1")
                        .sessionAttr("orderProduct", new OrderProductDto()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/webshop/order/*"));
    }

    /**
     * Check a Product with mandatory ProductOption, the checkout contains a ProductOption.
     *
     * @throws Exception on AssertionError
     */
    @Test
    public void testCheckoutProductWithMandatoryProductOptionContainsOption() throws Exception {
        product.setMandatoryProductOption(true);
        productRepository.saveAndFlush(product);

        mockMvc.perform(
                post("/webshop/checkout")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("products[0].productKey", product.getPublicReference())
                        .param("products[0].productOptionKey", productOption.getPublicReference())
                        .param("products[0].amount", "1")
                        .sessionAttr("orderProduct", new OrderProductDto()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/webshop/order/*"));
    }

    /**
     * Check a Product with mandatory ProductOption, the checkout does not contain a ProductOption.
     *
     * @throws Exception on AssertionError
     */
    @Test
    public void testCheckoutProductWithMandatoryProductOptionContainsNoOption() throws Exception {
        product.setMandatoryProductOption(true);
        productRepository.saveAndFlush(product);

        mockMvc.perform(
                post("/webshop/checkout")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("products[0].productKey", product.getPublicReference())
                        .param("products[0].productOptionKey", "")
                        .param("products[0].amount", "1")
                        .sessionAttr("orderProduct", new OrderProductDto()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/webshop/"))
                .andExpect(flash().attribute("errors", ImmutableMap.of("invalid", "One or more invalid products in this order")));
    }

    /**
     * Test when shopping basket is empty.
     *
     * @throws Exception on AssertionError
     */
    @Test
    public void testCheckoutEmptyShoppingBasket() throws Exception {
        mockMvc.perform(
                post("/webshop/checkout")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .sessionAttr("orderProduct", new OrderProductDto()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/webshop/"))
                .andExpect(flash().attribute("errors", ImmutableMap.of("products", "No products in this order")));
    }

    /**
     * Test when Product is out of Stock.
     *
     * @throws Exception on AssertionError
     */
    @Test
    public void testCheckoutProductOutOfStock() throws Exception {
        product.setSold(1);
        product.setTicketLimit(1);
        productRepository.saveAndFlush(product);

        mockMvc.perform(
                post("/webshop/checkout")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("products[0].productKey", product.getPublicReference())
                        .param("products[0].productOptionKey", productOption.getPublicReference())
                        .param("products[0].amount", "2")
                        .sessionAttr("orderProduct", new OrderProductDto()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/webshop/"))
                .andExpect(flash().attribute("errors", ImmutableMap.of("invalid", "One or more products in out of stock")));
    }

    /**
     * Test when the Event connected to the Product is in the Past.
     *
     * @throws Exception on AssertionError
     */
    @Test
    public void testCheckoutEventInThePast() throws Exception {
        Event event = product.getEvent();
        event.setStarting(ZonedDateTime.now().minusHours(1));
        event.setEnding(ZonedDateTime.now().minusMinutes(1));
        eventRepository.saveAndFlush(event);

        mockMvc.perform(
                post("/webshop/checkout")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("products[0].productKey", product.getPublicReference())
                        .param("products[0].productOptionKey", productOption.getPublicReference())
                        .param("products[0].amount", "1")
                        .sessionAttr("orderProduct", new OrderProductDto()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/webshop/"))
                .andExpect(flash().attribute("errors", ImmutableMap.of("invalid", "One or more products are no longer be sold")));
    }

    /**
     * Test when Product is not in sell interval.
     *
     * @throws Exception on AssertionError
     */
    @Test
    public void testCheckoutProductNotInSellInterval() throws Exception {
        fail("TODO: Implement Product sell interval");

        mockMvc.perform(
                post("/webshop/checkout")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("products[0].productKey", product.getPublicReference())
                        .param("products[0].productOptionKey", productOption.getPublicReference())
                        .param("products[0].amount", "1")
                        .sessionAttr("orderProduct", new OrderProductDto()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/webshop/"))
                .andExpect(flash().attribute("errors", ImmutableMap.of("invalid", "One or more products are no longer be sold")));
    }

    /**
     * Checkout with a Product that does not exists.
     *
     * @throws Exception on AssertionError
     */
    @Test
    public void testCheckoutProductDoesNotExists() throws Exception {
        mockMvc.perform(
                post("/webshop/checkout")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("products[0].productKey", "asdflk")
                        .param("products[0].productOptionKey", productOption.getPublicReference())
                        .param("products[0].amount", "1")
                        .sessionAttr("orderProduct", new OrderProductDto()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/webshop/"))
                .andExpect(flash().attribute("errors", ImmutableMap.of("invalid", "One or more Products does not exists")));
    }

    /**
     * Checkout with a ProductOption that does not exists.
     *
     * @throws Exception on AssertionError
     */
    @Test
    public void testCheckoutProductOptionDoesNotExists() throws Exception {
        mockMvc.perform(
                post("/webshop/checkout")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("products[0].productKey", product.getPublicReference())
                        .param("products[0].productOptionKey", "asdfasdf")
                        .param("products[0].amount", "1")
                        .sessionAttr("orderProduct", new OrderProductDto()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/webshop/"))
                .andExpect(flash().attribute("errors", ImmutableMap.of("invalid", "One or more ProductOptions does not exists")));
    }

    /**
     * Checkout with invalid Product and ProductOption pair
     *
     * @throws Exception on AssertionError
     */
    @Test
    public void testCheckoutInvalidCombinationProductAndProductOption() throws Exception {
        ProductOption secondOption = new ProductOption();
        productOptionRepository.saveAndFlush(secondOption);

        mockMvc.perform(
                post("/webshop/checkout")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("products[0].productKey", product.getPublicReference())
                        .param("products[0].productOptionKey", secondOption.getPublicReference())
                        .param("products[0].amount", "1")
                        .sessionAttr("orderProduct", new OrderProductDto()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/webshop/"))
                .andExpect(flash().attribute("errors", ImmutableMap.of("invalid", "Invalid combination of product and additional option")));
    }
}