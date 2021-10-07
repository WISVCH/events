package ch.wisv.events.core.service.auth;

import ch.wisv.events.ServiceTest;
import ch.wisv.events.core.exception.normal.CustomerNotFoundException;
import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.service.customer.CustomerService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;

public class AuthenticationServiceImplTest extends ServiceTest {

    /** CustomerService. */
    @Mock
    private CustomerService customerService;

    /** AuthenticationService. */
    private AuthenticationService authenticationService;

    @Before
    public void setUp() {
        authenticationService = new AuthenticationServiceImpl(customerService);
    }

    @After
    public void tearDown() {
        authenticationService = null;
    }

    @Test
    public void testGetChUserInfoInvalidAuth() {
        Authentication auth = mock(UsernamePasswordAuthenticationToken.class);
        SecurityContextHolder.getContext().setAuthentication(auth);
        assertNull(authenticationService.getCurrentCustomer());
    }

    @Test
    public void testGetUserInfoInvalidUserInfo() {
        Authentication auth = mock(Authentication.class);
        when(((OidcUserAuthority) auth).getUserInfo()).thenReturn(null);

        SecurityContextHolder.getContext().setAuthentication(auth);
        assertNull(authenticationService.getCurrentCustomer());
    }

    @Test
    public void testGetCustomerByChUserInfoSub() throws Exception {
        Customer customer = new Customer();
        customer.setSub("WISVCH.1234");

        HashMap<String, Object> claims = new HashMap<>();
        claims.put("sub", "WISV.CH.1234");
        claims.put("ldap_groups", new HashSet<>());
        OidcUserInfo userInfo = new OidcUserInfo(claims);

        Authentication auth = mock(Authentication.class);
        when(((OidcUserAuthority) auth).getUserInfo()).thenReturn(userInfo);
        when(customerService.getBySub("WISVCH.1234")).thenReturn(customer);

        SecurityContextHolder.getContext().setAuthentication(auth);
        assertEquals(customer, authenticationService.getCurrentCustomer());
    }

    // TODO: add customer update info
//    @Test
//    public void testUpdateCustomerInfo() throws Exception {
//        Customer customer = new Customer();
//
//        HashMap<String, Object> claims = new HashMap<>();
//        OidcUserInfo userInfo = new OidcUserInfo(claims);
//
//        HashSet<String> ldapGroups = new HashSet<>();
//        ldapGroups.add("CHBEHEER");
//        ldapGroups.add("TEST");
//
//        userInfo.setLdapGroups(ldapGroups);
//
//        Authentication auth = mock(Aut].class);
//        when(((OidcUserAuthority) auth).getUserInfo()).thenReturn(userInfo);
//        when(customerService.getBySub(null)).thenThrow(CustomerNotFoundException.class);
//        when(customerService.getByEmail(null)).thenThrow(CustomerNotFoundException.class);
//        when(customerService.createByChUserInfo(userInfo)).thenReturn(customer);
//
//        SecurityContextHolder.getContext().setAuthentication(auth);
//        assertEquals(customer, authenticationService.getCurrentCustomer());
//        assertEquals(1, customer.getLdapGroups().size());
//    }
}