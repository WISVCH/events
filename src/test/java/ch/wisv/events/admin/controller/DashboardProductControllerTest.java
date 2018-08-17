package ch.wisv.events.admin.controller;

import ch.wisv.events.ControllerTest;
import ch.wisv.events.EventsApplicationTest;
import ch.wisv.events.core.model.product.Product;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
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
public class DashboardProductControllerTest extends ControllerTest {

    @Test
    public void testIndex() throws Exception {
        Product product = this.createProduct();
        productRepository.saveAndFlush(product);

        mockMvc.perform(get("/administrator/products"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("admin/products/index"))
                .andExpect(model().attribute("products", ImmutableList.of(product)));
    }

    @Test
    public void testView() throws Exception {
        Product product = this.createProduct();
        productRepository.saveAndFlush(product);

        mockMvc.perform(get("/administrator/products/view/" + product.getKey()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("admin/products/view"))
                .andExpect(model().attribute("product", product));
    }

    @Test
    public void testViewNotFound() throws Exception {
        Product product = this.createProduct();
        productRepository.saveAndFlush(product);

        mockMvc.perform(get("/administrator/products/view/not-found"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/products/"))
                .andExpect(flash().attribute("warning", "Product with key not-found not found!"));
    }

    @Test
    public void testCreateGet() throws Exception {
        mockMvc.perform(get("/administrator/products/create"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("admin/products/product"))
                .andExpect(model().attribute("product", any(Product.class)));
    }

    @Test
    public void testCreateGetModelAlreadySet() throws Exception {
        Product product = this.createProduct();

        mockMvc.perform(
                get("/administrator/products/create").flashAttr("product", product))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("admin/products/product"))
                .andExpect(model().attribute("product", product));
    }

    @Test
    public void testCreatePostMissingName() throws Exception {
        Product product = new Product();

        mockMvc.perform(post("/administrator/products/create")
                                .param("name", "")
                                .param("email", "piet@hein.nl")
                                .param("rfidToken", "RF123456")
                                .sessionAttr("product", product))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/products/create/"))
                .andExpect(flash().attribute("error", "Name is empty, but a required field, so please fill in this field!"));
    }

    @Test
    public void testCreatePostMissingEmail() throws Exception {
        Product product = new Product();

        mockMvc.perform(post("/administrator/products/create")
                                .param("name", "Piet Hein")
                                .param("email", "")
                                .param("rfidToken", "RF123456")
                                .sessionAttr("product", product))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/products/create/"))
                .andExpect(flash().attribute("error", "Email is empty, but a required field, so please fill in this field!"));
    }

    @Test
    public void testCreatePostDoubleEmail() throws Exception {
        Product product = this.createProduct();
        productRepository.saveAndFlush(product);

        mockMvc.perform(post("/administrator/products/create")
                                .param("name", "Piet Hein")
                                .param("rfidToken", "RF123458")
                                .sessionAttr("product", new Product()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/products/create/"))
                .andExpect(flash().attribute("error", "Email address is already used!"));
    }

    @Test
    public void testCreatePostDoubleRfidToken() throws Exception {
        Product product = this.createProduct();
        productRepository.saveAndFlush(product);

        mockMvc.perform(post("/administrator/products/create")
                                .param("name", "Piet Hein")
                                .param("email", "piet@hein.nl")
                                .sessionAttr("product", new Product()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/products/create/"))
                .andExpect(flash().attribute("error", "RFID token is already used!"));
    }

    @Test
    public void testCreatePost() throws Exception {
        Product product = new Product();

        mockMvc.perform(post("/administrator/products/create")
                                .param("name", "Piet Hein")
                                .param("email", "piet@hein.nl")
                                .param("rfidToken", "RF123456")
                                .sessionAttr("product", product))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/products/"))
                .andExpect(flash().attribute("success", "Product with name Piet Hein  has been created!"));
    }

    @Test
    public void testEditGet() throws Exception {
        Product product = this.createProduct();
        productRepository.saveAndFlush(product);

        mockMvc.perform(get("/administrator/products/edit/" + product.getKey()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("admin/products/product"))
                .andExpect(model().attribute("product", product));
    }

    @Test
    public void testEditGetNotFound() throws Exception {
        Product product = this.createProduct();
        productRepository.saveAndFlush(product);

        mockMvc.perform(get("/administrator/products/edit/not-found"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/products/"))
                .andExpect(flash().attribute("warning", "Product with key not-found not found!"));
    }

    @Test
    public void testEditPost() throws Exception {
        Product product = this.createProduct();
        productRepository.saveAndFlush(product);

        mockMvc.perform(post("/administrator/products/edit/" + product.getKey())
                                .param("title", product.getTitle())
                                .param("sellStart", product.getSellStart().toString())
                                .param("cost", product.getCost().toString())
                                .param("maxSoldPerCustomer", product.getMaxSoldPerCustomer().toString())
                                .sessionAttr("product", new Product()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/products/view/" + product.getKey()))
                .andExpect(flash().attribute("success", "Product changes have been saved!"));
    }

    @Test
    public void testEditPostInvalidName() throws Exception {
        Product product = this.createProduct();
        productRepository.saveAndFlush(product);

        mockMvc.perform(post("/administrator/products/edit/" + product.getKey())
                                .param("name", "")
                                .param("email", "piet@hein.nl")
                                .param("rfidToken", "RF123456")
                                .sessionAttr("product", product))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/products/edit/" + product.getKey()))
                .andExpect(flash().attribute("error", "Name is empty, but a required field, so please fill in this field!"))
                .andExpect(flash().attribute("product", any(Product.class)));
    }

    @Test
    public void testEditPostInvalidEmail() throws Exception {
        Product product = this.createProduct();
        productRepository.saveAndFlush(product);

        mockMvc.perform(post("/administrator/products/edit/" + product.getKey())
                                .param("name", "Piet Hein")
                                .param("email", "")
                                .param("rfidToken", "RF123456")
                                .sessionAttr("product", product))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/products/edit/" + product.getKey()))
                .andExpect(flash().attribute("error", "Email is empty, but a required field, so please fill in this field!"))
                .andExpect(flash().attribute("product", any(Product.class)));
    }

    @Test
    public void testDeleteGet() throws Exception {
        Product product = this.createProduct();
        productRepository.saveAndFlush(product);

        mockMvc.perform(get("/administrator/products/delete/" + product.getKey()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/products/"))
                .andExpect(flash().attribute("success", "Product with name has been deleted!"));
    }

    @Test
    public void testDeleteGetNotFound() throws Exception {
        Product product = this.createProduct();
        productRepository.saveAndFlush(product);

        mockMvc.perform(get("/administrator/products/delete/not-found"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/products/"))
                .andExpect(flash().attribute("error", "Product with key not-found not found!"));
    }
}