package ch.wisv.events.core.repository;

import ch.wisv.events.core.model.order.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
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
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    /**
     * Method findById ...
     *
     * @param customerId of type Integer
     * @return Optional<Customer>
     */
    Optional<Customer> findById(Integer customerId);

    /**
     * Find a customer by its rfidToken.
     *
     * @param token of type String
     * @return optional
     */
    Optional<Customer> findByRfidToken(String token);

    /**
     * Find a customer by its key.
     *
     * @param key key
     * @return optional
     */
    Optional<Customer> findByKey(String key);

    /**
     * Find a Customer by its email.
     *
     * @param email of type String
     * @return Optional<Customer>
     */
    Optional<Customer> findByEmail(String email);

    /**
     * Method findAllByCreatedAtAfter ...
     *
     * @param after of type LocalDateTime
     * @return List<Customer>
     */
    List<Customer> findAllByCreatedAtAfter(LocalDateTime after);

    /**
     * Find a Customer by its sub.
     *
     * @param sub of type String
     * @return Optional<Customer>
     */
    Optional<Customer> findBySub(String sub);

    /**
     * Find a Customer by its username.
     *
     * @param username of type username.
     * @return Optional<Customer>
     */
    Optional<Customer> findByChUsername(String username);
}
