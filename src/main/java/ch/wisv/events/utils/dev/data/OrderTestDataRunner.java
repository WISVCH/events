package ch.wisv.events.utils.dev.data;

import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.repository.CustomerRepository;
import ch.wisv.events.core.repository.OrderRepository;
import ch.wisv.events.core.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import org.json.simple.JSONObject;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@Order(value = 6)
public class OrderTestDataRunner extends TestDataRunner {

    /** OrderRepository. */
    private final OrderRepository orderRepository;

    /** CustomerRepository. */
    private final CustomerRepository customerRepository;

    /** ProductRepository. */
    private final ProductRepository productRepository;

    /**
     * Constructor EventTestDataRunner creates a new EventTestDataRunner instance.
     *
     * @param orderRepository    of type OrderRepository
     * @param customerRepository of type CustomerRepository
     * @param productRepository  of type ProductRepository
     */
    public OrderTestDataRunner(
            OrderRepository orderRepository, CustomerRepository customerRepository, ProductRepository productRepository
    ) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;

        this.setJsonFileName("orders.json");
    }

    /**
     * Method loop.
     *
     * @param jsonObject of type JSONObject
     */
    @Override
    protected void loop(JSONObject jsonObject) {
        ch.wisv.events.core.model.order.Order order = this.createOrder(jsonObject);

        this.orderRepository.save(order);

        //        order.getProducts().forEach(product -> {
        //            SoldProduct soldProduct = new SoldProduct(
        //                    product,
        //                    order,
        //                    order.getCustomer()
        //            );
        //            soldProduct.setUniqueCode(RandomStringUtils.randomNumeric(6));
        //            soldProduct.setStatus((RandomUtils.nextFloat(0, 1) > 0.4) ? SoldProductStatus.SCANNED : SoldProductStatus.OPEN);
        //
        //            this.ticketReposiotorysaveAndFlush(soldProduct);
        //        });
    }

    /**
     * Method createProduct ...
     *
     * @param jsonObject of type JSONObject
     *
     * @return Product
     */
    private ch.wisv.events.core.model.order.Order createOrder(JSONObject jsonObject) {
        String customerRfid = (String) jsonObject.get("customerRfid");
        Optional<Customer> customer = this.customerRepository.findByRfidToken(customerRfid);

        if (customer.isPresent()) {
            ch.wisv.events.core.model.order.Order order = new ch.wisv.events.core.model.order.Order();
            order.setOwner(customer.get());

            List<Product> allProduct = this.productRepository.findAll();
            //            order.addProduct(allProduct.get(df.getNumberBetween(0, allProduct.size())));
            //            order.getProducts().forEach(x -> {
            //                x.setSold(x.getSold() + 1);
            //                this.productRepository.saveAndFlush(x);
            //            });
            //            order.setStatus(OrderStatus.valueOf((String) jsonObject.get("orderStatus")));
            //            order.setAmount(order.getProducts().stream().mapToDouble(Product::getCost).sum());

            return order;
        }

        return new ch.wisv.events.core.model.order.Order();
    }
}
