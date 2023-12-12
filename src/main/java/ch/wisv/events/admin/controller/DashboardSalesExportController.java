package ch.wisv.events.admin.controller;

import ch.wisv.events.core.model.order.PaymentMethod;
import ch.wisv.events.core.admin.TreasurerData;
import ch.wisv.events.core.admin.SalesExportSubmission;
import ch.wisv.events.utils.LdapGroup;


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
        
        ListIterator<TreasurerData> listIterator = treasurerData.listIterator(treasurerData.size());
        
        // Loop through all orders in TreasurerData and add orders of the same product together

        // Key: Product ID, Value: Septet with: event title, organized by, product title, total income, amount, vatRate, price
        Map<Integer, Septet<String, Integer, String, Double, Integer, String, Double>> map = new HashMap<>();
        
        while (listIterator.hasPrevious()) {
            TreasurerData data = listIterator.previous();

            if (!map.containsKey(data.getProductId())) {
                map.put(
                    data.getProductId(),
                    Septet.with(
                        data.getEventTitle(),
                        data.getOrganizedBy(),
                        data.getProductTitle(),
                        data.getPrice(),
                        data.getAmount(),
                        data.getVatRate(),
                        data.getPrice()));
            } else {
                Double totalIncome = map.get(data.getProductId()).getValue3();
                Integer totalAmount = map.get(data.getProductId()).getValue4();
                map.put(
                    data.getProductId(),
                    Septet.with(
                        data.getEventTitle(), 
                        data.getOrganizedBy(),
                        data.getProductTitle(),
                        totalIncome + data.getPrice(),
                        totalAmount + data.getAmount(),
                        data.getVatRate(),
                        data.getPrice()));
            }
        }
        
        // String csvContent = "Options:\n" + SalesExportSubmission.toString() + "\n\n";
        // csvContent += "Event;Organized by;Product;Total income;Total amount;VAT rate\n";
        String csvContent = "Event;Organized by;Product;Total income;Total amount;VAT rate;price\n";
        for (Map.Entry<Integer, Septet<String, Integer, String, Double, Integer, String, Double>> entry : map.entrySet()) {
            csvContent += entry.getValue().getValue0()                  // event title
                        + ";" + LdapGroup.intToString(entry.getValue().getValue1()) // organized by
                        + ";" + entry.getValue().getValue2()            // product title
                        + ";" + entry.getValue().getValue3()            // total income
                        + ";" + entry.getValue().getValue4()            // total amount
                        + ";" + entry.getValue().getValue5()            // vat rate
                        + ";" + entry.getValue().getValue6() + "\n";    // price
        }

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
}
