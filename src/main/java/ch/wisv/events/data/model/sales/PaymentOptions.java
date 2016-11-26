package ch.wisv.events.data.model.sales;

import ch.wisv.events.data.model.order.OrderStatus;

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
public enum PaymentOptions {

    CASH(1, "Cash", "cash", "success", OrderStatus.PAID_CASH),
    CANCEL(1, "Cancel", "cancel", "danger", OrderStatus.CANCELLED);

    private final int id;

    private final String displayName;

    private final String value;

    private final String displayClass;

    private final OrderStatus orderStatus;

    PaymentOptions(int id, String displayName, String value, String displayClass, OrderStatus orderStatus) {
        this.id = id;
        this.displayName = displayName;
        this.value = value;
        this.displayClass = displayClass;
        this.orderStatus = orderStatus;
    }

    public static OrderStatus getStatusByValue(String payment) {
        PaymentOptions tmp = stream(PaymentOptions.values()).filter(option -> Objects
                .equals(option.getValue(), payment)).findFirst().orElse(null);
        return (tmp != null) ? tmp.getOrderStatus() : OrderStatus.REJECTED;
    }

    public int getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getValue() {
        return value;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public String getDisplayClass() {
        return displayClass;
    }
}
