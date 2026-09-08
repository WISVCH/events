package ch.wisv.events.utils.dev.data;

import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.event.EventCategory;
import ch.wisv.events.core.model.event.EventStatus;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderProduct;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.model.order.PaymentMethod;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.model.ticket.Ticket;
import ch.wisv.events.core.model.ticket.TicketStatus;
import ch.wisv.events.core.model.webhook.Webhook;
import ch.wisv.events.core.model.webhook.WebhookTrigger;
import ch.wisv.events.core.repository.CustomerRepository;
import ch.wisv.events.core.repository.EventRepository;
import ch.wisv.events.core.repository.OrderProductRepository;
import ch.wisv.events.core.repository.OrderRepository;
import ch.wisv.events.core.repository.ProductRepository;
import ch.wisv.events.core.repository.TicketRepository;
import ch.wisv.events.core.repository.WebhookRepository;
import ch.wisv.events.core.repository.document.DocumentRepository;
import ch.wisv.events.core.util.VatRate;
import ch.wisv.events.utils.LdapGroup;
import jakarta.persistence.EntityManager;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Profile({"dev", "devcontainer"})
@RequiredArgsConstructor
@Slf4j
public class DevDataSeeder {

    private final CustomerRepository customerRepository;
    private final DocumentRepository documentRepository;
    private final EventRepository eventRepository;
    private final OrderProductRepository orderProductRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final TicketRepository ticketRepository;
    private final WebhookRepository webhookRepository;
    private final ResourceLoader resourceLoader;
    private final EntityManager entityManager;

    @Transactional
    public void seedIfDatabaseIsEmpty() {
        if (!isDatabaseEmpty()) {
            log.info("Skipping development data seed because the database already contains data");
            return;
        }

        log.info("Seeding development data");

        Map<String, Product> products = seedProducts();
        seedEvents(products);
        Map<String, Customer> customers = seedCustomers();
        seedWebhooks();
        seedOrders(products, customers);

        entityManager.flush();
        log.info("Finished seeding development data");
    }

    private boolean isDatabaseEmpty() {
        return customerRepository.count() == 0
                && documentRepository.count() == 0
                && eventRepository.count() == 0
                && orderProductRepository.count() == 0
                && orderRepository.count() == 0
                && productRepository.count() == 0
                && ticketRepository.count() == 0
                && webhookRepository.count() == 0;
    }

    private Map<String, Product> seedProducts() {
        Map<String, Product> products = new HashMap<>();
        List<JSONObject> fixtures = readFixture("products.json");

        for (JSONObject fixture : fixtures) {
            LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
            Product product = new Product(
                    string(fixture, "title"),
                    string(fixture, "description"),
                    number(fixture, "cost").doubleValue(),
                    VatRate.valueOf(string(fixture, "vatRate")),
                    number(fixture, "maxSold").intValue(),
                    now.plusHours(numberOrDefault(fixture, "sellStartOffsetHours", -1).longValue()),
                    now.plusHours(numberOrDefault(fixture, "sellEndOffsetHours", 240).longValue())
            );
            product.setKey(string(fixture, "key"));
            product.setMaxSoldPerCustomer(number(fixture, "maxSoldPerCustomer").intValue());
            product.setChOnly(booleanOrDefault(fixture, "chOnly", false));
            productRepository.save(product);
            products.put(product.getKey(), product);
        }

        for (JSONObject fixture : fixtures) {
            String parentProductKey = optionalString(fixture, "parentProductKey");
            if (parentProductKey != null) {
                Product product = required(products, string(fixture, "key"), "product");
                product.setParentProduct(required(products, parentProductKey, "parent product"));
            }
        }

        return products;
    }

    private void seedEvents(Map<String, Product> products) {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        for (JSONObject fixture : readFixture("events.json")) {
            LocalDateTime start = now.plusHours(number(fixture, "startOffsetHours").longValue());
            Event event = new Event(
                    string(fixture, "title"),
                    string(fixture, "description"),
                    string(fixture, "location"),
                    number(fixture, "target").intValue(),
                    number(fixture, "maxSold").intValue(),
                    string(fixture, "imageUrl"),
                    start,
                    start.plusHours(number(fixture, "durationHours").longValue()),
                    string(fixture, "shortDescription")
            );
            event.setKey(string(fixture, "key"));
            event.setPublished(EventStatus.PUBLISHED);
            event.setOrganizedBy(LdapGroup.FLITCIE);
            event.setCategories(List.of(EventCategory.CAREER));
            eventRepository.save(event);

            for (Object productKey : array(fixture, "productKeys")) {
                Product product = required(products, (String) productKey, "event product");
                event.addProduct(product);
                product.setLinked(true);
            }
        }
    }

    private Map<String, Customer> seedCustomers() {
        Map<String, Customer> customers = new HashMap<>();
        for (JSONObject fixture : readFixture("customers.json")) {
            Customer customer = new Customer(
                    string(fixture, "sub"),
                    string(fixture, "name"),
                    string(fixture, "email"),
                    string(fixture, "rfidToken")
            );
            customer.setVerifiedChMember(booleanOrDefault(fixture, "verifiedChMember", false));
            customerRepository.save(customer);
            customers.put(customer.getRfidToken(), customer);
        }
        return customers;
    }

    private void seedWebhooks() {
        for (JSONObject fixture : readFixture("webhooks.json")) {
            Webhook webhook = new Webhook();
            webhook.setLdapGroup(LdapGroup.valueOf(string(fixture, "ldapGroup")));
            webhook.setPayloadUrl(string(fixture, "payloadUrl"));
            webhook.setActive(true);
            webhook.setSecret("secret");
            webhook.setWebhookTriggers(array(fixture, "triggers").stream()
                    .map(trigger -> WebhookTrigger.valueOf((String) trigger))
                    .toList());
            webhookRepository.save(webhook);
        }
    }

    private void seedOrders(Map<String, Product> products, Map<String, Customer> customers) {
        for (JSONObject fixture : readFixture("orders.json")) {
            Customer customer = required(customers, string(fixture, "customerRfid"), "order customer");
            Product product = required(products, string(fixture, "productKey"), "order product");

            OrderProduct orderProduct = new OrderProduct(product, product.getCost(), 1L);
            orderProductRepository.save(orderProduct);

            Order order = new Order();
            order.setOwner(customer);
            order.setCreatedBy(string(fixture, "createdBy"));
            order.setStatus(OrderStatus.valueOf(string(fixture, "orderStatus")));
            order.setPaymentMethod(PaymentMethod.valueOf(string(fixture, "paymentMethod")));
            order.setTicketCreated(true);
            order.setPaidAt(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
            order.addOrderProduct(orderProduct);
            order = orderRepository.save(order);

            product.increaseSold(1);
            Ticket ticket = new Ticket(order, customer, product, string(fixture, "ticketCode"));
            ticket.setStatus(TicketStatus.valueOf(string(fixture, "ticketStatus")));
            ticketRepository.save(ticket);
        }
    }

    @SuppressWarnings("unchecked")
    private List<JSONObject> readFixture(String fileName) {
        Resource resource = resourceLoader.getResource("classpath:dev/data/" + fileName);
        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            return (List<JSONObject>) new JSONParser().parse(reader);
        } catch (IOException | ParseException exception) {
            throw new IllegalStateException("Could not read development data fixture " + fileName, exception);
        }
    }

    private static JSONArray array(JSONObject fixture, String field) {
        Object value = fixture.get(field);
        if (value instanceof JSONArray array) {
            return array;
        }
        throw new IllegalStateException("Missing array field '" + field + "' in development data fixture");
    }

    private static Number number(JSONObject fixture, String field) {
        Object value = fixture.get(field);
        if (value instanceof Number number) {
            return number;
        }
        throw new IllegalStateException("Missing number field '" + field + "' in development data fixture");
    }

    private static Number numberOrDefault(JSONObject fixture, String field, Number defaultValue) {
        Object value = fixture.get(field);
        return value instanceof Number number ? number : defaultValue;
    }

    private static boolean booleanOrDefault(JSONObject fixture, String field, boolean defaultValue) {
        Object value = fixture.get(field);
        return value instanceof Boolean booleanValue ? booleanValue : defaultValue;
    }

    private static String string(JSONObject fixture, String field) {
        Object value = fixture.get(field);
        if (value instanceof String string) {
            return string;
        }
        throw new IllegalStateException("Missing string field '" + field + "' in development data fixture");
    }

    private static String optionalString(JSONObject fixture, String field) {
        Object value = fixture.get(field);
        return value instanceof String string ? string : null;
    }

    private static <T> T required(Map<String, T> values, String key, String description) {
        T value = values.get(key);
        if (value == null) {
            throw new IllegalStateException("Unknown " + description + " key in development data fixture: " + key);
        }
        return value;
    }
}
