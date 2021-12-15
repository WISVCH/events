package ch.wisv.events.sales.model;

import ch.wisv.events.core.model.ticket.TicketStatus;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ScanDto {

    @NotNull
    private String productTitle;

    @NotNull
    private String ownerName;

    public ScanDto(String productTitle, String ownerName) {
        this.productTitle = productTitle;
        this.ownerName = ownerName;
    }
}