package ch.wisv.events.core.service.ticket;

import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderProduct;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.model.ticket.Ticket;
import ch.wisv.events.core.repository.TicketRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;

    /**
     * Default constructor
     *
     * @param ticketRepository of type TicketRepository
     */
    public TicketServiceImpl(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    /**
     * Create a Ticket by an OrderProduct.
     *
     * @param order        of type Order
     * @param orderProduct of type OrderProduct
     */
    @Override
    public Ticket createByOrderProduct(Order order, OrderProduct orderProduct) {
        Ticket ticket = new Ticket(order.getOwner(), orderProduct.getProduct(), this.generateUniqueString());

        ticketRepository.saveAndFlush(ticket);

        return ticket;
    }

    /**
     * Generate a Ticket unique String.
     *
     * @return String
     */
    private String generateUniqueString() {
        // TODO: generate unique string
        return "123456";
    }

    /**
     * Get all Ticket by a Product and Customer
     *
     * @param product  of type Product
     * @param customer of type Customer
     *
     * @return List<Ticket>
     */
    @Override
    public List<Ticket> getAllByProductAndCustomer(Product product, Customer customer) {
        return ticketRepository.findAllByProductAndOwner(product, customer);
    }

    /**
     * Get all Ticket by a Product
     *
     * @param product of type Product
     *
     * @return List<Ticket>
     */
    @Override
    public List<Ticket> getAllByProduct(Product product) {
        return ticketRepository.findAllByProduct(product);
    }

    /**
     * Get all Ticket by a Customer
     *
     * @param customer of type Customer
     *
     * @return List<Ticket>
     */
    @Override
    public List<Ticket> getAllByCustomer(Customer customer) {
        return ticketRepository.findAllByOwner(customer);
    }

    /**
     * Get all Ticket by Product
     *
     * @param product of type Product
     *
     * @return int
     */
    @Override
    public int countByProduct(Product product) {
        return ticketRepository.countTicketsByProduct(product);
    }
}
