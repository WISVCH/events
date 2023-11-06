package ch.wisv.events.core.admin;

import java.time.LocalDateTime;

public interface TreasurerData {
    int getProductId();
    String getEventTitle();
    int getOrganizedBy();
    String getProductTitle();
    double getPrice();
    int getAmount();
    String getVatRate();
    LocalDateTime getPaidAt();
}
