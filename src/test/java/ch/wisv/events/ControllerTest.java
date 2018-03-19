package ch.wisv.events;

import ch.wisv.events.core.repository.EventRepository;
import ch.wisv.events.core.repository.OrderRepository;
import ch.wisv.events.core.repository.ProductRepository;
import ch.wisv.events.core.service.event.EventService;
import ch.wisv.events.core.service.order.OrderService;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@Transactional
public abstract class ControllerTest {

    /**
     * Services.
     */
    @Autowired
    protected EventService eventService;

    @Autowired
    protected OrderService orderService;

    /**
     * Repositories.
     */
    @Autowired
    protected EventRepository eventRepository;

    @Autowired
    protected ProductRepository productRepository;

    @Autowired
    protected OrderRepository orderRepository;

    /**
     * Testing utils.
     */
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    protected WebApplicationContext wac;

    protected MockMvc mockMvc;

    /**
     * Setup and tear down.
     */
    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @After
    public void tearDown() {
        this.mockMvc = null;

        // Clear repository
        orderRepository.deleteAll();
        eventRepository.deleteAll();
        productRepository.deleteAll();
    }
}