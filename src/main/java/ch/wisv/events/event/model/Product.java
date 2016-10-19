package ch.wisv.events.event.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Created by svenp on 11-10-2016.
 */
@Entity
public class Product {

    public final static String TIME_FORMAT = "dd/MM/yyyy HH:mm";

    @Id
    @GeneratedValue
    @Getter
    public long id;

    @Column(unique = true)
    @Getter
    @Setter
    public String title;

    @Getter
    @Setter
    public String key;

    @Lob
    @Getter
    @Setter
    public String description;

    @Getter
    @Setter
    public float cost;

    @Getter
    @Setter
    public int maxSold;

    @DateTimeFormat(pattern = TIME_FORMAT)
    @Getter
    @Setter
    public LocalDateTime start;

    @DateTimeFormat(pattern = TIME_FORMAT)
    @Getter
    @Setter
    public LocalDateTime end;

    public Product() {
        this.key = UUID.randomUUID().toString();
    }
}
