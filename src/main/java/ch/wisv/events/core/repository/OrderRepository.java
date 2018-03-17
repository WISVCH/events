package ch.wisv.events.core.repository;

import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

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
public interface OrderRepository extends JpaRepository<Order, Integer> {

    /**
     * Method findOneByPublicReference find Order by public reference.
     *
     * @param publicReference of type String
     *
     * @return Optional<Order>
     */
    Optional<Order> findOneByPublicReference(String publicReference);

    /**
     * Method findAllByOwner find Order by Customer.
     *
     * @param owner of type Customer
     *
     * @return List<Order>
     */
    List<Order> findAllByOwner(Customer owner);

    /**
     * Find all Order by a Customer that have a given status
     *
     * @param owner  of type Customer
     * @param status of type OrderStatus
     *
     * @return List<Order>
     */
    List<Order> findAllByOwnerAndStatus(Customer owner, OrderStatus status);

}
