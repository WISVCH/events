package ch.wisv.events.event.model;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created by svenp on 11-10-2016.
 */
@Entity
@Data
public class Ticket {

    public final static String TIME_FORMAT = "dd/MM/yyyy HH:mm";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String title;

    @Lob
    private String description;

    private float cost;

    private int maxSold;

    @Column(name = "eventStart")
    @DateTimeFormat(pattern = TIME_FORMAT)
    private LocalDateTime start;

    @Column(name = "eventEnd")
    @DateTimeFormat(pattern = TIME_FORMAT)
    private LocalDateTime end;

}
