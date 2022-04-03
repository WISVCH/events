package ch.wisv.events.admin.model;

import java.time.LocalDateTime;

public class TreasurerData {
    public String title;
    public double price;
    public int amount;
    public LocalDateTime paidAt;

    public TreasurerData(String title, double price, int amount, LocalDateTime paidAt) {
        this.title = title;
        this.price = price;
        this.amount = amount;
        this.paidAt = paidAt;
    }
}
