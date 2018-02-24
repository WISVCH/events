package ch.wisv.events.core.repository;

import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.model.ticket.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

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
public interface TicketRepository extends JpaRepository<Ticket, Integer> {

    /**
     * Find all Ticket by Product and Customer
     *
     * @param product of type Product
     * @param owner   of type Customer
     * @return List<Ticket>
     */
    List<Ticket> findAllByProductAndOwner(Product product, Customer owner);

    /**
     * Find all Ticket by Product
     *
     * @param product of type Product
     * @return List<Ticket>
     */
    List<Ticket> findAllByProduct(Product product);

    /**
     * Find all Ticket by Customer
     *
     * @param owner of type Customer
     * @return List<Ticket>
     */
    List<Ticket> findAllByOwner(Customer owner);

    /**
     * Count all Ticket by Product
     *
     * @param product of type Product
     * @return int
     */
    int countTicketsByProduct(Product product);
}
