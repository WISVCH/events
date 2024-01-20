package ch.wisv.events.core.admin;

import ch.wisv.events.core.model.order.PaymentMethod;

import lombok.Getter;
import lombok.Setter;
import java.util.*;
import java.time.LocalDate;
import com.google.common.collect.Lists;


/**
 * Class that contains the settings for the salesexport query,
 * which can be specified in on the form on the Sales Export tab.
 */
@Getter
@Setter
public class SalesExportSubmission {
    
    /**
     * Year of query
     */
    private int year;

    /**
     * Month of query.
     */
    private int month;

    /**
     * Payment methods that should be contained in query.
     */
    private List<PaymentMethod> includedPaymentMethods;

    private boolean freeProductsIncluded;

    /**
     * Default constructor.
     */
    public SalesExportSubmission() {
        
        // default: previous month
        if (LocalDate.now().getMonthValue() == 1) {
            this.year = LocalDate.now().getYear()-1;
            this.month = 12;
        }
        else {
            this.year = LocalDate.now().getYear();
            this.month = LocalDate.now().getMonthValue()-1;
        }
        
        this.includedPaymentMethods = Lists.newArrayList(PaymentMethod.IDEAL, PaymentMethod.SOFORT);

        this.freeProductsIncluded = false;

    }
    
    /**
     * Return all options in a string.
     */
    public String toString() {
        String settings = "Year: " + this.year + ", Month: " + this.month + ", Free products included: " + this.freeProductsIncluded;
        for (PaymentMethod paymentMethod : this.includedPaymentMethods) {
            settings += ", Payment method: " + paymentMethod.getName();
        }
        return settings;
    }
}   
