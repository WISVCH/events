package ch.wisv.events.core.service.auth;

import ch.wisv.events.ServiceTest;
import ch.wisv.events.core.exception.normal.CustomerNotFoundException;
import ch.wisv.events.core.model.customer.Customer;
import ch.wisv.events.core.service.customer.CustomerService;

import java.time.Instant;
import java.util.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.thymeleaf.extras.springsecurity5.auth.Authorization;

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
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("sub", "WISVCH.1234");
        OidcUserInfo userInfo = new OidcUserInfo(claims);
        when(auth.getPrincipal()).thenReturn(new DefaultOidcUser(null,new OidcIdToken("11", Instant.MIN, Instant.MAX, claims), userInfo));

        SecurityContextHolder.getContext().setAuthentication(auth);
        assertNull(authenticationService.getCurrentCustomer());
    }

    @Test
    public void testGetCustomerByChUserInfoSub() throws Exception {
        Customer customer = new Customer();
        customer.setSub("WISVCH.1234");


        HashMap<String, Object> claims = new HashMap<>();
        claims.put("sub", "WISVCH.1234");
        claims.put("email", "email");
        claims.put("given_name", "name");
        claims.put("ldap_groups", new HashSet<>());
        OidcUserInfo userInfo = new OidcUserInfo(claims);

        OidcUserAuthority auth = mock(OidcUserAuthority.class);
        when(auth.getUserInfo()).thenReturn(userInfo);
        when(customerService.getBySub("WISVCH.1234")).thenReturn(customer);

        Authentication auth2 = mock(Authentication.class);
        when(auth2.getPrincipal()).thenReturn(new DefaultOidcUser(null,new OidcIdToken("11", Instant.MIN, Instant.MAX, claims), userInfo));

        SecurityContextHolder.getContext().setAuthentication(auth2);
        assertEquals(customer, authenticationService.getCurrentCustomer());
    }

    @Test
    public void testReplaceUserEmail() throws Exception {
        Customer customer = new Customer();
        customer.setSub("WISVCH.1234");
        customer.setEmail("notEmail");

        HashMap<String, Object> claims = new HashMap<>();
        claims.put("sub", "WISVCH.1234");
        claims.put("email", "email");
        claims.put("given_name", "name");
        claims.put("ldap_groups", new HashSet<>());
        OidcUserInfo userInfo = new OidcUserInfo(claims);

        Customer oneTimeOrderCustomer = new Customer();
        oneTimeOrderCustomer.setEmail("email");

        OidcUserAuthority auth = mock(OidcUserAuthority.class);
        when(auth.getUserInfo()).thenReturn(userInfo);
        when(customerService.getBySub("WISVCH.1234")).thenReturn(customer);
        when(customerService.getByEmail("email")).thenReturn(oneTimeOrderCustomer);

        Authentication auth2 = mock(Authentication.class);
        when(auth2.getPrincipal()).thenReturn(new DefaultOidcUser(null,new OidcIdToken("11", Instant.MIN, Instant.MAX, claims), userInfo));

        SecurityContextHolder.getContext().setAuthentication(auth2);
        assertEquals(customer, authenticationService.getCurrentCustomer());
        assertNotEquals("email", oneTimeOrderCustomer.getEmail());
        assertEquals("email", customer.getEmail());
    }
}