package ch.wisv.events.utils.dev.data;

import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.repository.CustomerRepository;
import org.json.simple.JSONObject;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@Order(value = 3)
public class CustomerTestDataRunner extends TestDataRunner {

    /** CustomerRepository. */
    private final CustomerRepository customerRepository;

    /**
     * Constructor EventTestDataRunner creates a new EventTestDataRunner instance.
     *
     * @param customerRepository of type VendorRepository
     */
    public CustomerTestDataRunner(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;

        this.setJsonFileName("customers.json");
    }

    /**
     * Method loop.
     *
     * @param jsonObject of type JSONObject
     */
    @Override
    protected void loop(JSONObject jsonObject) {
        Customer customer = this.createCustomer(jsonObject);

        this.customerRepository.saveAndFlush(customer);
    }

    /**
     * Method createProduct ...
     *
     * @param jsonObject of type JSONObject
     *
     * @return Product
     */
    private Customer createCustomer(JSONObject jsonObject) {
        return new Customer(
                (String) jsonObject.get("sub"),
                (String) jsonObject.get("name"),
                (String) jsonObject.get("email"),
                (String) jsonObject.get("chUsername"),
                (String) jsonObject.get("rfidToken")
        );
    }
}
