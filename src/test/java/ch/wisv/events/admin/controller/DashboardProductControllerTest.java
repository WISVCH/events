package ch.wisv.events.admin.controller;

import ch.wisv.events.ControllerTest;
import ch.wisv.events.EventsApplicationTest;
import ch.wisv.events.core.model.product.Product;
import com.google.common.collect.ImmutableList;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.contains;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
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
    public void testCreatePostMissingTitle() throws Exception {
        Product product = new Product();

        mockMvc.perform(post("/administrator/products/create")
                                .param("title", "")
                                .param("sellStart", "2018-01-02T16:00")
                                .param("sellEnd", "2018-01-02T17:00")
                                .param("cost", "1.50")
                                .param("maxSoldPerCustomer", "1")
                                .sessionAttr("product", product))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/products/create/"))
                .andExpect(flash().attribute("error", "Title is required, and therefore should be filled in!"));
    }

    @Test
    public void testCreatePostMissingSellStart() throws Exception {
        Product product = new Product();

        mockMvc.perform(post("/administrator/products/create")
                                .param("title", "Product")
                                .param("sellEnd", "2018-01-02T17:00")
                                .param("cost", "1.50")
                                .param("maxSoldPerCustomer", "1")
                                .sessionAttr("product", product))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/products/create/"))
                .andExpect(flash().attribute("error", "Starting date for selling should be before the ending time"));
    }

    @Test
    public void testCreatePostMissingSellEndBeforeSellStart() throws Exception {
        Product product = new Product();

        mockMvc.perform(post("/administrator/products/create")
                                .param("title", "Product")
                                .param("sellStart", "2018-01-02T18:00")
                                .param("sellEnd", "2018-01-02T17:00")
                                .param("cost", "1.50")
                                .param("maxSoldPerCustomer", "1")
                                .sessionAttr("product", product))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/products/create/"))
                .andExpect(flash().attribute("error", "Starting date for selling should be before the ending time"));
    }

    @Test
    public void testCreatePostMissingCost() throws Exception {
        Product product = new Product();

        mockMvc.perform(post("/administrator/products/create")
                                .param("title", "Product")
                                .param("sellStart", "2018-01-02T16:00")
                                .param("sellEnd", "2018-01-02T17:00")
                                .param("cost", "")
                                .param("maxSoldPerCustomer", "1")
                                .sessionAttr("product", product))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/products/create/"))
                .andExpect(flash().attribute("error", "Price is required, and therefore should be filled in!"));
    }

    @Test
    public void testCreatePostMissingMaxSoldPerCustomerToHigh() throws Exception {
        Product product = new Product();

        mockMvc.perform(post("/administrator/products/create")
                                .param("title", "Product")
                                .param("sellStart", "2018-01-02T16:00")
                                .param("sellEnd", "2018-01-02T17:00")
                                .param("cost", "1.50")
                                .param("maxSoldPerCustomer", "26")
                                .sessionAttr("product", product))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/products/create/"))
                .andExpect(flash().attribute("error", "Max sold per customer should be between 1 and 25!"));
    }

    @Test
    public void testCreatePostMissingMaxSoldPerCustomerToLow() throws Exception {
        Product product = new Product();

        mockMvc.perform(post("/administrator/products/create")
                                .param("title", "Product")
                                .param("sellStart", "2018-01-02T16:00")
                                .param("sellEnd", "2018-01-02T17:00")
                                .param("cost", "1.50")
                                .param("maxSoldPerCustomer", "0")
                                .sessionAttr("product", product))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/products/create/"))
                .andExpect(flash().attribute("error", "Max sold per customer should be between 1 and 25!"));
    }

    @Test
    public void testCreatePost() throws Exception {
        Product product = new Product();

        mockMvc.perform(post("/administrator/products/create")
                                .param("title", "Product")
                                .param("sellStart", "2018-01-02T16:00")
                                .param("sellEnd", "2018-01-02T17:00")
                                .param("cost", "1.50")
                                .param("maxSoldPerCustomer", "1")
                                .sessionAttr("product", product))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/administrator/products/view/*"))
                .andExpect(flash().attribute("success", "Product with title Product has been created!"));
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
                .andExpect(flash().attribute("error", "Product with key not-found not found!"));
    }

    @Test
    public void testEditPostMissingTitle() throws Exception {
        Product product = this.createProduct();
        productRepository.saveAndFlush(product);

        mockMvc.perform(post("/administrator/products/edit/" + product.getKey())
                                .param("title", "")
                                .param("sellStart", "2018-01-02T16:00")
                                .param("sellEnd", "2018-01-02T17:00")
                                .param("cost", "1.50")
                                .param("maxSoldPerCustomer", "1")
                                .sessionAttr("product", product))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/products/edit/" + product.getKey()))
                .andExpect(flash().attribute("error", "Title is required, and therefore should be filled in!"));
    }

    @Test
    public void testEditPostMissingSellStart() throws Exception {
        Product product = this.createProduct();
        productRepository.saveAndFlush(product);

        mockMvc.perform(post("/administrator/products/edit/" + product.getKey())
                                .param("title", "Product")
                                .param("sellEnd", "2018-01-02T17:00")
                                .param("cost", "1.50")
                                .param("maxSoldPerCustomer", "1")
                                .sessionAttr("product", product))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/products/edit/" + product.getKey()))
                .andExpect(flash().attribute("error", "Starting date for selling is required, and therefore should be filled in!"));
    }

    @Test
    public void testEditPostMissingSellEndBeforeSellStart() throws Exception {
        Product product = this.createProduct();
        productRepository.saveAndFlush(product);

        mockMvc.perform(post("/administrator/products/edit/" + product.getKey())
                                .param("title", "Product")
                                .param("sellStart", "2018-01-02T18:00")
                                .param("sellEnd", "2018-01-02T17:00")
                                .param("cost", "1.50")
                                .param("maxSoldPerCustomer", "1")
                                .sessionAttr("product", product))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/products/edit/" + product.getKey()))
                .andExpect(flash().attribute("error", "Starting date for selling should be before the ending time"));
    }

    @Test
    public void testEditPostMissingCost() throws Exception {
        Product product = this.createProduct();
        productRepository.saveAndFlush(product);

        mockMvc.perform(post("/administrator/products/edit/" + product.getKey())
                                .param("title", "Product")
                                .param("sellStart", "2018-01-02T16:00")
                                .param("sellEnd", "2018-01-02T17:00")
                                .param("cost", "")
                                .param("maxSoldPerCustomer", "1")
                                .sessionAttr("product", product))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/products/edit/" + product.getKey()))
                .andExpect(flash().attribute("error", "Price is required, and therefore should be filled in!"));
    }

    @Test
    public void testEditPostMissingMaxSoldPerCustomerToHigh() throws Exception {
        Product product = this.createProduct();
        productRepository.saveAndFlush(product);

        mockMvc.perform(post("/administrator/products/edit/" + product.getKey())
                                .param("title", "Product")
                                .param("sellStart", "2018-01-02T16:00")
                                .param("sellEnd", "2018-01-02T17:00")
                                .param("cost", "1.50")
                                .param("maxSoldPerCustomer", "26")
                                .sessionAttr("product", product))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/products/edit/" + product.getKey()))
                .andExpect(flash().attribute("error", "Max sold per customer should be between 1 and 25!"));
    }

    @Test
    public void testEditPostMissingMaxSoldPerCustomerToLow() throws Exception {
        Product product = this.createProduct();
        productRepository.saveAndFlush(product);

        mockMvc.perform(post("/administrator/products/edit/" + product.getKey())
                                .param("title", "Product")
                                .param("sellStart", "2018-01-02T16:00")
                                .param("sellEnd", "2018-01-02T17:00")
                                .param("cost", "1.50")
                                .param("maxSoldPerCustomer", "0")
                                .sessionAttr("product", product))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/products/edit/" + product.getKey()))
                .andExpect(flash().attribute("error", "Max sold per customer should be between 1 and 25!"));
    }

    @Test
    public void testEditPost() throws Exception {
        Product product = this.createProduct();
        productRepository.saveAndFlush(product);

        mockMvc.perform(post("/administrator/products/edit/" + product.getKey())
                                .param("title", "Product")
                                .param("sellStart", "2018-01-02T16:00")
                                .param("sellEnd", "2018-01-02T17:00")
                                .param("cost", "1.50")
                                .param("maxSoldPerCustomer", "1")
                                .sessionAttr("product", product))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/products/view/" + product.getKey()))
                .andExpect(flash().attribute("success", "Product changes have been saved!"));
    }

    @Test
    public void testDeleteGet() throws Exception {
        Product product = this.createProduct();
        productRepository.saveAndFlush(product);

        mockMvc.perform(get("/administrator/products/delete/" + product.getKey()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/products/"))
                .andExpect(flash().attribute("success", "Product Product product has been deleted!"));
    }

    @Test
    public void testDeleteGetNotFound() throws Exception {
        mockMvc.perform(get("/administrator/products/delete/not-found"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/products/"))
                .andExpect(flash().attribute("error", "Product with key not-found not found!"));
    }


    @Test
    public void testOverviewGet() throws Exception {
        Product product = this.createProduct();
        productRepository.saveAndFlush(product);

        mockMvc.perform(get("/administrator/products/overview/" + product.getKey()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("admin/products/overview"))
                .andExpect(model().attributeExists("tickets"))
                .andExpect(model().attribute("product", product));
    }

    @Test
    public void testOverviewGetNotFound() throws Exception {
        mockMvc.perform(get("/administrator/products/overview/not-found"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrator/products/"))
                .andExpect(flash().attribute("error", "Product with key not-found not found!"));
    }
}