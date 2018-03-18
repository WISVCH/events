package ch.wisv.events.sales.controller.sell;

import ch.wisv.events.core.exception.normal.EventsException;
import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.model.order.OrderStatus;
import ch.wisv.events.core.service.auth.AuthenticationService;
import ch.wisv.events.core.service.order.OrderService;
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

@Controller
@RequestMapping(value = "/sales/sell")
@PreAuthorize("hasRole('USER')")
public class SalesSellMainController {

    private final AuthenticationService authenticationService;

    private final SalesService salesService;

    private final OrderService orderService;

    @Autowired
    public SalesSellMainController(
            AuthenticationService authenticationService, SalesService salesService, OrderService orderService
    ) {
        this.authenticationService = authenticationService;
        this.salesService = salesService;
        this.orderService = orderService;
    }

    @GetMapping
    public String index(Model model) {
        Customer currentUser = authenticationService.getCurrentCustomer();
        model.addAttribute("products", salesService.getAllGrantedProductByCustomer(currentUser));

        return "sales/sell/index";
    }

    @PostMapping
    public String createOrder(RedirectAttributes redirect, @ModelAttribute ch.wisv.events.core.model.order.OrderProductDto orderProductDto) {
        if (orderProductDto.getProducts().isEmpty()) {
            redirect.addFlashAttribute("error", "Shopping cart can not be empty!");

            return "redirect:/sales/sell/";
        }

        try {
            Order order = orderService.createOrderByOrderProductDTO(orderProductDto);
            order.setCreatedBy(authenticationService.getCurrentCustomer().getName());
            order.setStatus(OrderStatus.RESERVATION);
            orderService.tempSaveOrder(order);

            return "redirect:/sales/sell/customer/" + order.getPublicReference();
        } catch (EventsException e) {
            redirect.addFlashAttribute("error", e.getMessage());

            return "redirect:/sales/sell";
        }
    }
}
