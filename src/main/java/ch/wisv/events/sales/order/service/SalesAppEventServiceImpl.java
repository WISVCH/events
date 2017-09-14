package ch.wisv.events.sales.order.service;

import ch.wisv.connect.common.model.CHUserInfo;
import ch.wisv.events.core.model.event.EventOptions;
import ch.wisv.events.core.model.event.EventStatus;
import ch.wisv.events.core.model.product.Product;
import ch.wisv.events.core.repository.EventRepository;
import ch.wisv.events.utils.LDAPGroup;
import org.mitre.openid.connect.model.OIDCAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
public class SalesAppEventServiceImpl implements SalesAppEventService {

    /**
     * Field eventRepository
     */
    private final EventRepository eventRepository;

    /**
     * Constructor SalesAppEventServiceImpl creates a new SalesAppEventServiceImpl instance.
     *
     * @param eventRepository of type EventRepository
     */
    public SalesAppEventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    /**
     * Method getAllGrantedProducts returns the allGrantedProducts of this SalesAppEventService object.
     *
     * @return the allGrantedProducts (type List<Product>) of this SalesAppEventService object.
     */
    @Override
    public List<Product> getAllGrantedProducts() {
        List<Product> products = new ArrayList<>();

        this.getAllLdapGroups().forEach(current -> {
            this.eventRepository.findAllByOptions(new EventOptions(EventStatus.PUBLISHED, current)).forEach(
                    event -> products.addAll(event.getProducts())
            );
        });

        return products;
    }

    /**
     * Method getAllLdapGroups returns the allLdapGroups of this SalesAppEventServiceImpl object.
     *
     * @return the allLdapGroups (type List<LDAPGroup>) of this SalesAppEventServiceImpl object.
     */
    private List<LDAPGroup> getAllLdapGroups() {
        OIDCAuthenticationToken auth = (OIDCAuthenticationToken) SecurityContextHolder.getContext()
                .getAuthentication();

        List<LDAPGroup> groups = new ArrayList<>();
        if (auth.getUserInfo() instanceof CHUserInfo) {
            CHUserInfo userInfo = (CHUserInfo) auth.getUserInfo();

            userInfo.getLdapGroups().forEach(current -> {
                try {
                    groups.add(LDAPGroup.valueOf(current.toUpperCase()));
                } catch (IllegalArgumentException ignored) {
                }
            });
        }

        return groups;
    }
}
