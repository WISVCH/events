package ch.wisv.events.sales.model;

import lombok.Data;

@Data
public class ScanDto {

    private String productTitle;

    private String ownerName;

    public ScanDto(String productTitle, String ownerName) {
        this.productTitle = productTitle;
        this.ownerName = ownerName;
    }
}