package ch.wisv.events.core.service.customer;

import ch.wisv.connect.common.model.CHUserInfo;
import ch.wisv.events.core.exception.normal.CustomerInvalidException;
import ch.wisv.events.core.exception.normal.CustomerNotFoundException;
import ch.wisv.events.core.model.customer.Customer;
import java.time.LocalDateTime;
import java.util.List;

/**
 * CustomerService interface.
 */
public interface CustomerService {

    /**
     * Get all customers.
     *
     * @return list of all customers
     */
    List<Customer> getAllCustomers();

    /**
     * Method getAllCustomerCreatedAfter ...
     *
     * @param after of type LocalDateTime
     *
     * @return List
     */
    List<Customer> getAllCustomerCreatedAfter(LocalDateTime after);

    /**
     * Get a customer by key.
     *
     * @param key key
     *
     * @return Customer
     *
     * @throws CustomerNotFoundException when Customer is not found
     */
    Customer getByKey(String key) throws CustomerNotFoundException;

    /**
     * Get a Customer by its sub.
     *
     * @param sub of type String
     *
     * @return Customer
     *
     * @throws CustomerNotFoundException when Customer is not found
     */
    Customer getBySub(String sub) throws CustomerNotFoundException;

    /**
     * Get a customer by Email.
     *
     * @param email of type String
     *
     * @return Customer
     *
     * @throws CustomerNotFoundException when Customer is not found
     */
    Customer getByEmail(String email) throws CustomerNotFoundException;

    /**
     * Get a customer by rfidToken.
     *
     * @param query of type String
     *
     * @return Customer
     *
     * @throws CustomerNotFoundException when Customer is not found
     */
    Customer getByRfidToken(String query) throws CustomerNotFoundException;

    /**
     * Add a new customer.
     *
     * @param customer customer model
     *
     * @throws CustomerInvalidException when Customer is invalid
     */
    void create(Customer customer) throws CustomerInvalidException;

    /**
     * Add a new customer by ChUserInfo.
     *
     * @param userInfo of type CHUserInfo
     *
     * @throws CustomerInvalidException when Customer is invalid
     */
    Customer createByChUserInfo(CHUserInfo userInfo) throws CustomerInvalidException;

    /**
     * Update a existing customer.
     *
     * @param customer customer model
     *
     * @throws CustomerInvalidException  when Customer is invalid
     * @throws CustomerNotFoundException when Customer is not found
     */
    void update(Customer customer) throws CustomerInvalidException, CustomerNotFoundException;

    /**
     * Delete a customer.
     *
     * @param customer customer model
     */
    void delete(Customer customer);
}
