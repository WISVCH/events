package ch.wisv.events.core.model.order;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

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
@Entity
@EqualsAndHashCode
public class Customer {

    /**
     * Field id of the customer.
     */
    @Id
    @GeneratedValue
    @Getter
    private Integer id;

    /**
     * Field key UUID of the customer.
     */
    @Getter
    @Setter
    private String key;

    /**
     * Field name of the customer.
     */
    @Getter
    @Setter
    private String name;

    /**
     * Field email of the customer.
     */
    @Getter
    @Setter
    private String email;

    /**
     * Field chUsername of the customer, this will be the ldap username.
     */
    @Getter
    @Setter
    private String chUsername;

    /**
     * Field rfidToken of the customers pass.
     */
    @Getter
    @Setter
    private String rfidToken;

    /**
     * Constructor Customer creates a new Customer instance.
     */
    public Customer() {
        this.key = UUID.randomUUID().toString();
    }

    /**
     * Constructor Customer creates a new Customer instance.
     *
     * @param name       of type String
     * @param email      of type String
     * @param chUsername of type String
     * @param rfidToken  of type String
     */
    public Customer(String name, String email, String chUsername, String rfidToken) {
        this();
        this.name = name;
        this.email = email;
        this.chUsername = chUsername;
        this.rfidToken = rfidToken;
    }
}
