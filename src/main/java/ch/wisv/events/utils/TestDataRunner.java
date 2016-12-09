package ch.wisv.events.utils;

import ch.wisv.events.app.request.OrderRequest;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.event.EventStatus;
import ch.wisv.events.core.model.order.*;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.model.sales.Vendor;
import ch.wisv.events.core.repository.*;
import ch.wisv.events.core.service.order.OrderService;
import ch.wisv.events.core.service.product.SoldProductService;
import org.springframework.boot.CommandLineRunner;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * TestDataRunner.
 * <p>
 * Adds some data into the Repositories.
 */
public class TestDataRunner implements CommandLineRunner {

    private static DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    private final EventRepository eventRepository;
    private final ProductRepository productRepository;
    private final VendorRepository vendorRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final SoldProductService soldProductService;
    private final SoldProductRepository soldProductRepository;

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
    }

    @Override
    public void run(String... args) throws Exception {
        LocalDateTime today = LocalDateTime.now();
        today = today.withSecond(0).withNano(0);

        Product product;
        for (int i = 1; i < 16; i++) {
            product = new Product();
            product.setTitle("Product " + i);
            product.setCost(10.0f);
            product.setDescription(
                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque vitae lectus est. Nam ultrices sapien felis, hendrerit pulvinar tortor lobortis a. Nunc mauris est, fermentum in neque sed, consectetur aliquam justo. Etiam nec feugiat mi. Aliquam sed.");
            product.setSellStart(today.minusDays(i - 7).minusHours(1));
            product.setSellEnd(today.minusDays(i - 7).plusMinutes(1));
            product.setMaxSold(null);
            productRepository.save(product);
        }

        Event event;
        for (int i = 1; i < 16; i++) {
            event = new Event("Event " + i,
                    "Phasellus eget mauris fringilla, tincidunt enim eget, luctus massa. Suspendisse ultricies in neque " +
                            "at cursus. Duis viverra nunc in volutpat pellentesque. Ut eu finibus urna, a posuere nulla. Fusce vel nulla nibh. Curabitur gravida ante sed tellus posuere.",
                    "Lecture hall A",
                    randomNumber(50, 100),
                    null,
                    "http://placehold.it/300x300",
                    today.minusDays(i - 7).minusHours(1),
                    today.minusDays(i - 7).plusMinutes(1)
            );

            event.addProduct(productRepository.findById(i));
            event.getOptions().setPublished(EventStatus.PUBLISHED);
            eventRepository.save(event);
        }

        /*
          Sell access
         */
        Vendor vendor = new Vendor();
        vendor.setLdapGroup(LDAPGroupEnum.W3CIE);
        vendor.addEvent(eventRepository.findById(1));
        vendor.addEvent(eventRepository.findById(9));
        vendor.setStartingTime(today.minusDays(1));
        vendor.setEndingTime(today.plusDays(1));
        vendorRepository.saveAndFlush(vendor);

        Vendor vendor2 = new Vendor();
        vendor2.setLdapGroup(LDAPGroupEnum.AKCIE);
        vendor2.addEvent(eventRepository.findById(3));
        vendor2.setStartingTime(today.minusDays(1));
        vendor2.setEndingTime(today.plusDays(1));
        vendorRepository.saveAndFlush(vendor2);


        /*
          Customers
         */
        Customer customer1 = new Customer();
        customer1.setRfidToken("0001809788");
        customer1.setChUsername("svenp");
        customer1.setEmail("svenp@ch.tudelft.nl");
        customer1.setName("Sven Popping");
        customerRepository.saveAndFlush(customer1);

        Customer customer2 = new Customer();
        customer2.setRfidToken("123");
        customer2.setChUsername("svenh");
        customer2.setEmail("svenh@ch.tudelft.nl");
        customer2.setName("Sven van Hal");
        customerRepository.saveAndFlush(customer2);

        this.createSalesOrders();
        this.scanRandomOrders();
    }

    /**
     * Method createSalesOrders creates random sales orders
     * @throws Exception when
     */
    private void createSalesOrders() throws Exception {
        OrderRequest orderRequest = new OrderRequest();
        Customer customer;
        Order order;
        // Create 100 random orders
        for (int i = 0; i < 1001; i++) {
            // Add random product to order
            Map<String, Integer> products = new HashMap<>();

            products.put(productRepository.findOne(randomNumber(1 , (int) productRepository.count())).getKey(), 1);
            orderRequest.setProducts(products);
            order = orderService.create(orderRequest);

            // Add customer to order
            customer = customerRepository.findOne(randomNumber(1, (int) customerRepository.count()));
            orderService.addCustomerToOrder(order, customer);

            // Set order status to paid.
            order.setStatus(OrderStatus.PAID_CASH);
            orderRepository.save(order);
            for (Product product1 : order.getProducts()) {
                product1.setSold(product1.getSold() + 1);
                productRepository.save(product1);
            }
            soldProductService.create(order);
        }
    }

    /**
     * Method scanRandomOrders set of 25 order the status to scanned
     * @throws Exception when
     */
    private void scanRandomOrders() throws Exception {
        // Scan 25 random tickets
        SoldProduct soldProduct;
        for (int i = 0; i < 1001; i++) {
            soldProduct = soldProductRepository.findOne(randomNumber(1, (int) soldProductRepository.count()));
            soldProduct.setStatus(SoldProductStatus.SCANNED);

            soldProductService.update(soldProduct);
        }
    }

    /**
     * Method randomNumber generates random number between bounds.
     *
     * @param start of type int
     * @param end of type int
     * @return Integer
     */
    private Integer randomNumber(int start, int end) {
        Random randomGenerator = new Random();

        return randomGenerator.nextInt(end - start + 1) + start;
    }
}
