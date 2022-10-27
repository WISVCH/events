package ch.wisv.events.core.admin;

import java.time.LocalDateTime;

public interface TreasurerData {
    String getTitle();
    double getPrice();
    int getAmount();
    LocalDateTime getPaidAt();
}
