package ch.wisv.events.infrastructure.webshop.controller;

import ch.wisv.events.ControllerTest;
import ch.wisv.events.EventsApplicationTest;
import ch.wisv.events.domain.model.order.Order;
import ch.wisv.events.domain.model.order.OrderStatus;
import ch.wisv.events.domain.model.product.Product;
import static ch.wisv.events.infrastructure.webshop.util.WebshopConstant.ERROR_INVALID;
import static ch.wisv.events.infrastructure.webshop.util.WebshopConstant.ERROR_MESSAGE_GUEST_CHECKOUT_NOT_ALLOWED;
import static ch.wisv.events.infrastructure.webshop.util.WebshopConstant.MODEL_ATTR_ERRORS;
import static ch.wisv.events.infrastructure.webshop.util.WebshopConstant.ROUTE_WEBSHOP_CUSTOMER;
import static ch.wisv.events.infrastructure.webshop.util.WebshopConstant.ROUTE_WEBSHOP_LOGIN;
import static ch.wisv.events.infrastructure.webshop.util.WebshopConstant.ROUTE_WEBSHOP_PAYMENT;
import static ch.wisv.events.infrastructure.webshop.util.WebshopConstant.VIEW_WEBSHOP_CUSTOMER;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * WebshopCustomerControllerTest class.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = EventsApplicationTest.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class WebshopCustomerControllerTest extends ControllerTest {

    /** Order. */
    private Order order;

    /**
     * Setup before test.
     */
    @Before
    public void before() {
        this.order = orderRepository.findAll().get(0);
    }

    /**
     * Create new User.
     *
     * @throws Exception on AssertionError
     */
    @Test
    public void testGetCreateCustomer() throws Exception {
        mockMvc.perform(get(String.format("%s/%s", ROUTE_WEBSHOP_CUSTOMER, order.getPublicReference())))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name(VIEW_WEBSHOP_CUSTOMER));
    }

    /**
     * Create new User when order has already been assigned.
     *
     * @throws Exception on AssertionError
     */
    @Test
    public void testGetCreateCustomerOrderAssigned() throws Exception {
        order.setStatus(OrderStatus.ASSIGNED);
        order.setCustomer(userRepository.findAll().get(0));
        orderRepository.saveAndFlush(order);

        mockMvc.perform(get(String.format("%s/%s", ROUTE_WEBSHOP_CUSTOMER, order.getPublicReference())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("%s/%s", ROUTE_WEBSHOP_PAYMENT, order.getPublicReference())));
    }

    /**
     * Create new User when order does not exists.
     *
     * @throws Exception on AssertionError
     */
    @Test
    public void testGetCreateCustomerOrderNotExists() throws Exception {
        mockMvc.perform(get(String.format("%s/not-found", ROUTE_WEBSHOP_CUSTOMER)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("webshop/error/not-found"));
    }

    /**
     * Create new User when order contains CH only product.
     *
     * @throws Exception on AssertionError
     */
    @Test
    public void testGetCreateCustomerChOnlyProduct() throws Exception {
        Product product = order.getItems().get(0).getProduct();
        product.setChOnly(true);
        productRepository.saveAndFlush(product);

        mockMvc.perform(get(String.format("%s/%s", ROUTE_WEBSHOP_CUSTOMER, order.getPublicReference())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("%s/%s", ROUTE_WEBSHOP_LOGIN, order.getPublicReference())))
                .andExpect(flash().attribute(MODEL_ATTR_ERRORS, ImmutableList.of(ERROR_INVALID, ERROR_MESSAGE_GUEST_CHECKOUT_NOT_ALLOWED)));
    }

    /**
     * POST request create customer.
     *
     * @throws Exception on AssertionError
     */
    @Test
    public void testPostCreateCustomer() throws Exception {
        mockMvc.perform(
                post(String.format("%s/%s", ROUTE_WEBSHOP_CUSTOMER, order.getPublicReference()))
                        .param("name", "Ali Baba")
                        .param("email", "ali.baba@ch.tudelft.nl"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("%s/%s", ROUTE_WEBSHOP_PAYMENT, order.getPublicReference())));
    }

    /**
     * POST request create customer.
     *
     * @throws Exception on AssertionError
     */
    @Test
    public void testPostCreateCustomerOrderNotExists() throws Exception {
        mockMvc.perform(post(String.format("%s/not-found", ROUTE_WEBSHOP_CUSTOMER)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("webshop/error/not-found"));
    }

    /**
     * POST request create customer, name is missing.
     *
     * @throws Exception on AssertionError
     */
    @Test
    public void testPostCreateCustomerMissingName() throws Exception {
        mockMvc.perform(post(String.format("%s/%s", ROUTE_WEBSHOP_CUSTOMER, order.getPublicReference()))
                                .param("name", "")
                                .param("email", "ali.baba@ch.tudelft.nl"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("%s/%s", ROUTE_WEBSHOP_CUSTOMER, order.getPublicReference())))
                .andExpect(flash().attribute(MODEL_ATTR_ERRORS, ImmutableMap.of("name", "Name cannot be empty")));
    }

    /**
     * POST request create customer, email is missing.
     *
     * @throws Exception on AssertionError
     */
    @Test
    public void testPostCreateCustomerMissingEmail() throws Exception {
        mockMvc.perform(post(String.format("%s/%s", ROUTE_WEBSHOP_CUSTOMER, order.getPublicReference()))
                                .param("name", "Ali Baba")
                                .param("email", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("%s/%s", ROUTE_WEBSHOP_CUSTOMER, order.getPublicReference())))
                .andExpect(flash().attribute(MODEL_ATTR_ERRORS, ImmutableMap.of("email", "Email cannot be empty")));
    }

    /**
     * POST request create customer, order contains CH only product.
     *
     * @throws Exception on AssertionError
     */
    @Test
    public void testPostCreateCustomerChOnlyProduct() throws Exception {
        Product product = order.getItems().get(0).getProduct();
        product.setChOnly(true);
        productRepository.saveAndFlush(product);

        mockMvc.perform(
                post(String.format("%s/%s", ROUTE_WEBSHOP_CUSTOMER, order.getPublicReference()))
                        .param("name", "Ali Baba")
                        .param("email", "ali.baba@ch.tudelft.nl"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("%s/%s", ROUTE_WEBSHOP_LOGIN, order.getPublicReference())))
                .andExpect(flash().attribute(MODEL_ATTR_ERRORS, ImmutableList.of(ERROR_INVALID, ERROR_MESSAGE_GUEST_CHECKOUT_NOT_ALLOWED)));
    }

    /**
     * POST request create customer, order has been assigned.
     *
     * @throws Exception on AssertionError
     */
    @Test
    public void testPostCreateCustomerOrderAssigned() throws Exception {
        order.setStatus(OrderStatus.ASSIGNED);
        order.setCustomer(userRepository.findAll().get(0));
        orderRepository.saveAndFlush(order);

        mockMvc.perform(
                post(String.format("%s/%s", ROUTE_WEBSHOP_CUSTOMER, order.getPublicReference()))
                        .param("name", "Ali Baba")
                        .param("email", "ali.baba@ch.tudelft.nl"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format("%s/%s", ROUTE_WEBSHOP_PAYMENT, order.getPublicReference())));
    }
}


