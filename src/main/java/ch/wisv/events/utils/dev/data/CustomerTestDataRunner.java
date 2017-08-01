package ch.wisv.events.utils.dev.data;

import ch.wisv.events.core.model.order.Customer;
import ch.wisv.events.core.repository.CustomerRepository;
import org.json.simple.JSONObject;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

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
@Component
@Profile("dev")
@Order(value = 3)
public class CustomerTestDataRunner extends TestDataRunner {

    /**
     * Field eventRepository
     */
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
     * Method loop
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
     * @return Product
     */
    private Customer createCustomer(JSONObject jsonObject) {
        return new Customer(
                (String) jsonObject.get("name"),
                (String) jsonObject.get("email"),
                (String) jsonObject.get("chUsername"),
                (String) jsonObject.get("rfidToken")
        );
    }
}
