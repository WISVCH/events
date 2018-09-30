package ch.wisv.events.admin.utils;

import lombok.Getter;
import net.minidev.json.JSONObject;

/**
 * ProductTemplate enum.
 */
public enum ProductTemplate {

    /** Template for a tuesday lecture. */
    TUESDAY_LECTURE("T.U.E.S.Day lecture", "T.U.E.S.Day lecture: ", 0, 1, false),

    /** Template for a members lunch. */
    MEMBERS_LUNCH("Members lunch", "Members lunch ticket", 1, 5, true),

    /** Template for a pizza. */
    PIZZA("Pizza", "Pizza", 5.0, 25, false);

    /** Name of the template. */
    @Getter
    private final String templateName;

    /** Product title. */
    @Getter
    private final String title;

    /** Product cost. */
    @Getter
    private final double cost;

    /** Product maxSoldperCustomer. */
    @Getter
    private final int maxSolPerCustomer;

    /** Product chOnly. */
    @Getter
    private final boolean chOnly;

    /**
     * Products template.
     *
     * @param templateName      of type String
     * @param title             of type String
     * @param cost              of type double
     * @param maxSolPerCustomer of type int
     * @param chOnly            of type boolean
     */
    ProductTemplate(
            String templateName, String title,
            double cost,
            int maxSolPerCustomer,
            boolean chOnly
    ) {
        this.templateName = templateName;
        this.title = title;
        this.cost = cost;
        this.maxSolPerCustomer = maxSolPerCustomer;
        this.chOnly = chOnly;
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
        object.put("maxSoldPerCustomer", this.getMaxSolPerCustomer());
        object.put("chOnly", this.isChOnly());

        return object.toString();
    }
}
