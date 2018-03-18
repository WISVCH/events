package ch.wisv.events;

import ch.wisv.events.core.service.auth.AuthenticationService;
import ch.wisv.events.core.service.event.EventService;
import ch.wisv.events.core.service.order.OrderService;
import ch.wisv.events.core.service.order.OrderValidationService;
import ch.wisv.events.webshop.service.PaymentsService;
import ch.wisv.events.webshop.service.WebshopService;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;


@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public abstract class ControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected EventService eventService;

    @MockBean
    protected AuthenticationService authenticationService;

    @MockBean
    protected OrderService orderService;

    @MockBean
    protected WebshopService webshopService;

    @MockBean
    protected PaymentsService paymentsService;

    @MockBean
    protected OrderValidationService orderValidationService;

    @Rule
    public ExpectedException thrown = ExpectedException.none();
}