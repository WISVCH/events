package ch.wisv.events.data.model;

import ch.wisv.events.data.model.product.Product;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Set;

/**
 * Created by sven on 17/10/2016.
 */
@Entity
@Data
public class Order {

    @Id
    @GeneratedValue
    public Long id;

    String reference;

    @OneToMany
    Set<Product> products;

}
