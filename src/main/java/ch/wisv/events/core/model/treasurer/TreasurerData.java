package ch.wisv.events.core.model.treasurer;

import java.time.LocalDateTime;

public interface TreasurerData {
    String getTitle();
    double getPrice();
    int getAmount();
    LocalDateTime getPaidAt();
}
