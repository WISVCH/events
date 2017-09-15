package ch.wisv.events.core.model.sales;

import ch.wisv.events.core.model.order.OrderStatus;
import lombok.Getter;

import java.util.Objects;

import static java.util.Arrays.stream;

/**
 * Copyright (c) 2016  W.I.S.V. 'Christiaan Huygens'
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
public enum PaymentOption {

    CASH(1, "Cash", "cash", "success", OrderStatus.PAID_CASH),
    PIN(1, "Pin", "pin", "success", OrderStatus.PAID_PIN),
    CANCEL(2, "Cancel", "cancel", "danger", OrderStatus.CANCELLED);

    /**
     * Field id
     */
    @Getter
    private final int id;

    /**
     * Field displayName
     */
    @Getter
    private final String displayName;

    /**
     * Field value the value of the POST, used for the redirect of the payment method.
     */
    @Getter
    private final String value;

    /**
     * Field displayClass display class of the button
     */
    @Getter
    private final String displayClass;

    /**
     * Field orderStatus status of the Order
     */
    @Getter
    private final OrderStatus orderStatus;

    /**
     * Constructor PaymentOption creates a new PaymentOption instance.
     *
     * @param id           of type int
     * @param displayName  of type String
     * @param value        of type String
     * @param displayClass of type String
     * @param orderStatus  of type OrderStatus
     */
    PaymentOption(int id, String displayName, String value, String displayClass, OrderStatus orderStatus) {
        this.id = id;
        this.displayName = displayName;
        this.value = value;
        this.displayClass = displayClass;
        this.orderStatus = orderStatus;
    }

    /**
     * Method getStatusByValue returns a order status by payment name. If order status not found it will return a
     * Rejected order status.
     *
     * @param payment of type String
     * @return OrderStatus
     */
    public static OrderStatus getStatusByValue(String payment) {
        PaymentOption tmp = stream(PaymentOption.values()).filter(option -> Objects
                .equals(option.getValue(), payment)).findFirst().orElse(null);

        return (tmp != null) ? tmp.getOrderStatus() : OrderStatus.REJECTED;
    }

}
