package ch.wisv.events.core.service.auth;

import ch.wisv.connect.common.model.CHUserInfo;
import ch.wisv.events.core.exception.normal.CustomerInvalidException;
import ch.wisv.events.core.exception.normal.CustomerNotFoundException;
import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.service.customer.CustomerService;
import ch.wisv.events.utils.LDAPGroup;
import org.mitre.openid.connect.model.OIDCAuthenticationToken;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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
public class AuthenticationServiceImpl implements AuthenticationService {

    /**
     * CustomerService.
     */
    private final CustomerService customerService;

    /**
     * Default constructor.
     *
     * @param customerService of type CustomerService.
     */
    public AuthenticationServiceImpl(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * Get the Customer that is currently logged in.
     *
     * @return Customer
     */
    @Override
    public Customer getCurrentCustomer() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CHUserInfo userInfo = this.getChUserInfo(auth);

        try {
            Customer customer = this.getCustomerByCHUserInfo(userInfo);
            this.updateCustomerInfo(customer, userInfo);

            return customer;
        } catch (CustomerInvalidException | CustomerNotFoundException e) {
            throw new AccessDeniedException("Invalid authentication");
        }
    }

    /**
     * Get CHUserInfo from a Authentication object.
     *
     * @param auth of type Authentication.
     *
     * @return CHUserInfo
     */
    private CHUserInfo getChUserInfo(Authentication auth) {
        if (!(auth instanceof OIDCAuthenticationToken)) {
            throw new AccessDeniedException("Invalid authentication");
        }

        OIDCAuthenticationToken oidcToken = (OIDCAuthenticationToken) auth;

        if (!(oidcToken.getUserInfo() instanceof CHUserInfo)) {
            throw new AccessDeniedException("Invalid UserInfo object");
        }

        return (CHUserInfo) oidcToken.getUserInfo();
    }

    /**
     * Get a Customer by CHUserInfo.
     *
     * @param userInfo of type CHUserInfo.
     *
     * @return Customer
     *
     * @throws CustomerInvalidException when the CHUserInfo will result in an invalid
     */
    private Customer getCustomerByCHUserInfo(CHUserInfo userInfo) throws CustomerInvalidException {
        try {
            return customerService.getBySub(userInfo.getSub());
        } catch (CustomerNotFoundException ignored) {
        }

        try {
            return customerService.getByUsername(userInfo.getLdapUsername());
        } catch (CustomerNotFoundException ignored) {
        }

        try {
            return customerService.getByEmail(userInfo.getEmail());
        } catch (CustomerNotFoundException ignored) {
        }

        return customerService.createByChUserInfo(userInfo);
    }

    /**
     * Update Customer Info with the information provided by CHUserInfo.
     *
     * @param customer of type Customer.
     * @param userInfo of type CHUserInfo.
     *
     * @throws CustomerInvalidException  when the Customer is invalid.
     * @throws CustomerNotFoundException when the Customer does not exists.
     */
    private void updateCustomerInfo(Customer customer, CHUserInfo userInfo) throws CustomerInvalidException, CustomerNotFoundException {
        if (customer.getSub() == null || customer.getSub().equals("")) {
            customer.setSub(userInfo.getSub());
        }

        if (customer.getChUsername() == null || customer.getChUsername().equals("")) {
            customer.setChUsername(userInfo.getLdapUsername());
        }

        if (customer.getChUsername() == null || customer.getEmail().equals("")) {
            customer.setEmail(userInfo.getEmail());
        }

        customer.setLdapGroups(userInfo.getLdapGroups().stream().map(ldapString -> {
            try {
                return LDAPGroup.valueOf(ldapString.toUpperCase());
            } catch (IllegalArgumentException ignored) {
                return null;
            }
        }).collect(Collectors.toList()));

        customerService.update(customer);
    }
}
