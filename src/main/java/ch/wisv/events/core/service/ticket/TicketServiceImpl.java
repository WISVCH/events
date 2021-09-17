package ch.wisv.events.core.service.ticket;

import ch.wisv.events.core.exception.normal.TicketNotFoundException;
import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderProduct;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.model.ticket.Ticket;
import ch.wisv.events.core.model.ticket.TicketStatus;
import ch.wisv.events.core.repository.TicketRepository;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

/**
 * TicketServiceImpl class.
 */
@Service
public class TicketServiceImpl implements TicketService {

    /** /** Ticket unique code length. */
    private static final int TICKET_UNIQUE_LENGTH = 6;

    /** Ticket unique code allowed chars. */
    private static final String TICKET_UNIQUE_ALLOWED_CHARS = "0123456789";

    /** TicketRepository. */
    private final TicketRepository ticketRepository;

    /**
     * TicketServiceImpl constructor.
     *
     * @param ticketRepository of type TicketRepository
     */
    public TicketServiceImpl(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    /**
     * Get Ticket by unique code.
     *
     * @param product    of type Product
     * @param uniqueCode of type String
     *
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
     *
     * @return Ticket
     *
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
     *
     * @return List of Tickets
     */
    @Override
    public List<Ticket> getAllByProductAndCustomer(Product product, Customer customer) {
        return ticketRepository.findAllByProductAndOwner(product, customer);
    }

    /**
     * Get all Ticket by a Product.
     *
     * @param product of type Product
     *
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
     *
     * @return List of Tickets
     */
    @Override
    public List<Ticket> getAllByCustomer(Customer customer) {
        return ticketRepository.findAllByOwner(customer);
    }

    /**
     * Get all Tickets by a Order.
     *
     * @param order of type Order
     *
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
     *
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
     *
     * @return String
     */
    private String generateUniqueString(Product product) {
        String ticketUnique = RandomStringUtils.random(TICKET_UNIQUE_LENGTH, TICKET_UNIQUE_ALLOWED_CHARS);

        while (ticketRepository.existsByProductAndUniqueCode(product, ticketUnique)) {
            ticketUnique = RandomStringUtils.random(TICKET_UNIQUE_LENGTH, TICKET_UNIQUE_ALLOWED_CHARS);
        }

        return ticketUnique;
    }

}
