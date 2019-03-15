package ch.wisv.events.infrastructure.webshop.util;

/**
 * WebshopConstant class.
 */
public final class WebshopConstant {

    /*
     * Error.
     */

    /** Error invalid. */
    public static final String ERROR_INVALID = "invalid";

    /** Error message guest checkout not allowed. */
    public static final String ERROR_MESSAGE_GUEST_CHECKOUT_NOT_ALLOWED = "Checkout as guest is not allowed for this order";


    /*
     * Model attributes constants.
     */

    /** Model attr errors. */
    public static final String MODEL_ATTR_ERRORS = "errors";

    /** Model attr event. */
    public static final String MODEL_ATTR_EVENT = "event";

    /** Model attr events. */
    public static final String MODEL_ATTR_EVENTS = "events";

    /** Model attribute order. */
    public static final String MODEL_ATTR_ORDER = "order";

    /** Model attr orderDto. */
    public static final String MODEL_ATTR_ORDER_DTO = "orderDto";

    /** Model attr user. */
    public static final String MODEL_ATTR_USER_DTO = "userDto";

    /*
     * Redirect constants.
     */

    /** Redirect to customer create page. */
    public static final String REDIRECT_CUSTOMER_PAGE = "redirect:/webshop/customer/%s";

    /** Redirect to homepage. */
    public static final String REDIRECT_HOME_PAGE = "redirect:/webshop";

    /** Redirect to login page. */
    public static final String REDIRECT_LOGIN_PAGE = "redirect:/webshop/login/%s";

    /** Redirect to order page. */
    public static final String REDIRECT_ORDER_PAGE = "redirect:/webshop/order/%s";

    /** Redirect to payment page. */
    public static final String REDIRECT_PAYMENT_PAGE = "redirect:/webshop/payment/%s";

    /*
     * Routes.
     */

    /** Route for the webshop. */
    public static final String ROUTE_WEBSHOP = "/webshop";

    /** Route for the webshop login. */
    public static final String ROUTE_WEBSHOP_CHECKOUT = "/webshop/checkout";

    /** Route for the webshop customer. */
    public static final String ROUTE_WEBSHOP_CUSTOMER = "/webshop/customer";

    /** Route for the webshop login. */
    public static final String ROUTE_WEBSHOP_LOGIN = "/webshop/login";

    /** Route for the webshop payment. */
    public static final String ROUTE_WEBSHOP_PAYMENT = "/webshop/payment";

    /** Route for the webshop login option connect. */
    public static final String ROUTE_WEBSHOP_LOGIN_OPTION_CONNECT = "/connect";

    /** Route for the webshop login option guest. */
    public static final String ROUTE_WEBSHOP_LOGIN_OPTION_GUEST = "/guest";

    /** Route for the webshop option public reference. */
    public static final String ROUTE_WEBSHOP_OPTION_PUBLIC_REFERENCE = "/{publicReference}";

    /** Route for the webshop order. */
    public static final String ROUTE_WEBSHOP_ORDER = "/webshop/order";

    /*
     * View constants.
     */

    /** View webshop checkout order. */
    public static final String VIEW_WEBSHOP_CHECKOUT_ORDER = "webshop/webshop-checkout-order";

    /** View webshop index page. */
    public static final String VIEW_WEBSHOP_INDEX = "webshop/webshop-index";

    /** View webshop single event page. */
    public static final String VIEW_WEBSHOP_SINGLE_EVENT = "webshop/webshop-single-event";

    /** View webshop customer create page. */
    public static final String VIEW_WEBSHOP_CUSTOMER = "webshop/webshop-customer-create";

    /**
     * Private constructor.
     */
    private WebshopConstant() {
    }
}
