package ch.wisv.events;

import ch.wisv.events.core.exception.runtime.PaymentsConnectionException;
import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.repository.OrderRepository;
import ch.wisv.events.core.service.auth.AuthenticationService;
import ch.wisv.events.core.service.mail.MailService;
import ch.wisv.events.utils.LdapGroup;
import ch.wisv.events.webshop.service.PaymentsService;
import com.google.common.collect.ImmutableList;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@Configuration
@ComponentScan
@EnableAutoConfiguration(exclude = {FlywayAutoConfiguration.class})
@ActiveProfiles("test")
public class EventsApplicationTest {


    /**
     * Method main ...
     *
     * @param args of type String[]
     */
    public static void main(String[] args) {
        SpringApplication.run(EventsApplicationTest.class, args);
    }

    /**
     * Method mailService ...
     *
     * @return MailService
     */
    @Bean
    @Primary
    public MailService mailService() {
        return Mockito.mock(MailService.class);
    }

    @Bean
    @Primary
    public AuthenticationService authenticationService() {
        AuthenticationService service = Mockito.mock(AuthenticationService.class);
        Customer customer = new Customer();
        customer.setLdapGroups(ImmutableList.of(LdapGroup.BEHEER));
        Mockito.when(service.getCurrentCustomer()).thenReturn(customer);

        return service;
    }

    @Bean
    @Primary
    public PaymentsService paymentService() {
        PaymentsService paymentsService = Mockito.mock(PaymentsService.class);

        Mockito.when(paymentsService.getPaymentsMollieUrl(Mockito.any(Order.class))).then(invocation -> "https://paymentURL.com");
        Mockito.when(paymentsService.getPaymentsOrderStatus("123-345-561")).thenReturn("WAITING");
        Mockito.when(paymentsService.getPaymentsOrderStatus("123-345-562")).thenReturn("PAID");
        Mockito.when(paymentsService.getPaymentsOrderStatus("123-345-563")).thenReturn("CANCELLED");
        Mockito.when(paymentsService.getPaymentsOrderStatus("123-345-564")).thenReturn("STATUS_EXCEPTION");
        Mockito.when(paymentsService.getPaymentsOrderStatus("123-345-565")).thenThrow(new PaymentsConnectionException(""));

        return paymentsService;
    }

    @Component
    public class TestRunner implements CommandLineRunner {

        /**
         * Callback used to run the bean.
         *
         * @param args incoming main method arguments
         * @throws Exception on error
         */
        @Override
        public void run(String... args) throws Exception {

        }
    }
}