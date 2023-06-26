package ch.wisv.events.core.util;

import lombok.Getter;

public enum VatRate {
    VAT_FREE(0.0),
    VAT_ZERO(0.0),
    VAT_LOW(9.0),
    VAT_HIGH(21.0);

    @Getter
    private final Double vatRate;

    VatRate(Double vatRate) {
        this.vatRate = vatRate;
    }

    public String toString() {
        return this.name() + " (" + this.vatRate + "%)";
    }
}