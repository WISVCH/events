package ch.wisv.events.core.repository;

import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderProduct;
import ch.wisv.events.core.model.order.OrderStatus;
import java.util.List;
import java.util.Optional;
import java.util.Collection;

import ch.wisv.events.core.admin.TreasurerData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
            "SELECT B.TITLE AS productTitle,B.PRICE AS price,B.AMOUNT AS amount,B.VAT_RATE AS vatRate, O.PAID_AT AS paidAt " +
                    "FROM " +
                    "( SELECT * " +
                    "FROM " +
                    "( SELECT P.COST," +
                    "P.SOLD," +
                    "P.TITLE," +
                    "OP.AMOUNT," +
                    "OP.PRICE," +
                    "OP.VAT_RATE," +
                    "OP.ID AS OOPID " +
                    "FROM PRODUCT P " +
                    "INNER JOIN ORDER_PRODUCT OP ON P.ID = OP.PRODUCT_ID " +
                    "WHERE OP.PRICE > 0 ) A " +
                    "INNER JOIN ORDERS_ORDER_PRODUCTS OOP ON A.OOPID = OOP.ORDER_PRODUCTS_ID) B " +
                    "INNER JOIN ORDERS O ON B.ORDER_ID = O.ID " +
                    "WHERE O.STATUS = 5 " +
                    "AND (O.PAYMENT_METHOD = 2 " +
                    "OR O.PAYMENT_METHOD = 3)", nativeQuery = true)
    List<TreasurerData> findallPayments();


    /**
     * Query for all orders with status PAID.
     * 
     * Also joins the product and event tables to get the information
     * about the products (and corresponding event) that were bought in the order
     * 
     * @param month of type Integer. Filter query on month that the order was paid in (1-12)
     * @param year of type Integer. Filter query on year that the order was paid in
     * @param paymentMethods of type Collection<Integer>. Payment methods to include in the query
     * @param includeFreeProducts of type boolean. Whether to include products with price 0 in the query
     *
     * @return List of TreasurerData. Contains the following fields:
     * - productId
     * - eventTitle
     * - organizedBy
     * - productTitle
     * - price
     * - amount
     * - vatRate
     * 
     */
    @Query(value ="""
            SELECT
                P.ID AS productId,
                E.TITLE AS eventTitle,
                E.ORGANIZED_BY AS organizedBy,
                P.TITLE AS productTitle,
                B.OP_PRICE AS price,
                B.OP_AMOUNT AS amount,
                P.VAT_RATE AS vatRate
            FROM (
                SELECT OP.PRICE AS OP_PRICE, OP.AMOUNT AS OP_AMOUNT, OP.PRODUCT_ID
                FROM (
                    SELECT OOP.ORDER_PRODUCTS_ID AS OPID
                    FROM ORDERS O
                    INNER JOIN ORDERS_ORDER_PRODUCTS OOP ON O.ID = OOP.ORDER_ID
                    WHERE O.STATUS = 5
                        AND EXTRACT(YEAR FROM O.PAID_AT) = :year
                        AND EXTRACT(MONTH FROM O.PAID_AT) = :month
                        AND O.PAYMENT_METHOD IN :paymentMethods
                    ) A
                INNER JOIN ORDER_PRODUCT OP ON A.OPID = OP.ID
                WHERE (:includeFreeProducts OR OP.PRICE > 0)
            ) B
            INNER JOIN PRODUCT P ON B.PRODUCT_ID = P.ID
            LEFT JOIN EVENT_PRODUCTS EP ON P.ID = EP.PRODUCTS_ID
            LEFT JOIN EVENT E ON EP.EVENT_ID = E.ID""", nativeQuery = true)
    List<TreasurerData> findallPaymentsByMonth(@Param("month") Integer month, @Param("year") Integer year, @Param("paymentMethods") Collection<Integer> paymentMethods, @Param("includeFreeProducts") boolean includeFreeProducts);
}
