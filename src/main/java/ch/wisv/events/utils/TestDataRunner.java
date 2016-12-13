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
import org.fluttercode.datafactory.impl.DataFactory;
import org.springframework.boot.CommandLineRunner;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * TestDataRunner.
 * <p>
 * Adds some data into the Repositories.
 */
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


    private final LocalDateTime today;

    private final int
            events = 15,
            products = 15,
            orders = 250,
            maxProductsPerOrder = 5,
            scanned = 800,
            customer = 25;


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
        LocalDateTime tm = LocalDateTime.now();
        this.today = tm.withSecond(0).withNano(0);
    }

    @Override
    public void run(String... args) throws Exception {
        this.createProducts(today);
        this.createEvents(today);

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

        this.createRandomCustomers();
        this.createSalesOrders();
        this.scanRandomOrders();
    }

    private void createProducts(LocalDateTime today) throws Exception {
        Product product;
        for (int i = 1; i < this.products + 1; i++) {
            String first = this.df.getRandomWord(5, 12);

            product = new Product();
            product.setTitle(first.substring(0, 1).toUpperCase() + first.substring(1) + " " + this.df.getRandomWord(5, 12));
            product.setCost(this.df.getNumberUpTo(10));
            product.setDescription(this.df.getRandomText(30, 150));
            product.setSellStart(today.minusDays(i - 7).minusHours(1));
            product.setSellEnd(today.minusDays(i - 7).plusMinutes(1));
            product.setMaxSold(null);
            productRepository.save(product);
        }
    }

    private void createEvents(LocalDateTime today) throws Exception {
        Event event;
        for (int i = 1; i < this.events + 1; i++) {
            String first = this.df.getRandomWord(5, 12);
            event = new Event(first.substring(0, 1).toUpperCase() + first.substring(1) + " " + this.df.getRandomWord
                    (5, 12),
                    this.df.getRandomText(30, 50),
                    "Lecture hall " + df.getRandomChars(1).toUpperCase(),
                    randomNumber(20, 80),
                    null,
                    "http://placehold.it/300x300",
                    today.minusDays(i - 7).minusHours(1),
                    today.minusDays(i - 7).plusMinutes(1)
            );

            event.addProduct(productRepository.findById(i));
            event.getOptions().setPublished(EventStatus.PUBLISHED);
            eventRepository.save(event);
        }
    }

    /**
     * Method createSalesOrders creates random sales orders
     *
     * @throws Exception when
     */
    private void createSalesOrders() throws Exception {
        OrderRequest orderRequest = new OrderRequest();
        Customer customer;
        Order order;
        // Create 100 random orders
        for (int i = 0; i < this.orders; i++) {
            // Add random product to order
            Map<String, Integer> products = new HashMap<>();

            for (Integer integer = 0; integer < randomNumber(1, this.maxProductsPerOrder); integer++) {
                products.put(productRepository.findOne(randomNumber(1, (int) productRepository.count())).getKey(), 1);
            }
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
            order.getProducts().forEach(x -> {
                List<Event> events = eventRepository.findAllByProductsId(x.getId());
                events.forEach(y -> {
                    y.setSold(y.getProducts().stream().mapToInt(Product::getSold).sum());
                    eventRepository.save(y);
                });
            });

            order.setCreationDate(today.minusDays(df.getNumberBetween(0, 20)));

            soldProductService.create(order);
        }
    }

    /**
     * Method scanRandomOrders set of 25 order the status to scanned
     *
     * @throws Exception when
     */
    private void scanRandomOrders() throws Exception {
        SoldProduct soldProduct;
        for (int i = 0; i < this.scanned; i++) {
            soldProduct = soldProductRepository.findOne(randomNumber(1, (int) soldProductRepository.count()));
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
        for (int i = 0; i < this.customer; i++) {
            Customer customer = new Customer();
            String firstName = this.df.getFirstName();
            String lastName = this.df.getLastName();
            customer.setName(firstName + " " + lastName);
            customer.setEmail(this.df.getEmailAddress());
            customer.setChUsername(firstName.toLowerCase() + String.valueOf(lastName.charAt(0)).toLowerCase());
            customer.setRfidToken(this.df.getNumberText(10));

            customerRepository.save(customer);
        }
    }

    /**
     * Method randomNumber generates random number between bounds.
     *
     * @param start of type int
     * @param end   of type int
     * @return Integer
     */
    private Integer randomNumber(int start, int end) {
        Random randomGenerator = new Random();

        return randomGenerator.nextInt(end - start + 1) + start;
    }
}
