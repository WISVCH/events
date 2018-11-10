package ch.wisv.events.domain.model;

import ch.wisv.events.domain.converter.ZonedDateTimeConverter;
import java.time.ZonedDateTime;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * AbstractModel entity.
 */
@Entity
@Data
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class AbstractModel {

    /**
     * Item id of the model.
     */
    @Id
    @GeneratedValue
    public Integer itemId;

    /**
     * Public reference of the model.
     */
    @NotEmpty
    @Column(unique = true)
    public String publicReference;

    /**
     * Timestamp at which the model has been created.
     */
    @Convert(converter = ZonedDateTimeConverter.class)
    @NotNull
    public ZonedDateTime createdAt;

    /**
     * Timestamp at which the model has been updated.
     */
    @Convert(converter = ZonedDateTimeConverter.class)
    @NotNull
    public ZonedDateTime updatedAt;

    /**
     * AbstractModel constructor.
     */
    protected AbstractModel() {
        this.publicReference = UUID.randomUUID().toString();
        this.createdAt = ZonedDateTime.now();
        this.updatedAt = ZonedDateTime.now();
    }
}

