package ch.wisv.events.core.service.ticket;

import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderProduct;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.model.ticket.Ticket;
import ch.wisv.events.core.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Copyright (c) 2016  W.I.S.V. 'Christiaan Huygens'
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
