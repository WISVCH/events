package ch.wisv.events;

import ch.wisv.events.domain.repository.EventRepository;
import ch.wisv.events.domain.repository.OrderRepository;
import ch.wisv.events.domain.repository.ProductOptionRepository;
import ch.wisv.events.domain.repository.ProductRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

/**
 * ControllerTest class.
 */
@Transactional
public abstract class ControllerTest {

    /** ExpectedException. */
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    /** EventRepository. */
    @Autowired
    protected EventRepository eventRepository;

    /** ProductRepository. */
    @Autowired
    protected ProductRepository productRepository;

    /** ProductOptionRepository. */
    @Autowired
    protected ProductOptionRepository productOptionRepository;

    /** OrderRepository. */
    @Autowired
    protected OrderRepository orderRepository;

    /** WebApplicationContext. */
    @Autowired
    protected WebApplicationContext wac;

    /** MockMvc. */
    protected MockMvc mockMvc;

    /**
     * Setup and tear down.
     */
    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

}