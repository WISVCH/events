package ch.wisv.events.infrastructure.webshop.controller;

import ch.wisv.events.ControllerTest;
import ch.wisv.events.EventsApplicationTest;
import ch.wisv.events.domain.model.order.Order;
import ch.wisv.events.domain.model.order.OrderStatus;
import ch.wisv.events.domain.model.product.Product;
import ch.wisv.events.domain.model.user.User;
import static ch.wisv.events.infrastructure.webshop.util.WebshopConstant.ERROR_INVALID;
import static ch.wisv.events.infrastructure.webshop.util.WebshopConstant.ERROR_MESSAGE_GUEST_CHECKOUT_NOT_ALLOWED;
import static ch.wisv.events.infrastructure.webshop.util.WebshopConstant.MODEL_ATTR_ERRORS;
import static ch.wisv.events.infrastructure.webshop.util.WebshopConstant.ROUTE_WEBSHOP_CUSTOMER;
import static ch.wisv.events.infrastructure.webshop.util.WebshopConstant.ROUTE_WEBSHOP_LOGIN;
import static ch.wisv.events.infrastructure.webshop.util.WebshopConstant.ROUTE_WEBSHOP_LOGIN_OPTION_CONNECT;
import static ch.wisv.events.infrastructure.webshop.util.WebshopConstant.ROUTE_WEBSHOP_LOGIN_OPTION_GUEST;
import static ch.wisv.events.infrastructure.webshop.util.WebshopConstant.ROUTE_WEBSHOP_PAYMENT;
import ch.wisv.events.services.AuthenticationService;
import com.google.common.collect.ImmutableMap;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.when;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * WebshopLoginControllerTest class.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = EventsApplicationTest.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class WebshopLoginControllerTest extends ControllerTest {



    /**
     * AuthenticationService mock.
     */
    @MockBean
    public AuthenticationService authenticationService;

    /** Order. */
    private Order order;

    /**
     *
     */
    @Before
    public void before() {
        this.order = orderRepository.findAll().get(0);
    }

    /**
     * Test login page in normal circumstances.
     *
     * @throws Exception on AssertionError
     */
    @Test
    public void testLoginIndex() throws Exception {
        mockMvc.perform(get(String.format(URL_CONCAT, ROUTE_WEBSHOP_LOGIN, order.getPublicReference())))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("webshop/webshop-checkout-login"));
    }

    /**
     * Test login page when order is already assigned to a User.
     *
     * @throws Exception on AssertionError
     */
    @Test
    public void testLoginOrderAssignedToUser() throws Exception {
        order.setStatus(OrderStatus.ASSIGNED);
        order.setCustomer(userRepository.findAll().get(0));
        orderRepository.saveAndFlush(order);

        mockMvc.perform(get(String.format(URL_CONCAT, ROUTE_WEBSHOP_LOGIN, order.getPublicReference())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format(URL_CONCAT, ROUTE_WEBSHOP_PAYMENT, order.getPublicReference())));
    }

    /**
     * Test checkout through CH Connect.
     *
     * @throws Exception on AssertionError
     */
    @Test
    public void testLoginChConnect() throws Exception {
        User user = userRepository.findAll().get(0);
        when(authenticationService.getLoggedInUser()).thenReturn(user);

        mockMvc.perform(get(String.format(URL_CONCAT_WITH_OPTION, ROUTE_WEBSHOP_LOGIN, order.getPublicReference(), ROUTE_WEBSHOP_LOGIN_OPTION_CONNECT)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format(URL_CONCAT, ROUTE_WEBSHOP_PAYMENT, order.getPublicReference())));

        Order check = orderRepository.findByPublicReference(order.getPublicReference()).get();

        assertEquals(user, check.getCustomer());
    }

    /**
     * Test checkout through CH Connect, when Order already ASSIGNED.
     *
     * @throws Exception on AssertionError
     */
    @Test
    public void testLoginChConnectAssignedToUser() throws Exception {
        order.setStatus(OrderStatus.ASSIGNED);
        order.setCustomer(userRepository.findAll().get(0));
        orderRepository.saveAndFlush(order);

        mockMvc.perform(get(String.format(URL_CONCAT_WITH_OPTION, ROUTE_WEBSHOP_LOGIN, order.getPublicReference(), ROUTE_WEBSHOP_LOGIN_OPTION_CONNECT)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format(URL_CONCAT, ROUTE_WEBSHOP_PAYMENT, order.getPublicReference())));
    }

    /**
     * Test checkout as Guest.
     *
     * @throws Exception on AssertionError
     */
    @Test
    public void testLoginGuest() throws Exception {
        Product product = order.getItems().get(0).getProduct();
        product.setChOnly(false);
        productRepository.saveAndFlush(product);

        mockMvc.perform(get(String.format(URL_CONCAT_WITH_OPTION, ROUTE_WEBSHOP_LOGIN, order.getPublicReference(), ROUTE_WEBSHOP_LOGIN_OPTION_GUEST)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format(URL_CONCAT, ROUTE_WEBSHOP_CUSTOMER, order.getPublicReference())));
    }

    /**
     * Test checkout as Guest when Order contains CH only product.
     *
     * @throws Exception on AssertionError
     */
    @Test
    public void testLoginGuestChOnlyProduct() throws Exception {
        Product product = order.getItems().get(0).getProduct();
        product.setChOnly(true);
        productRepository.saveAndFlush(product);

        mockMvc.perform(get(String.format(URL_CONCAT_WITH_OPTION, ROUTE_WEBSHOP_LOGIN, order.getPublicReference(), ROUTE_WEBSHOP_LOGIN_OPTION_GUEST)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format(URL_CONCAT, ROUTE_WEBSHOP_LOGIN, order.getPublicReference())))
                .andExpect(flash().attribute(MODEL_ATTR_ERRORS, ImmutableMap.of(ERROR_INVALID, ERROR_MESSAGE_GUEST_CHECKOUT_NOT_ALLOWED)));
    }

    /**
     * Test checkout as Guest, when Order already ASSIGNED.
     *
     * @throws Exception on AssertionError
     */
    @Test
    public void testLoginGuestAssignedToUser() throws Exception {
        order.setStatus(OrderStatus.ASSIGNED);
        order.setCustomer(userRepository.findAll().get(0));
        orderRepository.saveAndFlush(order);

        mockMvc.perform(get(String.format(URL_CONCAT_WITH_OPTION, ROUTE_WEBSHOP_LOGIN, order.getPublicReference(), ROUTE_WEBSHOP_LOGIN_OPTION_GUEST)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(String.format(URL_CONCAT, ROUTE_WEBSHOP_PAYMENT, order.getPublicReference())));
    }
}