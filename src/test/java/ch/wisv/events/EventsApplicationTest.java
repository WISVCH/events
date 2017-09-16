package ch.wisv.events;

import ch.wisv.events.core.service.mail.MailService;
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

/**
 * Copyright (c) 2016  W.I.S.V. 'Christiaan Huygens'
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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