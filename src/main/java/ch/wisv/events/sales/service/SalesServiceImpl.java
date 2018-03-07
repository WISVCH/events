package ch.wisv.events.sales.service;

import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.service.event.EventService;
import ch.wisv.events.utils.LDAPGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
@Service
public class SalesServiceImpl implements SalesService {

    /**
     * EventService.
     */
    private final EventService eventService;

    /**
     * Default constructor.
     *
     * @param eventService of type EventService.
     */
    @Autowired
    public SalesServiceImpl(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Get all Event which can be sold by the current user.
     *
     * @return List<Event>
     */
    @Override
    public List<Event> getAllGrantedEventByCustomer(Customer customer) {
        if (customer.getLdapGroups().contains(LDAPGroup.BESTUUR)) {
            return eventService.getUpcoming();
        } else {
            return eventService.getUpcoming().stream()
                    .filter(events -> customer.getLdapGroups().contains(events.getOrganizedBy()))
                    .collect(Collectors.toList());
        }
    }

    /**
     * Get all Product which can be sold by the current user.
     *
     * @return List<Product>
     */
    @Override
    public List<Product> getAllGrantedProductByCustomer(Customer customer) {
        List<Event> events = this.getAllGrantedEventByCustomer(customer);

        return events.stream().flatMap(event -> event.getProducts().stream())
                .filter(product -> LocalDateTime.now().isAfter(product.getSellStart()) && LocalDateTime.now().isBefore(product.getSellEnd()))
                .collect(Collectors.toList());
    }
}
