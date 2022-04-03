package ch.wisv.events.core.repository;

import ch.wisv.events.admin.model.TreasurerData;
import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderProduct;
import ch.wisv.events.core.model.order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * OrderRepository interface.
 */
public interface TreasurerRepository extends JpaRepository<TreasurerData, Integer> {

    @Query("" +
            "SELECT B.TITLE," +
            "B.PRICE," +
            "B.AMOUNT," +
            "O.PAID_AT " +
            "FROM " +
            "(SELECT * " +
            "FROM " +
            "(SELECT P.COST," +
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
            "OR O.PAYMENT_METHOD = 3);")
    List<TreasurerData> findallPayments();
}
