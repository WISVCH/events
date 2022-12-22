package ch.wisv.events.core.repository;

import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderProduct;
import ch.wisv.events.core.model.order.OrderStatus;
import java.util.List;
import java.util.Optional;

import ch.wisv.events.core.admin.TreasurerData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * OrderRepository interface.
 */
public interface OrderRepository extends JpaRepository<Order, Integer> {

    /**
     * Method findOneByPublicReference find Order by public reference.
     *
     * @param publicReference of type String
     *
     * @return Optional
     */
    Optional<Order> findOneByPublicReference(String publicReference);

    /**
     * Find Order by Customer.
     *
     * @param owner of type Customer
     *
     * @return List
     */
    List<Order> findAllByOwnerOrderByCreatedAtDesc(Customer owner);

    /**
     * Find Reservation Orders by Customer.
     *
     * @param owner  of type Customer
     * @param status of type OrderStatus
     *
     * @return List of Orders
     */
    List<Order> findAllByOwnerAndStatusOrderByCreatedAt(Customer owner, OrderStatus status);

    /**
     * Find all Order by a Customer that have a given status.
     *
     * @param owner  of type Customer
     * @param status of type OrderStatus
     *
     * @return List
     */
    List<Order> findAllByOwnerAndStatus(Customer owner, OrderStatus status);

    /**
     * Find all Order with a certain status.
     *
     * @param status of type OrderStatus
     *
     * @return List of Orders.
     */
    List<Order> findAllByStatus(OrderStatus status);

    /**
     * Find one by CH Payments Reference.
     *
     * @param chPaymentsReference of type String
     *
     * @return Optional of Order
     */
    Optional<Order> findOneByChPaymentsReference(String chPaymentsReference);

    /**
     * Find order associated to orderProduct.
     *
     * @param orderProduct of type OrderProduct
     *
     * @return List of Order
     */
    List<Order> findAllByOrderProducts(OrderProduct orderProduct);

    @Query(value =
            "SELECT B.TITLE AS title,B.PRICE AS price,B.AMOUNT AS amount,O.PAID_AT AS paidAt " +
                    "FROM " +
                    "( SELECT * " +
                    "FROM " +
                    "( SELECT P.COST," +
                    "P.SOLD," +
                    "P.TITLE," +
                    "OP.AMOUNT," +
                    "OP.PRICE," +
                    "OP.ID AS OOPID " +
                    "FROM PRODUCT P " +
                    "INNER JOIN ORDER_PRODUCT OP ON P.ID = OP.PRODUCT_ID " +
                    "WHERE OP.PRICE > 0 ) A " +
                    "INNER JOIN ORDERS_ORDER_PRODUCTS OOP ON A.OOPID = OOP.ORDER_PRODUCTS_ID) B " +
                    "INNER JOIN ORDERS O ON B.ORDER_ID = O.ID " +
                    "WHERE O.STATUS = 5 " +
                    "AND (O.PAYMENT_METHOD = 2 " +
                    "OR O.PAYMENT_METHOD = 3) ORDER BY paidAt DESC LIMIT 200", nativeQuery = true)
    List<TreasurerData> findallPayments();
}
