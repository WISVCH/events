package ch.wisv.events.core.service.auth;

import ch.wisv.connect.common.model.CHUserInfo;
import ch.wisv.events.ServiceTest;
import ch.wisv.events.core.exception.normal.CustomerNotFoundException;
import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.service.customer.CustomerService;
import java.util.HashSet;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.mitre.openid.connect.model.OIDCAuthenticationToken;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;

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
    public void testGetChUserInfoInvalidUserInfo() {
        Authentication auth = mock(OIDCAuthenticationToken.class);
        when(((OIDCAuthenticationToken) auth).getUserInfo()).thenReturn(null);

        SecurityContextHolder.getContext().setAuthentication(auth);
        assertNull(authenticationService.getCurrentCustomer());
    }

    @Test
    public void testGetCustomerByChUserInfoSub() throws Exception {
        Customer customer = new Customer();
        customer.setSub("WISVCH.1234");

        CHUserInfo userInfo = new CHUserInfo();
        userInfo.setSub("WISVCH.1234");
        userInfo.setLdapGroups(new HashSet<>());

        Authentication auth = mock(OIDCAuthenticationToken.class);
        when(((OIDCAuthenticationToken) auth).getUserInfo()).thenReturn(userInfo);
        when(customerService.getBySub("WISVCH.1234")).thenReturn(customer);

        SecurityContextHolder.getContext().setAuthentication(auth);
        assertEquals(customer, authenticationService.getCurrentCustomer());
    }

    @Test
    public void testUpdateCustomerInfo() throws Exception {
        Customer customer = new Customer();
        CHUserInfo userInfo = new CHUserInfo();

        HashSet<String> ldapGroups = new HashSet<>();
        ldapGroups.add("BEHEER");
        ldapGroups.add("TEST");

        userInfo.setLdapGroups(ldapGroups);

        Authentication auth = mock(OIDCAuthenticationToken.class);
        when(((OIDCAuthenticationToken) auth).getUserInfo()).thenReturn(userInfo);
        when(customerService.getBySub(null)).thenThrow(CustomerNotFoundException.class);
        when(customerService.getByEmail(null)).thenThrow(CustomerNotFoundException.class);
        when(customerService.createByChUserInfo(userInfo)).thenReturn(customer);

        SecurityContextHolder.getContext().setAuthentication(auth);
        assertEquals(customer, authenticationService.getCurrentCustomer());
        assertEquals(1, customer.getLdapGroups().size());
    }
}