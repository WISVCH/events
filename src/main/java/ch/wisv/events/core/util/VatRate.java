package ch.wisv.events.core.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
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

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static VatRate fromJson(String value) {
        return VatRate.valueOf(value);
    }

    @JsonValue
    public String toJson() {
        return this.name();
    }

    public String getPercentage() {
        if (this.name() == "VAT_FREE") {
            return "VAT Free";
        }
        return this.vatRate + "%";
    }
}