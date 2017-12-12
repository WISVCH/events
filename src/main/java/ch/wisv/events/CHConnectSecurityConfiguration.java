package ch.wisv.events;

import ch.wisv.connect.client.CHUserInfoFetcher;
import ch.wisv.connect.common.model.CHUserInfo;
import com.google.common.collect.ImmutableSet;
import org.mitre.openid.connect.client.OIDCAuthenticationFilter;
import org.mitre.openid.connect.client.OIDCAuthenticationProvider;
import org.mitre.openid.connect.client.service.ClientConfigurationService;
import org.mitre.openid.connect.client.service.ServerConfigurationService;
import org.mitre.openid.connect.client.service.impl.DynamicServerConfigurationService;
import org.mitre.openid.connect.client.service.impl.PlainAuthRequestUrlBuilder;
import org.mitre.openid.connect.client.service.impl.StaticSingleIssuerService;
import org.mitre.openid.connect.web.UserInfoInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.Collections;

import static org.mitre.openid.connect.client.OIDCAuthenticationFilter.FILTER_PROCESSES_URL;

/**
 * CH Connect Security Configuration
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
@Profile("!test")
public class CHConnectSecurityConfiguration extends WebSecurityConfigurerAdapter {
    private final CHConnectConfiguration properties;
    private final ClientConfigurationService clientConfigurationService;

    public CHConnectSecurityConfiguration(CHConnectConfiguration properties, ClientConfigurationService clientConfigurationService) {
        this.properties = properties;
        this.clientConfigurationService = clientConfigurationService;
    }

    /**
     * Configure the {@link HttpSecurity} object.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(oidcAuthenticationFilter(), AbstractPreAuthenticatedProcessingFilter.class)
                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint())
                .and()
                .logout()
                .logoutSuccessUrl("/");
    }

    /**
     * Configure OIDC authentication provider in manager.
     */
    @Autowired
    public void configureAuthenticationProvider(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(oidcAuthenticationProvider());
    }

    /**
     * Configure the OIDC login path as our authentication entry point.
     *
     * @return AuthenticationEntryPoint
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new LoginUrlAuthenticationEntryPoint(FILTER_PROCESSES_URL);
    }

    /**
     * Register OIDC {@link UserInfoInterceptor}, which makes UserInfo available in MVC views as a request attribute.
     *
     * @return WebMvcConfigurerAdapter
     */
    @Bean
    public WebMvcConfigurerAdapter mvcInterceptor() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(new UserInfoInterceptor());
            }
        };
    }

    /**
     * OIDC authentication provider with authorities mapper which assigns authorities (roles) to users when they log in.
     *
     * @return AuthenticationProvider
     */
    @Bean
    public AuthenticationProvider oidcAuthenticationProvider() {
        // TODO: fully configurable roles
        SimpleGrantedAuthority ROLE_ADMIN = new SimpleGrantedAuthority("ROLE_ADMIN");
        SimpleGrantedAuthority ROLE_USER = new SimpleGrantedAuthority("ROLE_USER");

        OIDCAuthenticationProvider authenticationProvider = new OIDCAuthenticationProvider();
        authenticationProvider.setUserInfoFetcher(new CHUserInfoFetcher());

        authenticationProvider.setAuthoritiesMapper((idToken, userInfo) -> {
            if (userInfo instanceof CHUserInfo) {
                CHUserInfo info = (CHUserInfo) userInfo;

                if (properties.getAdminGroups().stream().anyMatch(info.getLdapGroups()::contains)) {
                    return ImmutableSet.of(ROLE_ADMIN, ROLE_USER);
                } else if (properties.getBetaUsers().stream().anyMatch(info.getLdapUsername()::contains)) {
                    return ImmutableSet.of(ROLE_USER);
                } else {
                    return ImmutableSet.of();
                }
            }
            throw new AccessDeniedException("Invalid user info!");
        });

        return authenticationProvider;
    }

    /**
     * OIDC authentication filter which does the actual authentication. The OIDC server and this client are
     * registered through the respective services. We support only one OIDC issuer, hence the
     * {@link StaticSingleIssuerService}.
     *
     * @return OIDCAuthenticationFilter
     */
    @Bean
    public OIDCAuthenticationFilter oidcAuthenticationFilter() throws Exception {
        OIDCAuthenticationFilter oidcFilter = new OIDCAuthenticationFilter();

        oidcFilter.setAuthenticationManager(authenticationManager());

        oidcFilter.setServerConfigurationService(serverConfigurationService());
        oidcFilter.setClientConfigurationService(clientConfigurationService);

        StaticSingleIssuerService issuer = new StaticSingleIssuerService();
        issuer.setIssuer(properties.getIssuerUri());
        oidcFilter.setIssuerService(issuer);

        // TODO: for production, sign or encrypt requests
        oidcFilter.setAuthRequestUrlBuilder(new PlainAuthRequestUrlBuilder());

        return oidcFilter;
    }

    /**
     * Dynamic server configuration service: the information at $issuerUri/.well-known/openid-configuration is used
     * to configure the OIDC server.
     *
     * @return ServerConfigurationService
     */
    @Bean
    public ServerConfigurationService serverConfigurationService() {
        DynamicServerConfigurationService serverConfigurationService = new DynamicServerConfigurationService();
        serverConfigurationService.setWhitelist(Collections.singleton(properties.getIssuerUri()));
        return serverConfigurationService;
    }

}