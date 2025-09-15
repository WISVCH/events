package ch.wisv.events.core.service;

import ch.wisv.events.ServiceTest;
import ch.wisv.events.core.exception.normal.TicketNotFoundException;
import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderProduct;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.model.ticket.Ticket;
import ch.wisv.events.core.model.ticket.TicketStatus;
import ch.wisv.events.core.repository.TicketRepository;
import ch.wisv.events.core.service.event.EventService;
import ch.wisv.events.core.service.googlewallet.GoogleWalletService;
import ch.wisv.events.core.service.ticket.TicketService;
import ch.wisv.events.core.service.ticket.TicketServiceImpl;
import ch.wisv.events.core.util.VatRate;
import com.google.common.collect.ImmutableList;

import java.awt.image.BufferedImage;
import java.util.*;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * TicketService test.
 */
public class TicketServiceTest extends ServiceTest {

    /** Mock of TicketRepository. */
    @Mock
    private TicketRepository ticketRepository;

    @Mock
    /** EventService. */
    private EventService eventService;

    @Mock
    /** GoogleWalletService. */
    private GoogleWalletService googleWalletService;

    /** TicketService. */
    private TicketService ticketService;

    /** Tickets. */
    private Ticket ticket1, ticket2, ticket3;

    /** Product. */
    private Product product;

    /** Customer. */
    private Customer customer;

    /**
     * Setup for the test class.
     */
    @Before
    public void setUp() {
        ticketService = new TicketServiceImpl(ticketRepository, eventService, googleWalletService);

        ticket1 = new Ticket();
        ticket2 = new Ticket();
        ticket3 = new Ticket();

        product = new Product();
        product.setCost(1.d);
        product.setVatRate(VatRate.VAT_HIGH);
        customer = new Customer();
    }

    /**
     * Tear down for this test class.
     */
    @After
    public void tearDown() {
        ticketService = null;
    }

    /**
     *
     */
    @Test
    public void getByUniqueCode() throws Exception {
        when(ticketRepository.findByProductAndUniqueCode(product, "123456")).thenReturn(Optional.of(ticket1));
        Ticket tickets = ticketService.getByUniqueCode(product, "123456");

        assertEquals(ticket1, tickets);
    }

    /**
     *
     */
    @Test
    public void getByUniqueCodeException() throws Exception {
        thrown.expect(TicketNotFoundException.class);
        when(ticketRepository.findByProductAndUniqueCode(product, "123456")).thenReturn(Optional.empty());

        ticketService.getByUniqueCode(product, "123456");
    }

    /**
     *
     * @throws Exception
     */
    @Test
    public void generateQrCodeException() throws Exception {
        thrown.expect(NotFoundException.class);
        String uniqueCode = "131313";
        Ticket ticket = new Ticket();
        ticket.setUniqueCode(uniqueCode);

        ticketService.generateQrCode(ticket);
    }

    /**
     * End-to-end test generation of a QR code.
     * @throws Exception
     */
    @Test
    public void generateQrCode() throws Exception {
        String uniqueCode = UUID.randomUUID().toString();
        Ticket ticket = new Ticket();
        ticket.setUniqueCode(uniqueCode);

        BufferedImage qrCode = ticketService.generateQrCode(ticket);

        LuminanceSource source = new BufferedImageLuminanceSource(qrCode);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        String decodedUniqueCode = reader.decode(bitmap).getText();

        assertEquals(uniqueCode, decodedUniqueCode);
    }

    /**
     *
     */
    @Test
    public void getByKey() throws Exception {
        when(ticketRepository.findByKey("123456")).thenReturn(Optional.of(ticket1));
        Ticket tickets = ticketService.getByKey("123456");

        assertEquals(ticket1, tickets);
    }

    /**
     *
     */
    @Test
    public void getByKeyException() throws Exception {
        thrown.expect(TicketNotFoundException.class);
        when(ticketRepository.findByKey("123456")).thenReturn(Optional.empty());

        ticketService.getByKey("123456");
    }

    /**
     * Test if it will just return the elements from the ticketRepository.
     */
    @Test
    public void getAllByProductAndCustomer() {
        when(ticketRepository.findAllByProductAndOwner(product, customer)).thenReturn(ImmutableList.of(ticket1));

        List<Ticket> tickets = ticketService.getAllByProductAndCustomer(product, customer);

        assertEquals(ImmutableList.of(ticket1), tickets);
    }

    /**
     * Test if it will just return the elements from the ticketRepository.
     */
    @Test
    public void getAllByProductAndCustomerEmpty() {
        when(ticketRepository.findAllByProductAndOwner(product, customer)).thenReturn(ImmutableList.of());

        List<Ticket> tickets = ticketService.getAllByProductAndCustomer(product, customer);

        assertEquals(ImmutableList.of(), tickets);
    }

    /**
     * Test if it will just return the elements from the ticketRepository.
     */
    @Test
    public void getAllByProduct() {
        when(ticketRepository.findAllByProduct(product)).thenReturn(ImmutableList.of(ticket1, ticket2));
        List<Ticket> tickets = ticketService.getAllByProduct(product);

        assertEquals(ImmutableList.of(ticket1, ticket2), tickets);
    }

    /**
     * Test if it will just return the elements from the ticketRepository.
     */
    @Test
    public void getAllByProductEmpty() {
        when(ticketRepository.findAllByProduct(product)).thenReturn(ImmutableList.of());
        List<Ticket> tickets = ticketService.getAllByProduct(product);

        assertEquals(ImmutableList.of(), tickets);
    }

    /**
     * Test if it will just return the elements from the ticketRepository.
     */
    @Test
    public void getAllByCustomer() {
        when(ticketRepository.findAllByOwnerOrderByIdDesc(customer)).thenReturn(ImmutableList.of(ticket1, ticket2));
        List<Ticket> tickets = ticketService.getAllByCustomer(customer);

        assertEquals(ImmutableList.of(ticket1, ticket2), tickets);
    }

    /**
     * Test if it will just return the elements from the ticketRepository.
     */
    @Test
    public void getAllByCustomerEmpty() {
        when(ticketRepository.findAllByOwnerOrderByIdDesc(customer)).thenReturn(ImmutableList.of());
        List<Ticket> tickets = ticketService.getAllByCustomer(customer);

        assertEquals(ImmutableList.of(), tickets);
    }

    /**
     * Test if the amount of products in the Order will be delete.
     */
    @Test
    public void deleteByOrder() {
        Order order = new Order();
        order.setOwner(customer);
        int random = (new Random()).nextInt(500);
        this.fillOrderWithAmountOfProducts(order, random);

        when(ticketRepository.findAllByOrder(order)).thenReturn(ImmutableList.of(ticket3));
        doNothing().when(ticketRepository).delete(ticket3);

        ticketService.deleteByOrder(order);

        List<Ticket> ticketList = new ArrayList<>();
        ticketList.add(ticket3);

        verify(ticketRepository, times(1)).findAllByOrder(order);
        verify(ticketRepository, times(1)).deleteAll(ticketList);
    }

    @Test
    public void updateStatus() {
        Ticket ticket = mock(Ticket.class);
        ticketService.updateStatus(ticket, TicketStatus.SCANNED);

        verify(ticket, times(1)).setStatus(TicketStatus.SCANNED);
    }

    /**
     *
     */
    @Test
    public void createByOrder() {
        Order order = new Order();
        order.setOwner(customer);
        int random = (new Random()).nextInt(500);
        this.fillOrderWithAmountOfProducts(order, random);

        when(ticketRepository.saveAndFlush(any(Ticket.class))).thenReturn(new Ticket());

        List<Ticket> createdTickets = ticketService.createByOrder(order);

        assertEquals(random, createdTickets.size());
        assertEquals(customer, createdTickets.get(Math.abs((new Random()).nextInt(random))).getOwner());
        assertEquals(product, createdTickets.get(Math.abs((new Random()).nextInt(random))).getProduct());
    }

    /**
     *
     */
    @Test
    public void createByOrderUniqueCodeExists() {
        Order order = new Order();
        order.setOwner(customer);
        this.fillOrderWithAmountOfProducts(order, 1);

        when(ticketRepository.saveAndFlush(any(Ticket.class))).thenReturn(new Ticket());
        when(ticketRepository.existsByProductAndUniqueCode(any(Product.class), any(String.class))).thenReturn(true).thenReturn(false);

        ticketService.createByOrder(order);

        verify(ticketRepository, times(2)).existsByProductAndUniqueCode(any(Product.class), any(String.class));
    }

    /**
     *
     */
    @Test
    public void createByOrderAlreadyCreated() {
        Order order = new Order();
        order.setTicketCreated(true);

        assertNull(ticketService.createByOrder(order));
    }

    /**
     * Create OrderProducts and add them to the Order.
     *
     * @param order  of type Order.
     * @param random of type String.
     */
    private void fillOrderWithAmountOfProducts(Order order, int random) {
        for (int i = 0; i < random; i++) {
            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setProduct(product);
            orderProduct.setAmount(1L);

            order.addOrderProduct(orderProduct);
        }
    }
}