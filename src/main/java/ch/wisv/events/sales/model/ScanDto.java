package ch.wisv.events.sales.model;

import lombok.Data;

@Data
public class ScanDto {

    /**
     * Title of the product scanned
     */
    private String productTitle;

    /**
     * The name of the ticket owner
     */
    private String ownerName;

    public ScanDto(String productTitle, String ownerName) {
        this.productTitle = productTitle;
        this.ownerName = ownerName;
    }
}
