package ch.wisv.events.core.service.ticket;

import ch.wisv.events.core.exception.normal.EventNotFoundException;
import ch.wisv.events.core.exception.normal.TicketNotFoundException;
import ch.wisv.events.core.exception.normal.TicketNotTransferableException;
import ch.wisv.events.core.exception.normal.TicketPassFailedException;
import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderProduct;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.model.ticket.Ticket;
import ch.wisv.events.core.model.ticket.TicketStatus;
import ch.wisv.events.core.repository.TicketRepository;

import java.awt.image.BufferedImage;
import java.time.format.DateTimeFormatter;
import java.util.*;

import ch.wisv.events.core.service.event.EventService;
import ch.wisv.events.core.service.googlewallet.GoogleWalletService;
import ch.wisv.events.core.util.QrCode;
import com.google.zxing.WriterException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.validation.constraints.NotNull;

/**
 * TicketServiceImpl class.
 */
@Service
public class TicketServiceImpl implements TicketService {
    /**
     * TicketRepository.
     */
    private final TicketRepository ticketRepository;

    /**
     * EventService.
     */
    private final EventService eventService;

    /**
     * GoogleWalletService.
     */
    private final GoogleWalletService googleWalletService;

    @Value("${links.passes}")
    @NotNull
    private String passesLink;

    /**
     * TicketServiceImpl constructor.
     *
     * @param ticketRepository of type TicketRepository
     * @param eventService     of type EventService
     */
    public TicketServiceImpl(TicketRepository ticketRepository, EventService eventService, GoogleWalletService googleWalletService) {
        this.ticketRepository = ticketRepository;
        this.eventService = eventService;
        this.googleWalletService = googleWalletService;
    }

    /**
     * Get Ticket by unique code.
     *
     * @param product    of type Product
     * @param uniqueCode of type String
     * @return Ticket
     */
    @Override
    public Ticket getByUniqueCode(Product product, String uniqueCode) throws TicketNotFoundException {
        return ticketRepository.findByProductAndUniqueCode(product, uniqueCode)
                .orElseThrow(TicketNotFoundException::new);
    }

    /**
     * Get ticket by key.
     *
     * @param key of type String
     * @return Ticket
     * @throws TicketNotFoundException when ticket is not found
     */
    @Override
    public Ticket getByKey(String key) throws TicketNotFoundException {
        return ticketRepository.findByKey(key)
                .orElseThrow(TicketNotFoundException::new);
    }

    /**
     * Get all Ticket by a Product and Customer.
     *
     * @param product  of type Product
     * @param customer of type Customer
     * @return List of Tickets
     */
    @Override
    public List<Ticket> getAllByProductAndCustomer(Product product, Customer customer) {
        return ticketRepository.findAllByProductAndOwner(product, customer);
    }

    /**
     * Get all Ticket for one of multiple products and Customer.
     *
     * @param products of type List<Product></Product>
     * @param customer of type Customer
     *
     * @return List of Tickets
     */
    @Override
    public List<Ticket> getAllByProductsAndCustomer(List<Product> products, Customer customer) {
        return ticketRepository.findAllByProductInAndOwner(products, customer);
    }

    /**
     * Get all Ticket by a Product.
     *
     * @param product of type Product
     * @return List of Tickets
     */
    @Override
    public List<Ticket> getAllByProduct(Product product) {
        return ticketRepository.findAllByProduct(product);
    }

    /**
     * Get all Ticket by a Customer.
     *
     * @param customer of type Customer
     * @return List of Tickets
     */
    @Override
    public List<Ticket> getAllByCustomer(Customer customer) {
        return ticketRepository.findAllByOwnerOrderByIdDesc(customer);
    }

    /**
     * Get all Tickets of an Order.
     *
     * @param order of type Order
     * @return List of Tickets
     */
    @Override
    public List<Ticket> getAllByOrder(Order order) {
        return ticketRepository.findAllByOrder(order);
    }

    /**
     * Delete tickets by Order.
     *
     * @param order of type Order
     */
    @Override
    public void deleteByOrder(Order order) {
        List<Ticket> tickets = ticketRepository.findAllByOrder(order);

        ticketRepository.deleteAll(tickets);
    }

    /**
     * Update the Ticket status.
     *
     * @param ticket of type Ticket
     * @param status of type TicketStatus
     */
    @Override
    public void updateStatus(Ticket ticket, TicketStatus status) {
        ticket.setStatus(status);
        ticketRepository.saveAndFlush(ticket);
    }

    /**
     * Get all Ticket.
     *
     * @return List of Ticket
     */
    @Override
    public List<Ticket> getAll() {
        return ticketRepository.findAll();
    }

    /**
     * Create a Ticket by an OrderProduct.
     *
     * @param order of type Order
     * @return List of Ticket
     */
    @Override
    public List<Ticket> createByOrder(Order order) {
        if (order.isTicketCreated()) {
            return null;
        }

        List<Ticket> tickets = new ArrayList<>();

        for (OrderProduct orderProduct : order.getOrderProducts()) {
            for (int i = 0; i < orderProduct.getAmount(); i++) {
                Ticket ticket = new Ticket(
                        order,
                        order.getOwner(),
                        orderProduct.getProduct(),
                        this.generateUniqueString(orderProduct.getProduct())
                );

                tickets.add(ticket);
                ticketRepository.saveAndFlush(ticket);
            }
        }

        return tickets;
    }

    /**
     * Generate a Ticket unique String.
     *
     * @param product of type Product
     * @return String
     */
    private String generateUniqueString(Product product) {
        // Generate a UUID
        String ticketUnique = UUID.randomUUID().toString();

        while (ticketRepository.existsByProductAndUniqueCode(product, ticketUnique)) {
            ticketUnique = UUID.randomUUID().toString();
        }

        return ticketUnique;
    }

    /**
     * Generate a QR code from the uniqueCode.
     *
     * @param ticket of type Ticket
     * @return BufferedImage
     * @throws WriterException          when QR code is not generated
     * @throws IllegalArgumentException when uniqueCode is not a valid UUID.
     */
    public BufferedImage generateQrCode(Ticket ticket) throws IllegalArgumentException, WriterException {
        // Assert that the uniqueCode is a UUID (LEGACY CHECK)
        if (!ticket.getUniqueCode().matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")) {
            throw new IllegalArgumentException("The uniqueCode is not a UUID");
        }

        return QrCode.generateQrCode(ticket.getUniqueCode());
    }

    /**
     * Transfer a Ticket to another Customer.
     *
     * @param currentCustomer of type Customer
     * @param newCustomer     of type Customer
     */
    public Ticket transfer(Ticket ticket, Customer currentCustomer, Customer newCustomer) throws TicketNotTransferableException {
        // Get event from ticket product
        Event event = null;
        try {
            event = eventService.getByProduct(ticket.getProduct());
        } catch (EventNotFoundException ignored) {
        }

        // Check if the ticket can be transferred
        ticket.canTransfer(currentCustomer, newCustomer, event);

        // Generate new unique code
        String uniqueCode = this.generateUniqueString(ticket.getProduct());

        // Update ticket
        ticket.setUniqueCode(uniqueCode);
        ticket.setOwner(newCustomer);

        ticketRepository.saveAndFlush(ticket);

        return ticket;
    }

    /**
     * Get the Apple Pass of a Ticket.
     *
     * @param ticket of type Ticket
     * @return byte[]
     * @throws TicketPassFailedException when the Apple Pass is not generated
     */
    public byte[] getApplePass(Ticket ticket) throws TicketPassFailedException {
        try {
            RestTemplate restTemplate = new RestTemplate();
            Map<String, String> params = new HashMap<>();

            params.put("title", ticket.getProduct().getTitle());
            params.put("description", ticket.getProduct().getDescription());
            params.put("date", ticket.getProduct().getEvent().getStart().format(DateTimeFormatter.ISO_LOCAL_DATE));
            params.put("time", ticket.getProduct().getEvent().getStart().format(DateTimeFormatter.ofPattern("HH:mm")));
            params.put("location", ticket.getProduct().getEvent().getLocation());
            params.put("name", ticket.getOwner().getName());
            params.put("code", ticket.getUniqueCode());

            return restTemplate.getForObject(passesLink +
                    "?title={title}&description={description}&date={date}&time={time}" +
                            "&location={location}&code={code}&name={name}"
                    , byte[].class, params);
        } catch (Exception e) {
            e.printStackTrace();
            throw new TicketPassFailedException(e.getMessage());
        }
    }

    /**
     * Get Google Wallet pass for a Ticket.
     * @param ticket of type Ticket.
     * @return A link the user can use to add the ticket to their wallet.
     * @throws TicketPassFailedException when pass is not generated
     */
    public String getGooglePass(Ticket ticket) throws TicketPassFailedException {
        return this.googleWalletService.getPass(ticket);
    }
}
