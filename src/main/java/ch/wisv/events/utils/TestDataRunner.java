package ch.wisv.events.utils;

import ch.wisv.events.data.model.event.Event;
import ch.wisv.events.data.model.event.EventStatus;
import ch.wisv.events.data.model.order.Customer;
import ch.wisv.events.data.model.order.Order;
import ch.wisv.events.data.model.order.OrderStatus;
import ch.wisv.events.data.model.product.Product;
import ch.wisv.events.data.model.sales.LDAPGroupEnum;
import ch.wisv.events.data.model.sales.Vendor;
import ch.wisv.events.repository.event.EventRepository;
import ch.wisv.events.repository.order.CustomerRepository;
import ch.wisv.events.repository.order.OrderRepository;
import ch.wisv.events.repository.product.ProductRepository;
import ch.wisv.events.repository.sales.VendorRepository;
import org.springframework.boot.CommandLineRunner;

import java.time.LocalDateTime;
import java.time.Month;

/**
 * TestDataRunner.
 * <p>
 * Adds some data into the Repositories.
 */
public class TestDataRunner implements CommandLineRunner {

    private final EventRepository eventRepository;
    private final ProductRepository productRepository;
    private final VendorRepository vendorRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;

    public TestDataRunner(EventRepository eventRepository, ProductRepository productRepository,
                          VendorRepository vendorRepository, CustomerRepository customerRepository,
                          OrderRepository orderRepository) {
        this.eventRepository = eventRepository;
        this.productRepository = productRepository;
        this.vendorRepository = vendorRepository;
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Product product = null;
        for (int i = 1; i < 10; i++) {
            product = new Product();
            product.setTitle("Product " + i);
            product.setCost(10.0f);
            product.setDescription(
                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque vitae lectus est. Nam ultrices sapien felis, hendrerit pulvinar tortor lobortis a. Nunc mauris est, fermentum in neque sed, consectetur aliquam justo. Etiam nec feugiat mi. Aliquam sed.");
            product.setSellStart(LocalDateTime.of(2015, Month.DECEMBER, Math.floorMod(i, 11) + 1, 12, 45));
            product.setSellEnd(LocalDateTime.of(2014 + i, Month.DECEMBER, Math.floorMod(i, 11) + 1, 13, 30));
            product.setMaxSold(i);
            productRepository.save(product);
        }

        Event event = null;
        for (int i = 1; i < 10; i++) {
            event = new Event("Event " + i,
                    "Phasellus eget mauris fringilla, tincidunt enim eget, luctus massa. Suspendisse ultricies in neque " +
                            "at cursus. Duis viverra nunc in volutpat pellentesque. Ut eu finibus urna, a posuere nulla. Fusce vel nulla nibh. Curabitur gravida ante sed tellus posuere.",
                    "Lecture hall A",
                    8,
                    10,
                    "http://placehold.it/300x300",
                    LocalDateTime.of(2015, Month.DECEMBER, i, 12, 45),
                    LocalDateTime.of(2014 + i, Month.DECEMBER, i, 13, 30)
            );

            event.addProduct(productRepository.findById((long) i));
            event.getOptions().setPublished(EventStatus.PUBLISHED);
            eventRepository.save(event);
        }

        /*
          Sell access
         */
        Vendor vendor = new Vendor();
        vendor.setLdapGroup(LDAPGroupEnum.W3CIE);
        vendor.addEvent(eventRepository.findById(1L));
        vendor.addEvent(eventRepository.findById(9L));
        vendor.setStartingTime(LocalDateTime.of(2015, Month.DECEMBER, 1, 12, 45));
        vendor.setEndingTime(LocalDateTime.of(2017, Month.DECEMBER, 1, 13, 30));
        vendorRepository.saveAndFlush(vendor);

        Vendor vendor2 = new Vendor();
        vendor2.setLdapGroup(LDAPGroupEnum.AKCIE);
        vendor2.addEvent(eventRepository.findById(3L));
        vendor2.setStartingTime(LocalDateTime.of(2015, Month.DECEMBER, 1, 12, 45));
        vendor2.setEndingTime(LocalDateTime.of(2017, Month.DECEMBER, 1, 13, 30));
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

        /*
          Orders
         */
        Order order1 = new Order();
        order1.setCustomer(customer1);
        order1.addProduct(product);
        order1.setStatus(OrderStatus.PAID_CASH);
        orderRepository.saveAndFlush(order1);

        Order order2 = new Order();
        order2.setCustomer(customer1);
        order2.addProduct(product);
        order2.setStatus(OrderStatus.CANCELLED);
        orderRepository.saveAndFlush(order2);

        Order order3 = new Order();
        order3.setCustomer(customer2);
        order3.addProduct(product);
        order3.setStatus(OrderStatus.REJECTED);
        orderRepository.saveAndFlush(order3);

        Order order4 = new Order();
        order4.setCustomer(customer2);
        order4.addProduct(product);
        order4.setStatus(OrderStatus.PAID_CASH);
        orderRepository.saveAndFlush(order4);
    }

}
