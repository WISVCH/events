package ch.wisv.events.sales.controller.sell;

import ch.wisv.events.core.exception.normal.EventsException;
import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderProductDto;
import ch.wisv.events.core.service.auth.AuthenticationService;
import ch.wisv.events.core.service.order.OrderService;
import ch.wisv.events.core.service.order.OrderValidationService;
import ch.wisv.events.sales.service.SalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * SalesSellMainController class.
 */
@Controller
@RequestMapping({"/sales/sell","/sales/sell/"})
@PreAuthorize("hasRole('USER')")
public class SalesSellMainController {

    /** AuthenticationService. */
    private final AuthenticationService authenticationService;

    /** SalesService. */
    private final SalesService salesService;

    /** OrderService. */
    private final OrderService orderService;

    /** OrderValidationService. */
    private final OrderValidationService orderValidationService;

    /**
     * SalesSellMainController constructor.
     *
     * @param authenticationService  of type AuthenticationService
     * @param salesService           of type SalesService
     * @param orderService           of type OrderService
     * @param orderValidationService of type OrderValidationService
     */
    @Autowired
    public SalesSellMainController(
            AuthenticationService authenticationService,
            SalesService salesService,
            OrderService orderService,
            OrderValidationService orderValidationService
    ) {
        this.authenticationService = authenticationService;
        this.salesService = salesService;
        this.orderService = orderService;
        this.orderValidationService = orderValidationService;
    }

    /**
     * Sales sell index page.
     *
     * @param model of type Model
     *
     * @return String
     */
    @GetMapping("")
    public String index(Model model) {
        Customer currentUser = authenticationService.getCurrentCustomer();
        model.addAttribute("products", salesService.getAllGrantedProductByCustomer(currentUser));

        return "sales/sell/index";
    }

    /**
     * Create an Order based on a OrderProductDto.
     *
     * @param redirect        of type RedirectAttributes
     * @param orderProductDto of type OrderProductDto
     *
     * @return String
     */
    @PostMapping("")
    public String createOrder(RedirectAttributes redirect, @ModelAttribute OrderProductDto orderProductDto) {
        if (orderProductDto.getProducts().isEmpty()) {
            redirect.addFlashAttribute("error", "Shopping cart can not be empty!");

            return "redirect:/sales/sell/";
        }

        try {
            Order order = orderService.createOrderByOrderProductDto(orderProductDto);
            order.setCreatedBy(authenticationService.getCurrentCustomer().getName());
            orderValidationService.assertOrderIsValid(order);
            orderService.create(order);

            return "redirect:/sales/sell/customer/" + order.getPublicReference();
        } catch (EventsException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/sales/sell";
        }
    }
}
