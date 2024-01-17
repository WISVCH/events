package ch.wisv.events.admin.controller;

import ch.wisv.events.core.model.order.PaymentMethod;
import ch.wisv.events.core.admin.TreasurerData;
import ch.wisv.events.core.admin.SalesExportSubmission;
import ch.wisv.events.utils.LdapGroup;
import ch.wisv.events.admin.utils.AggregatedProduct;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import com.google.common.collect.Lists;

import ch.wisv.events.core.repository.OrderRepository;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.javatuples.Septet;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;


/**
 * DashboardSalesExportController class.
 */
@Controller
@RequestMapping("/administrator/salesexport")
@PreAuthorize("hasRole('ADMIN')")
public class DashboardSalesExportController extends DashboardController {

    /** TreasurerRepository */
    private final OrderRepository orderRepository;

    /**
     * DashboardSalesExportController constructor.
     *
     * @param orderRepository of type OrderRepository
     */
    @Autowired
    public DashboardSalesExportController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Index of vendor [GET "/"].
     *
     * @param model String model
     *
     * @return path to Thymeleaf template location
     */
    @GetMapping
    public String index(Model model) {
        // model.addAttribute("productMap", this.generateProductMap());
        model.addAttribute("SalesExportSubmission", new SalesExportSubmission());

        return "admin/salesexport/index";
    }

    
    
    /**
     * Exports sales of month to csv
     * 
     */
    @GetMapping(value="/csv", produces="text/csv")
    public HttpEntity<? extends Object> csvExport(@ModelAttribute SalesExportSubmission SalesExportSubmission, Model model) {
        model.addAttribute("SalesExportSubmission", SalesExportSubmission);
        
        // Convert payment methods to integers
        List<Integer> paymentMethods = new ArrayList<>();
        SalesExportSubmission.getIncludedPaymentMethods().forEach( (m) -> paymentMethods.add(m.toInt()) );

        List<TreasurerData> treasurerData = orderRepository.findallPaymentsByMonth(SalesExportSubmission.getMonth(), SalesExportSubmission.getYear(), paymentMethods, SalesExportSubmission.isFreeProductsIncluded());
        
        Map<Integer, AggregatedProduct> map = aggregateOrders(treasurerData);

        String csvContent = generateCsvContent(map);

        InputStream bufferedInputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));
        InputStreamResource fileInputStream = new InputStreamResource(bufferedInputStream);

        String filename = "Sales_overview_"+SalesExportSubmission.getYear()+"-"+SalesExportSubmission.getMonth()+"_export.csv";

        // setting HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
        // defining the custom Content-Type
        headers.set(HttpHeaders.CONTENT_TYPE, "text/csv");

        return new ResponseEntity<>(
                fileInputStream,
                headers,
                HttpStatus.OK
        );
    }

    /**
     * Go through all orders in a given month and add the total amount and income together in Aggregated product
     *
     * @param treasurerData containing all orders that have to be aggregated
     *
     * @return map with entry per product, with the product id as key
     */
    private Map<Integer, AggregatedProduct> aggregateOrders(List<TreasurerData> treasurerData) {
        
        ListIterator<TreasurerData> listIterator = treasurerData.listIterator(treasurerData.size());
        
        // Loop through all orders in treasurerData and add orders of the same product together in AggregatedProduct
        // Key: Product ID, Value: Aggregated product
        Map<Integer, AggregatedProduct> map = new HashMap<>();
        
        while (listIterator.hasPrevious()) {
            TreasurerData data = listIterator.previous();

            if (!map.containsKey(data.getProductId())) {
                AggregatedProduct product = new AggregatedProduct();

                product.eventTitle = data.getEventTitle();
                product.organizedBy = LdapGroup.intToString(data.getOrganizedBy());
                product.productTitle = data.getProductTitle();
                product.totalIncome = data.getPrice();
                product.totalAmount = data.getAmount();
                product.vatRate = data.getVatRate();
                product.price = data.getPrice();

                map.put(data.getProductId(),product);

            } else {
                AggregatedProduct product = map.get(data.getProductId());
                product.totalIncome += data.getPrice()*data.getAmount();
                product.totalAmount += data.getAmount();
                map.put(data.getProductId(),product);
            }
        }

        return map;
    }




    /**
     * Format all aggregated products into string in csv format
     *
     * @param map containing all aggregated orders.
     *
     * @return string that should be written to csv file
     */
    private String generateCsvContent(Map<Integer, AggregatedProduct> map) {
        
        // String csvContent = "Options:\n" + SalesExportSubmission.toString() + "\n\n";
        // csvContent += "Event;Organized by;Product;Total income;Total amount;VAT rate\n";
        String csvContent = "Event;Organized by;Product;Total income;Total amount;VAT rate;price\n";
        for (Map.Entry<Integer, AggregatedProduct> entry : map.entrySet()) {
            csvContent += entry.getValue().eventTitle                  // event title
                        + ";" + entry.getValue().organizedBy // organized by
                        + ";" + entry.getValue().productTitle           // product title
                        + ";" + entry.getValue().totalIncome            // total income
                        + ";" + entry.getValue().totalAmount            // total amount
                        + ";" + entry.getValue().vatRate            // vat rate
                        + ";" + entry.getValue().price + "\n";    // price
        }
    return csvContent;
    }
}
