package ch.wisv.events.core.service.auth;

import ch.wisv.connect.common.model.CHUserInfo;
import ch.wisv.events.core.exception.normal.CustomerInvalidException;
import ch.wisv.events.core.exception.normal.CustomerNotFoundException;
import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.service.customer.CustomerService;
import ch.wisv.events.utils.LdapGroup;
import java.util.Objects;
import java.util.stream.Collectors;
import org.mitre.openid.connect.model.OIDCAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.stereotype.Service;

/**
 * AuthenticationService class.
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
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            CHUserInfo userInfo = this.getChUserInfo(auth);

            Customer customer = this.getCustomerByChUserInfo(userInfo);
            this.updateCustomerInfo(customer, userInfo);

            return customer;
        } catch (CustomerInvalidException | CustomerNotFoundException | InvalidTokenException e) {
            return null;
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
            throw new InvalidTokenException("Invalid authentication");
        }

        OIDCAuthenticationToken oidcToken = (OIDCAuthenticationToken) auth;

        if (!(oidcToken.getUserInfo() instanceof CHUserInfo)) {
            throw new InvalidTokenException("Invalid UserInfo object");
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
    private Customer getCustomerByChUserInfo(CHUserInfo userInfo) throws CustomerInvalidException {
        try {
            return customerService.getBySub(userInfo.getSub());
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

        if (customer.getEmail() == null || customer.getEmail().equals("")) {
            customer.setEmail(userInfo.getEmail());
        }

        customer.setVerifiedChMember(true);

        customer.setLdapGroups(
                userInfo.getLdapGroups().stream()
                        .map(ldapString -> {
                            try {
                                return LdapGroup.valueOf(ldapString.toUpperCase());
                            } catch (IllegalArgumentException ignored) {
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())
        );

        customerService.update(customer);
    }
}
