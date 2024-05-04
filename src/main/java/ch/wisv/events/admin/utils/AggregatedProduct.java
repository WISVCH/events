package ch.wisv.events.admin.utils;


/**
 * AggregatedProduct class. Is used to store all information about a single product 
 * when adding the sales over a month together in the Sales Export tab
 */
public class AggregatedProduct {
    public String eventTitle;
    public String organizedBy;
    public String productTitle;
    public Double totalIncome;
    public Integer totalAmount;
    public String vatRate;
    public Double price;
}
