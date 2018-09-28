package ch.wisv.events.core.service;

import ch.wisv.events.ServiceTest;
import ch.wisv.events.core.exception.normal.EventNotFoundException;
import ch.wisv.events.core.exception.normal.OrderExceedCustomerLimitException;
import ch.wisv.events.core.exception.normal.OrderExceedEventLimitException;
import ch.wisv.events.core.exception.normal.OrderExceedProductLimitException;
import ch.wisv.events.core.exception.normal.OrderInvalidException;
import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderProduct;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.model.order.PaymentMethod;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.model.ticket.Ticket;
import ch.wisv.events.core.repository.OrderRepository;
import ch.wisv.events.core.service.event.EventService;
import ch.wisv.events.core.service.order.OrderValidationService;
import ch.wisv.events.core.service.order.OrderValidationServiceImpl;
import ch.wisv.events.core.service.ticket.TicketService;
import com.google.common.collect.ImmutableList;
import java.util.Collections;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 */
public class OrderValidationServiceImplTest extends ServiceTest {

    /** OrderService. */
    @Mock
    private OrderRepository orderRepository;

    /** TicketService. */
    @Mock
    private TicketService ticketService;

    /** EventService. */
    @Mock
    private EventService eventService;

    /** OrderValidationService. */
    private OrderValidationService orderValidationService;

    private Order order;

    private Product product;

    @Before
    public void setUp() {
        orderValidationService = new OrderValidationServiceImpl(orderRepository, ticketService, eventService);

        product = mock(Product.class);

        order = new Order();
        order.setOwner(mock(Customer.class));
        order.setCreatedBy("events-online");
        order.setAmount(1.d);
        order.setPaymentMethod(PaymentMethod.CASH);
        order.setStatus(OrderStatus.PAID);
        order.updateOrderAmount();

        OrderProduct orderProduct = new OrderProduct(product, 1.d, 1L);
        order.setOrderProducts(
                Collections.singletonList(orderProduct)
        );
    }

    @After
    public void tearDown() {
        orderValidationService = null;
    }

    @Test
    public void assertOrderIsValidInvalidAmount() throws Exception {
        order.setAmount(null);

        thrown.expect(OrderInvalidException.class);
        thrown.expectMessage("Order amount can not be null");

        orderValidationService.assertOrderIsValid(order);
    }

    @Test
    public void assertOrderIsValidInvalidWrongAmount() throws Exception {
        order.setAmount(2.d);

        thrown.expect(OrderInvalidException.class);
        thrown.expectMessage("Order amount does not match");

        orderValidationService.assertOrderIsValid(order);
    }

    @Test
    public void assertOrderIsValidInvalidCreatedBy() throws Exception {
        order.setAmount(1.d);
        order.setCreatedBy(null);

        thrown.expect(OrderInvalidException.class);
        thrown.expectMessage("Order created by can not be null");

        orderValidationService.assertOrderIsValid(order);
    }

    @Test
    public void assertOrderIsValidNoProducts() throws Exception {
        order.setOrderProducts(ImmutableList.of());

        thrown.expect(OrderInvalidException.class);
        thrown.expectMessage("Order should contain products");

        orderValidationService.assertOrderIsValid(order);
    }

    @Test
    public void assertOrderIsValidEventNoMaxSold() throws Exception {
        Event event = new Event();
        event.setMaxSold(null);

        when(eventService.getByProduct(product)).thenReturn(event);
        when(product.getSold()).thenReturn(9);
        when(product.getReserved()).thenReturn(1);
        when(product.getMaxSold()).thenReturn(null);

        order.setAmount(1.d);
        orderValidationService.assertOrderIsValid(order);
    }

    @Test
    public void assertOrderIsValidEventLimitExceed() throws Exception {
        Event event = new Event();
        event.setMaxSold(10);
        event.addProduct(product);

        when(eventService.getByProduct(product)).thenReturn(event);
        when(product.getSold()).thenReturn(9);
        when(product.getReserved()).thenReturn(1);
        when(product.getMaxSold()).thenReturn(null);

        order.setAmount(1.d);

        thrown.expect(OrderExceedEventLimitException.class);
        thrown.expectMessage("Event limit exceeded (max 0 tickets allowed).");

        orderValidationService.assertOrderIsValid(order);
    }

    @Test
    public void assertOrderIsValidEventNotFound() throws Exception {
        when(eventService.getByProduct(product)).thenThrow(EventNotFoundException.class);
        when(product.getSold()).thenReturn(9);
        when(product.getReserved()).thenReturn(1);
        when(product.getMaxSold()).thenReturn(null);

        order.setAmount(1.d);
        orderValidationService.assertOrderIsValid(order);
    }

    @Test
    public void assertOrderIsValidProductLimitExceed() throws Exception {
        Event event = new Event();
        event.setMaxSold(15);
        event.addProduct(product);

        when(eventService.getByProduct(product)).thenReturn(event);
        when(product.getSold()).thenReturn(9);
        when(product.getReserved()).thenReturn(1);
        when(product.getMaxSold()).thenReturn(10);

        order.setAmount(1.d);

        thrown.expect(OrderExceedProductLimitException.class);
        thrown.expectMessage("Product limit exceeded (max 0 tickets allowed).");

        orderValidationService.assertOrderIsValid(order);

    }

    @Test
    public void assertOrderIsValidForCustomer() throws Exception {
        Customer customer = mock(Customer.class);
        Order prevOrder = mock(Order.class);

        when(product.getMaxSoldPerCustomer()).thenReturn(1);
        when(orderRepository.findAllByOwnerAndStatus(customer, OrderStatus.RESERVATION)).thenReturn(ImmutableList.of(prevOrder));

        orderValidationService.assertOrderIsValidForCustomer(order, customer);
    }

    @Test
    public void assertOrderIsValidForCustomerNoLimit() throws Exception {
        Customer customer = mock(Customer.class);
        Order prevOrder = mock(Order.class);

        when(product.getMaxSoldPerCustomer()).thenReturn(null);
        when(orderRepository.findAllByOwnerAndStatus(customer, OrderStatus.RESERVATION)).thenReturn(ImmutableList.of(prevOrder));
        when(ticketService.getAllByProductAndCustomer(product, customer)).thenReturn(ImmutableList.of(mock(Ticket.class)));

        orderValidationService.assertOrderIsValidForCustomer(order, customer);
    }

    @Test
    public void assertOrderIsValidForCustomerExceedSold() throws Exception {
        Customer customer = mock(Customer.class);
        Order prevOrder = mock(Order.class);

        when(product.getMaxSoldPerCustomer()).thenReturn(1);
        when(orderRepository.findAllByOwnerAndStatus(customer, OrderStatus.RESERVATION)).thenReturn(ImmutableList.of(prevOrder));
        when(ticketService.getAllByProductAndCustomer(product, customer)).thenReturn(ImmutableList.of(mock(Ticket.class)));

        thrown.expect(OrderExceedCustomerLimitException.class);
        thrown.expectMessage("Customer limit exceeded (max 0 tickets allowed).");

        orderValidationService.assertOrderIsValidForCustomer(order, customer);
    }

    @Test
    public void assertOrderIsValidForCustomerExceedReservation() throws Exception {
        Customer customer = mock(Customer.class);
        Order prevOrder = mock(Order.class);
        OrderProduct prevOrderProduct = mock(OrderProduct.class);

        when(prevOrderProduct.getAmount()).thenReturn(1L);
        when(prevOrderProduct.getProduct()).thenReturn(product);
        when(prevOrder.getOrderProducts()).thenReturn(ImmutableList.of(prevOrderProduct));

        when(product.getMaxSoldPerCustomer()).thenReturn(1);
        when(orderRepository.findAllByOwnerAndStatus(customer, OrderStatus.RESERVATION)).thenReturn(ImmutableList.of(prevOrder));
        when(ticketService.getAllByProductAndCustomer(product, customer)).thenReturn(ImmutableList.of());

        thrown.expect(OrderExceedCustomerLimitException.class);
        thrown.expectMessage("Customer limit exceeded (max 0 tickets allowed).");

        orderValidationService.assertOrderIsValidForCustomer(order, customer);
    }

    @Test
    public void assertOrderIsValidForIdealPayment() throws Exception {
        order.setAmount(1.d);
        order.setStatus(OrderStatus.PENDING);

        orderValidationService.assertOrderIsValidForPayment(order);
    }

    @Test
    public void assertOrderIsValidForIdealPaymentInvalid() throws Exception {
        thrown.expect(OrderInvalidException.class);
        thrown.expectMessage("Invalid status of the Order");
        order.setStatus(OrderStatus.ERROR);

        orderValidationService.assertOrderIsValidForPayment(order);
    }
}