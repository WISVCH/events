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
import org.fluttercode.datafactory.impl.DataFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

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
        vendor.setStartingTime(LocalDateTime.now());
        vendor.setEndingTime(LocalDateTime.of(2017, 3, 7, 12, 30));
        vendorRepository.saveAndFlush(vendor);

        this.createRandomCustomers();
    }

    private void createProducts() throws Exception {
        Product product = new Product(
                "T.U.E.S.Day: Gamerendinging with too many players",
                "Ticket for T.U.E.S.Day: Gamerendinging with too many players 7 maart",
                0.f,
                100,
                LocalDateTime.of(2017, 2, 28, 13, 30),
                LocalDateTime.of(2017, 3, 6, 16, 0)
        );

        productRepository.saveAndFlush(product);

        Product product2 = new Product(
                "Broodje",
                "Broodje",
                0.f,
                100,
                LocalDateTime.of(2017, 2, 28, 13, 30),
                LocalDateTime.of(2017, 3, 6, 16, 0)
        );

        productRepository.saveAndFlush(product2);
    }

    private void createEvents() throws Exception {
        Event event = new Event(
                "T.U.E.S.Day: Gamerendinging with too many players",
                "Something about the lunchlecture",
                "Lecture hall Boole",
                200,
                null,
                "http://placehold.it/300x300",
                LocalDateTime.of(2017, 3, 7, 12, 30),
                LocalDateTime.of(2017, 3, 7, 13, 30)
        );
        event.addProduct(productRepository.findById(1).get());
        event.getOptions().setPublished(EventStatus.PUBLISHED);
        eventRepository.save(event);
    }

    /**
     * Method scanRandomOrders set of 25 order the status to scanned
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
     * Method createRandomCustomers ...
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
