package ch.wisv.events.admin.controller;

import ch.wisv.events.ControllerTest;
import ch.wisv.events.EventsApplicationTest;
import ch.wisv.events.core.model.customer.Customer;
import com.google.common.collect.ImmutableList;
import static org.hamcrest.Matchers.any;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = EventsApplicationTest.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class DashboardCustomerControllerTest extends ControllerTest {

    @Test
    public void testIndex() throws Exception {
        Customer customer = this.createCustomer();
        customerRepository.saveAndFlush(customer);

        mockMvc.perform(get("/administrator/customers"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("admin/customers/index"))
                .andExpect(model().attribute("customers", ImmutableList.of(customer)));
    }

    @Test
    public void testView() throws Exception {
        Customer customer = this.createCustomer();
        customerRepository.saveAndFlush(customer);

        mockMvc.perform(get("/administrator/customers/view/" + customer.getKey()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("admin/customers/view"))
                .andExpect(model().attribute("customer", customer));
    }

    @Test
    public void testViewNotFound() throws Exception {
        Customer customer = this.createCustomer();
        customerRepository.saveAndFlush(customer);

        mockMvc.perform(get("/administrator/customers/view/not-found"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/customers/"))
                .andExpect(flash().attribute("warning", "Customer with key not-found not found!"));
    }

    @Test
    public void testCreateGet() throws Exception {
        mockMvc.perform(get("/administrator/customers/create"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("admin/customers/customer"))
                .andExpect(model().attribute("customer", any(Customer.class)));
    }

    @Test
    public void testCreateGetModelAlreadySet() throws Exception {
        Customer customer = this.createCustomer();
        mockMvc.perform(
                get("/administrator/customers/create").flashAttr("customer", customer))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("admin/customers/customer"))
                .andExpect(model().attribute("customer", customer));
    }

    @Test
    public void testCreatePostMissingName() throws Exception {
        Customer customer = new Customer();

        mockMvc.perform(post("/administrator/customers/create")
                                .param("name", "")
                                .param("email", "piet@hein.nl")
                                .param("rfidToken", "RF123456")
                                .sessionAttr("customer", customer))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/customers/create/"))
                .andExpect(flash().attribute("error", "Name is empty, but a required field, so please fill in this field!"));
    }

    @Test
    public void testCreatePostMissingEmail() throws Exception {
        Customer customer = new Customer();

        mockMvc.perform(post("/administrator/customers/create")
                                .param("name", "Piet Hein")
                                .param("email", "")
                                .param("rfidToken", "RF123456")
                                .sessionAttr("customer", customer))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/customers/create/"))
                .andExpect(flash().attribute("error", "Email is empty, but a required field, so please fill in this field!"));
    }

    @Test
    public void testCreatePostDoubleEmail() throws Exception {
        Customer customer = this.createCustomer();
        customerRepository.saveAndFlush(customer);

        mockMvc.perform(post("/administrator/customers/create")
                                .param("name", "Piet Hein")
                                .param("email", customer.getEmail())
                                .param("rfidToken", "RF123458")
                                .sessionAttr("customer", new Customer()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/customers/create/"))
                .andExpect(flash().attribute("error", "Email address is already used!"));
    }

    @Test
    public void testCreatePostDoubleRfidToken() throws Exception {
        Customer customer = this.createCustomer();
        customerRepository.saveAndFlush(customer);

        mockMvc.perform(post("/administrator/customers/create")
                                .param("name", "Piet Hein")
                                .param("email", "piet@hein.nl")
                                .param("rfidToken", customer.getRfidToken())
                                .sessionAttr("customer", new Customer()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/customers/create/"))
                .andExpect(flash().attribute("error", "RFID token is already used!"));
    }

    @Test
    public void testCreatePost() throws Exception {
        Customer customer = new Customer();

        mockMvc.perform(post("/administrator/customers/create")
                                .param("name", "Piet Hein")
                                .param("email", "piet@hein.nl")
                                .param("rfidToken", "RF123456")
                                .sessionAttr("customer", customer))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/customers/"))
                .andExpect(flash().attribute("success", "Customer with name Piet Hein  has been created!"));
    }

    @Test
    public void testEditGet() throws Exception {
        Customer customer = this.createCustomer();
        customerRepository.saveAndFlush(customer);

        mockMvc.perform(get("/administrator/customers/edit/" + customer.getKey()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("admin/customers/customer"))
                .andExpect(model().attribute("customer", customer));
    }

    @Test
    public void testEditGetNotFound() throws Exception {
        Customer customer = this.createCustomer();
        customerRepository.saveAndFlush(customer);

        mockMvc.perform(get("/administrator/customers/edit/not-found"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/customers/"))
                .andExpect(flash().attribute("warning", "Customer with key not-found not found!"));
    }

    @Test
    public void testEditPost() throws Exception {
        Customer customer = this.createCustomer();
        customerRepository.saveAndFlush(customer);

        mockMvc.perform(post("/administrator/customers/edit/" + customer.getKey())
                                .param("name", "Piet Hein")
                                .param("email", "piet@hein.nl")
                                .param("rfidToken", "RF123456")
                                .sessionAttr("customer", customer))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/customers/view/" + customer.getKey()))
                .andExpect(flash().attribute("success", "Customer changes have been saved!"));
    }

    @Test
    public void testEditPostInvalidName() throws Exception {
        Customer customer = this.createCustomer();
        customerRepository.saveAndFlush(customer);

        mockMvc.perform(post("/administrator/customers/edit/" + customer.getKey())
                                .param("name", "")
                                .param("email", "piet@hein.nl")
                                .param("rfidToken", "RF123456")
                                .sessionAttr("customer", customer))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/customers/edit/" + customer.getKey()))
                .andExpect(flash().attribute("error", "Name is empty, but a required field, so please fill in this field!"))
                .andExpect(flash().attribute("customer", any(Customer.class)));
    }

    @Test
    public void testEditPostInvalidEmail() throws Exception {
        Customer customer = this.createCustomer();
        customerRepository.saveAndFlush(customer);

        mockMvc.perform(post("/administrator/customers/edit/" + customer.getKey())
                                .param("name", "Piet Hein")
                                .param("email", "")
                                .param("rfidToken", "RF123456")
                                .sessionAttr("customer", customer))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/customers/edit/" + customer.getKey()))
                .andExpect(flash().attribute("error", "Email is empty, but a required field, so please fill in this field!"))
                .andExpect(flash().attribute("customer", any(Customer.class)));
    }

    @Test
    public void testDeleteGet() throws Exception {
        Customer customer = this.createCustomer();
        customerRepository.saveAndFlush(customer);

        mockMvc.perform(get("/administrator/customers/delete/" + customer.getKey()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/customers/"))
                .andExpect(flash().attribute("success", "Customer with name " + customer.getName() + " has been deleted!"));
    }

    @Test
    public void testDeleteGetNotFound() throws Exception {
        Customer customer = this.createCustomer();
        customerRepository.saveAndFlush(customer);

        mockMvc.perform(get("/administrator/customers/delete/not-found"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/customers/"))
                .andExpect(flash().attribute("error", "Customer with key not-found not found!"));
    }
}