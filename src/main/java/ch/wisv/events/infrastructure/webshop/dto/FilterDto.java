package ch.wisv.events.infrastructure.webshop.dto;

import ch.wisv.events.domain.model.event.EventCategory;
import java.util.List;
import lombok.Data;

/**
 * FilterDto.
 */
@Data
public class FilterDto {

    /**
     * Filter query
     */
    public String search;

    /**
     * List of Event categories.
     */
    public List<EventCategory> categories;
}
