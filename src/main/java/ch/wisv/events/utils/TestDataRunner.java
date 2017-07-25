package ch.wisv.events.utils;

import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.event.EventStatus;
import ch.wisv.events.core.model.order.Customer;
import ch.wisv.events.core.model.order.SoldProduct;
import ch.wisv.events.core.model.order.SoldProductStatus;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.model.sales.Vendor;
import ch.wisv.events.core.repository.*;
import ch.wisv.events.core.service.order.OrderService;
import ch.wisv.events.core.service.product.SoldProductService;
import com.google.common.collect.ImmutableList;
import org.fluttercode.datafactory.impl.DataFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * TestDataRunner.
 * <p>
 * Adds some data into the Repositories.
 */
@Component
@Profile("dev")
public class TestDataRunner implements CommandLineRunner {

    private final DataFactory df;

    private final EventRepository eventRepository;

    private final ProductRepository productRepository;

    private final VendorRepository vendorRepository;

    private final CustomerRepository customerRepository;

    private final OrderRepository orderRepository;

    private final OrderService orderService;

    private final SoldProductService soldProductService;

    private final SoldProductRepository soldProductRepository;

    private final int
            events = 1,
            products = 3,
            orders = 1,
            maxProductsPerOrder = 2,
            scanned = 1,
            customer = 1;


    public TestDataRunner(EventRepository eventRepository, ProductRepository productRepository,
                          VendorRepository vendorRepository, CustomerRepository customerRepository,
                          OrderRepository orderRepository, OrderService orderService,
                          SoldProductService soldProductService, SoldProductRepository soldProductRepository) {
        this.eventRepository = eventRepository;
        this.productRepository = productRepository;
        this.vendorRepository = vendorRepository;
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.orderService = orderService;
        this.soldProductService = soldProductService;
        this.soldProductRepository = soldProductRepository;
        df = new DataFactory();
    }

    @Override
    public void run(String... args) throws Exception {
        this.createProducts();
        this.createEvents();

        /*
          Sell access
         */
        Vendor vendor = new Vendor();
        vendor.setLdapGroup(LDAPGroupEnum.W3CIE);
        vendor.addEvent(eventRepository.findById(1));
        vendor.setStartingTime(LocalDateTime.now().minusDays(10).truncatedTo(ChronoUnit.MINUTES));
        vendor.setEndingTime(LocalDateTime.now().plusDays(10).truncatedTo(ChronoUnit.MINUTES));
        vendorRepository.saveAndFlush(vendor);

        this.createRandomCustomers();
    }

    /**
     * Method createProducts create a product with a sub product
     *
     * @throws Exception when
     */
    private void createProducts() throws Exception {
        Product product = new Product(
                "T.U.E.S.Day: Gamerendinging with too many players",
                "Ticket for T.U.E.S.Day: Gamerendinging with too many players 7 maart",
                0.f,
                100,
                LocalDateTime.now().minusDays(10).truncatedTo(ChronoUnit.MINUTES),
                LocalDateTime.now().plusDays(10).truncatedTo(ChronoUnit.MINUTES)
        );
        productRepository.saveAndFlush(product);

        Product product2 = new Product(
                "Broodje",
                "Broodje",
                0.f,
                100,
                LocalDateTime.now().minusDays(10).truncatedTo(ChronoUnit.MINUTES),
                LocalDateTime.now().plusDays(10).truncatedTo(ChronoUnit.MINUTES)
        );
        productRepository.saveAndFlush(product2);

        product.setProducts(ImmutableList.of(product2));
        productRepository.save(product);
    }

    /**
     * Method createEvents create a test event
     *
     * @throws Exception when
     */
    private void createEvents() throws Exception {
        JSONParser parser = new JSONParser();
        JSONArray events = (JSONArray) parser.parse(new FileReader("src/main/resources/testdata/events.json"));

        int i = 0;
        for (Object event1 : events) {
            JSONObject jsonEvent = (JSONObject) event1;
            Event event = new Event(
                    (String) jsonEvent.get("title"),
                    (String) jsonEvent.get("description"),
                    (String) jsonEvent.get("location"),
                    ((Long) jsonEvent.get("target")).intValue(),
                    ((Long) jsonEvent.get("maxSold")).intValue(),
                    (String) jsonEvent.get("imageUrl"),
                    LocalDateTime.now().plusDays(i).truncatedTo(ChronoUnit.MINUTES),
                    LocalDateTime.now().plusDays(i).plusHours(1).truncatedTo(ChronoUnit.MINUTES),
                    (String) jsonEvent.get("shortDescription")
            );

            if (jsonEvent.get("productNumber") != null) {
                event.addProduct(productRepository.findById(((Long) jsonEvent.get("productNumber")).intValue()).get());
            }
            event.getOptions().setPublished(EventStatus.PUBLISHED);
            eventRepository.save(event);
            i++;
        }

    }

    /**
     * Method scanRandomOrders sets products status to scanned of an Order
     *
     * @throws Exception when
     */
    private void scanRandomOrders() throws Exception {
        SoldProduct soldProduct;
        for (int i = 0; i < this.scanned; i++) {
            soldProduct = soldProductRepository.findOne(df.getNumberBetween(1, (int) soldProductRepository.count()));
            soldProduct.setStatus(SoldProductStatus.SCANNED);

            soldProductService.update(soldProduct);
        }
    }

    /**
     * Method createRandomCustomers create a Customer
     *
     * @throws Exception when
     */
    private void createRandomCustomers() throws Exception {
        Customer customer = new Customer();
        customer.setName("Christiaan Huygens");
        customer.setEmail("christiaanh@ch.tudelft.nl");
        customer.setChUsername("christiaanh");
        customer.setRfidToken("123");

        customerRepository.saveAndFlush(customer);
    }

}
