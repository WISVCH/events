package ch.wisv.events.event.model;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Created by svenp on 11-10-2016.
 */
@Entity
@Data
public class Ticket {

    public final static String TIME_FORMAT = "dd/MM/yyyy HH:mm";

    @Id
    @GeneratedValue
    public long id;

    @Column(unique = true)
    public String title;

    public String key;

    @Lob
    public String description;

    public float cost;

    public int maxSold;

    @DateTimeFormat(pattern = TIME_FORMAT)
    public LocalDateTime start;

    @DateTimeFormat(pattern = TIME_FORMAT)
    public LocalDateTime end;

    public Ticket() {
        this.key = UUID.randomUUID().toString();
    }
}
