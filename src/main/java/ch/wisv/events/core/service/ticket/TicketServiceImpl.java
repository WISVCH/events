package ch.wisv.events.core.service.ticket;

import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderProduct;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.model.ticket.Ticket;
import ch.wisv.events.core.repository.TicketRepository;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

/**
 * TicketServiceImpl class.
 */
@Service
public class TicketServiceImpl implements TicketService {

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
     * Create a Ticket by an OrderProduct.
     *
     * @param order        of type Order
     * @param orderProduct of type OrderProduct
     */
    @Override
    public Ticket createByOrderProduct(Order order, OrderProduct orderProduct) {
        Ticket ticket = new Ticket(
                order.getOwner(),
                orderProduct.getProduct(),
                this.generateUniqueString(orderProduct.getProduct())
        );

        ticketRepository.saveAndFlush(ticket);

        return ticket;
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
     * Delete tickets by Order.
     *
     * @param order of type Order
     */
    @Override
    public void deleteByOrder(Order order) {
        order.getOrderProducts().forEach(orderProduct ->  {
            List<Ticket> tickets = ticketRepository.findAllByProductAndOwner(orderProduct.getProduct(), order.getOwner());

            ticketRepository.delete(tickets);
        });
    }

    /**
     * Generate a Ticket unique String.
     *
     * @param product of type Product
     *
     * @return String
     */
    private String generateUniqueString(Product product) {
        String ticketUnique = RandomStringUtils.random(6, "0123456789");

        while (ticketRepository.existsByProductAndUniqueCode(product, ticketUnique)) {
            ticketUnique = RandomStringUtils.random(6, "0123456789");
        }

        return ticketUnique;
    }

}
