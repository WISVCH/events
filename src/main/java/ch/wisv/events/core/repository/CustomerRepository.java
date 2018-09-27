package ch.wisv.events.core.repository;

import ch.wisv.events.core.model.customer.Customer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    /**
     * Find a customer by its rfidToken.
     *
     * @param token of type String
     *
     * @return Optional
     */
    Optional<Customer> findByRfidToken(String token);

    /**
     * Find a customer by its key.
     *
     * @param key key
     *
     * @return optional
     */
    Optional<Customer> findByKey(String key);

    /**
     * Find a Customer by its email.
     *
     * @param email of type String
     *
     * @return Optional
     */
    Optional<Customer> findByEmailIgnoreCase(String email);

    /**
     * Method findAllByCreatedAtAfter ...
     *
     * @param after of type LocalDateTime
     *
     * @return List
     */
    List<Customer> findAllByCreatedAtAfter(LocalDateTime after);

    /**
     * Find a Customer by its sub.
     *
     * @param sub of type String
     *
     * @return Optional
     */
    Optional<Customer> findBySub(String sub);

}
