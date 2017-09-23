package ch.wisv.events.sales.service;

import ch.wisv.connect.common.model.CHUserInfo;
import ch.wisv.events.core.model.event.Event;
import ch.wisv.events.core.model.event.EventStatus;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.repository.EventRepository;
import ch.wisv.events.utils.LDAPGroup;
import org.mitre.openid.connect.model.OIDCAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
public class SalesAppProductServiceImpl implements SalesAppProductService {

    /**
     * Field eventRepository
     */
    private final EventRepository eventRepository;

    /**
     * Constructor SalesAppProductServiceImpl creates a new SalesAppProductServiceImpl instance.
     *
     * @param eventRepository of type EventRepository
     */
    public SalesAppProductServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    /**
     * Method getAllGrantedProducts returns the allGrantedProducts of this SalesAppProductService object.
     *
     * @return the allGrantedProducts (type List<Product>) of this SalesAppProductService object.
     */
    @Override
    public List<Product> getAllGrantedProducts() {
        List<Event> grantedEvents = this.getGrantedEvents();

        List<Product> products = new ArrayList<>();
        grantedEvents.forEach(event -> event.getProducts().forEach(product -> {
            if (product.getSellStart().isBefore(LocalDateTime.now()) && product.getSellEnd().isAfter(LocalDateTime.now())) {
                products.add(product);
            }
        }));

        return products;
    }

    /**
     * Method getGrantedEvents ...
     *
     * @return List<Event>
     */
    private List<Event> getGrantedEvents() {
        Collection<GrantedAuthority> authorities = this.getAuthentication().getAuthorities();
        List<Event> grantedEvents = new ArrayList<>();

        if (authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            grantedEvents.addAll(this.eventRepository.findAllByPublishedAndEndingIsAfter(EventStatus.PUBLISHED, LocalDateTime.now()));
        } else {
            this.getAllLdapGroups().forEach(current -> grantedEvents.addAll(
                    this.eventRepository.findAllByPublishedAndOrganizedByAndEndingIsAfter(EventStatus.PUBLISHED, current, LocalDateTime.now()))
            );
        }

        return grantedEvents;
    }

    /**
     * Method getAllLdapGroups returns the allLdapGroups of this SalesAppProductServiceImpl object.
     *
     * @return the allLdapGroups (type List<LDAPGroup>) of this SalesAppProductServiceImpl object.
     */
    private List<LDAPGroup> getAllLdapGroups() {
        List<LDAPGroup> groups = new ArrayList<>();
        if (this.getAuthentication().getUserInfo() instanceof CHUserInfo) {
            CHUserInfo userInfo = (CHUserInfo) this.getAuthentication().getUserInfo();

            userInfo.getLdapGroups().forEach(current -> {
                try {
                    groups.add(LDAPGroup.valueOf(current.toUpperCase()));
                } catch (IllegalArgumentException ignored) {
                }
            });
        }

        return groups;
    }

    /**
     * Method getAuthentication returns the authentication of this SalesAppProductServiceImpl object.
     *
     * @return the authentication (type OIDCAuthenticationToken) of this SalesAppProductServiceImpl object.
     */
    private OIDCAuthenticationToken getAuthentication() {
        return (OIDCAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
    }
}
