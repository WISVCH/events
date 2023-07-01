package ch.wisv.events.admin.utils;

import ch.wisv.events.core.util.VatRate;
import lombok.Getter;
import net.minidev.json.JSONObject;

/**
 * ProductTemplate enum.
 */
public enum ProductTemplate {

    /** Template for a tuesday lecture. */
    TUESDAY_LECTURE("T.U.E.S.Day lecture", "T.U.E.S.Day lecture: ", 0, VatRate.VAT_HIGH, 1, false, false),

    /** Template for a members lunch. */
    MEMBERS_LUNCH("Members lunch", "Members lunch ticket", 1, VatRate.VAT_FREE, 5, true, true),

    /** Template for a pizza. */
    PIZZA("Pizza", "Pizza", 5.0, VatRate.VAT_LOW , 25, false, true);

    /** Name of the template. */
    @Getter
    private final String templateName;

    /** Product title. */
    @Getter
    private final String title;

    /** Product cost. */
    @Getter
    private final double cost;

    /** VatRate of the product. */
    @Getter
    private final VatRate vatRate;

    /** Product maxSoldperCustomer. */
    @Getter
    private final int maxSolPerCustomer;

    /** Product chOnly. */
    @Getter
    private final boolean chOnly;

    /** Product reservable. */
    @Getter
    private final boolean reservable;

    /**
     * Products template.
     *
     * @param templateName      of type String
     * @param title             of type String
     * @param cost              of type double
     * @param vatRate           of type VatRate
     * @param maxSolPerCustomer of type int
     * @param chOnly            of type boolean
     * @param reservable        of type boolean
     */
    ProductTemplate(
            String templateName, String title,
            double cost,
            VatRate vatRate,
            int maxSolPerCustomer,
            boolean chOnly,
            boolean reservable
    ) {
        this.templateName = templateName;
        this.title = title;
        this.cost = cost;
        this.vatRate = vatRate;
        this.maxSolPerCustomer = maxSolPerCustomer;
        this.chOnly = chOnly;
        this.reservable = reservable;
    }

    /**
     * Convert template to JSON String.
     *
     * @return String
     */
    public String toJson() {
        JSONObject object = new JSONObject();

        object.put("title", this.getTitle());
        object.put("cost", this.getCost());
        object.put("vatRate", this.getVatRate());
        object.put("maxSoldPerCustomer", this.getMaxSolPerCustomer());
        object.put("chOnly", this.isChOnly());
        object.put("reservable", this.isReservable());

        return object.toString();
    }
}
