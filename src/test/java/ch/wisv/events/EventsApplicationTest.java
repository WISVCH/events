package ch.wisv.events;

import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.order.Order;
import ch.wisv.events.core.service.auth.AuthenticationService;
import ch.wisv.events.core.service.mail.MailService;
import ch.wisv.events.utils.LdapGroup;
import ch.wisv.events.webshop.service.PaymentsService;
import com.google.common.collect.ImmutableList;
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
    public PaymentsService paymentsService() {
        PaymentsService paymentsService = Mockito.mock(PaymentsService.class);

        Mockito.when(paymentsService.getMollieUrl(Mockito.any(Order.class))).then(invocation -> "https://paymentURL.com");

        return paymentsService;
    }

    @Component
    public static class TestRunner implements CommandLineRunner {

        /**
         * Callback used to run the bean.
         *
         * @param args incoming main method arguments
         *
         */
        @Override
        public void run(String... args) {

        }
    }
}