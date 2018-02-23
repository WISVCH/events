package ch.wisv.events.core.model.customer;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;
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
@Data
public class Customer {

    /**
     * Field id of the customer.
     */
    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    private Integer id;

    /**
     * Field key UUID of the customer.
     */
    @Column(unique = true)
    private String key;

    /**
     * Field sub is the OIDC auth unique ID.
     */
    @Column(unique = true)
    private String sub;

    /**
     * Field name of the customer.
     */
    private String name;

    /**
     * Field email of the customer.
     */
    @Column(unique = true)
    private String email;

    /**
     * Field chUsername of the customer, this will be the ldap username.
     */
    @Column(unique = true)
    private String chUsername;

    /**
     * Field rfidToken of the customers pass.
     */
    private String rfidToken;

    /**
     * Field createdAt when th
     */
    private LocalDateTime createdAt;

    /**
     * Constructor Customer creates a new Customer instance.
     */
    public Customer() {
        this.key = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Constructor Customer creates a new Customer instance.
     *
     * @param sub        of type String
     * @param name       of type String
     * @param email      of type String
     * @param chUsername of type String
     * @param rfidToken  of type String
     */
    public Customer(String sub, String name, String email, String chUsername, String rfidToken) {
        this();
        this.sub = sub;
        this.name = name;
        this.email = email;
        this.chUsername = chUsername;
        this.rfidToken = rfidToken;
    }
}
