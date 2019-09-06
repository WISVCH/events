package ch.wisv.events.infrastructure.webshop.controller;

import ch.wisv.events.ControllerTest;
import ch.wisv.events.EventsApplicationTest;
import ch.wisv.events.domain.model.order.Order;
import ch.wisv.events.domain.model.order.OrderItem;
import ch.wisv.events.domain.model.order.OrderStatus;
import ch.wisv.events.domain.model.product.Product;
import static ch.wisv.events.infrastructure.webshop.util.WebshopConstant.MODEL_ATTR_ORDER;
import static ch.wisv.events.infrastructure.webshop.util.WebshopConstant.ROUTE_WEBSHOP_COMPLETE;
import static ch.wisv.events.infrastructure.webshop.util.WebshopConstant.ROUTE_WEBSHOP_PAYMENT;
import static ch.wisv.events.infrastructure.webshop.util.WebshopConstant.VIEW_WEBSHOP_CHECKOUT_PAYMENT;
import com.google.common.collect.ImmutableList;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * WebshopPaymentControllerTest class.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = EventsApplicationTest.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class WebshopPaymentControllerTest extends ControllerTest {

    /**
     * GET view payment options
     *
     * @throws Exception on AssertionError.
     */
    @Test
    public void testGetViewPaymentOptions() throws Exception {
        Order order = new Order();
        orderRepository.saveAndFlush(order);

        mockMvc.perform(get(String.format(URL_CONCAT, ROUTE_WEBSHOP_PAYMENT, order.getPublicReference())))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name(VIEW_WEBSHOP_CHECKOUT_PAYMENT))
                .andExpect(content().string(containsString("href=\"/checkout/" + order.getPublicReference() + "/payment/ideal\"")))
                .andExpect(content().string(containsString("href=\"/checkout/" + order.getPublicReference() + "/payment/sofort\"")))
                .andExpect(content().string(containsString("href=\"/checkout/" + order.getPublicReference() + "/payment/reservation\"")));
    }

    /**
     * GET view payment options, when Order contains non reservable Product.
     *
     * @throws Exception on AssertionError.
     */
    @Test
    public void testGetViewPaymentOptionsNonReservableProduct() throws Exception {
        Product product = new Product();
        product.setReservable(false);
        productRepository.saveAndFlush(product);

        OrderItem orderItem = new OrderItem(product, null, 1);
        orderItemRepository.saveAndFlush(orderItem);

        Order order = new Order();
        order.setItems(ImmutableList.of(orderItem));
        orderRepository.saveAndFlush(order);

        mockMvc.perform(get(String.format(URL_CONCAT, ROUTE_WEBSHOP_PAYMENT, order.getPublicReference())))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name(VIEW_WEBSHOP_CHECKOUT_PAYMENT))
                .andExpect(model().attribute(MODEL_ATTR_ORDER, order))
                .andExpect(content().string(containsString("href=\"/checkout/" + order.getPublicReference() + "/payment/ideal\"")))
                .andExpect(content().string(containsString("href=\"/checkout/" + order.getPublicReference() + "/payment/sofort\"")))
                .andExpect(content().string(not(containsString("href=\"/checkout/" + order.getPublicReference() + "/payment/reservation\""))));
    }

    /**
     * GET view payment options, when Order is already paid.
     *
     * @throws Exception on AssertionError.
     */
    @Test
    public void upgradetestGetViewPaymentOptionsAlreadyPaid() throws Exception {
        Order order = new Order();
        order.setStatus(OrderStatus.PAID);
        orderRepository.saveAndFlush(order);

        mockMvc.perform(get(String.format(URL_CONCAT, ROUTE_WEBSHOP_PAYMENT, order.getPublicReference())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format(URL_CONCAT, ROUTE_WEBSHOP_COMPLETE, order.getPublicReference())));
    }
}