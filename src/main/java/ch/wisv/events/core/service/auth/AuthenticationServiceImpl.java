package ch.wisv.events.core.service.auth;

import ch.wisv.events.core.exception.normal.CustomerInvalidException;
import ch.wisv.events.core.exception.normal.CustomerNotFoundException;
import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.service.customer.CustomerService;
import ch.wisv.events.utils.LdapGroup;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
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

            OidcUser oidcUser = this.getOidcUser(auth);

            Customer customer = this.getCustomerByOidcUser(oidcUser);

            try {
                this.updateCustomerInfo(customer, oidcUser);
            } catch (CustomerNotFoundException ignored) {
            }

            return customer;
        } catch (CustomerInvalidException | InvalidTokenException e) {
            return null;
        }
    }

    /**
     * Get OIDCIdToken from a Authentication object.
     *
     * @param auth of type Authentication.
     *
     * @return OIDCIdToken
     */
    private DefaultOidcUser getOidcUser(Authentication auth) throws InvalidTokenException {
        if (!(auth.getPrincipal() instanceof DefaultOidcUser)) {
            throw new InvalidTokenException("Invalid authentication");
        }

        DefaultOidcUser oidcUser = (DefaultOidcUser) auth.getPrincipal();
        if (oidcUser.getEmail() == null) {
            throw new InvalidTokenException("Invalid UserInfo object");
        }

        return oidcUser;
    }

    /**
     * Get a Customer by OidcIdToken.
     *
     * @param userInfo of type OidcIdToken.
     *
     * @return Customer
     *
     * @throws CustomerInvalidException when the OidcIdToken will result in an invalid
     */
    private Customer getCustomerByOidcUser(OidcUser userInfo) throws CustomerInvalidException {
        try {
            return customerService.getBySub(userInfo.getSubject());
        } catch (CustomerNotFoundException ignored) {
        }

        try {
            return customerService.getByEmail(userInfo.getEmail());
        } catch (CustomerNotFoundException ignored) {
        }

        return customerService.createByOidcUser(userInfo);
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
    private void updateCustomerInfo(Customer customer, OidcUser userInfo) throws CustomerInvalidException, CustomerNotFoundException {
        if (customer.getSub() == null || customer.getSub().equals("")) {
            customer.setSub(userInfo.getSubject());
        }

        if (customer.getEmail() == null || customer.getEmail().equals("")) {
            customer.setEmail(userInfo.getEmail());
        }

        customer.setVerifiedChMember(true);

        List<String> ldapGroups = Arrays.asList(userInfo.getClaim("ldap_username"));

        customer.setLdapGroups(
                ldapGroups.stream()
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
