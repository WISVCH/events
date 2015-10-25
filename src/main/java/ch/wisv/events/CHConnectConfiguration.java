package ch.wisv.events;

import com.google.common.collect.ImmutableSet;
import com.nimbusds.jose.JWSAlgorithm;
import org.mitre.oauth2.model.ClientDetailsEntity;
import org.mitre.oauth2.model.RegisteredClient;
import org.mitre.openid.connect.client.OIDCAuthenticationFilter;
import org.mitre.openid.connect.client.OIDCAuthenticationProvider;
import org.mitre.openid.connect.client.service.ClientConfigurationService;
import org.mitre.openid.connect.client.service.RegisteredClientService;
import org.mitre.openid.connect.client.service.ServerConfigurationService;
import org.mitre.openid.connect.client.service.impl.*;
import org.mitre.openid.connect.web.UserInfoInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.Collections;

/**
 * CH Connect / Spring Security Configuration.
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class CHConnectConfiguration extends WebSecurityConfigurerAdapter {

    /**
     * OIDC Issuer URI; see $issuerUri/.well-known/openid-configuration
     */
    @Value("${connect.issuerUri}")
    private String issuerUri;
    /**
     * URI of this application, without trailing slash
     */
    @Value("${connect.clientUri}")
    private String clientUri;
    /**
     * Login path as defined in {@link OIDCAuthenticationFilter#FILTER_PROCESSES_URL}
     */
    private final String loginPath = "/openid_connect_login";

    /**
     * Configure the {@link HttpSecurity} object.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(oidcAuthenticationFilter(), AbstractPreAuthenticatedProcessingFilter.class)
                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint())
                .and().logout().logoutSuccessUrl("/");
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
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new LoginUrlAuthenticationEntryPoint(loginPath);
    }

    @Bean
    /**
     * Register OIDC {@link UserInfoInterceptor}, which makes UserInfo available in MVC views as a request attribute.
     */
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
     */
    @Bean
    public AuthenticationProvider oidcAuthenticationProvider() {
        SimpleGrantedAuthority ROLE_ADMIN = new SimpleGrantedAuthority("ROLE_ADMIN");
        SimpleGrantedAuthority ROLE_USER = new SimpleGrantedAuthority("ROLE_USER");

        OIDCAuthenticationProvider authenticationProvider = new OIDCAuthenticationProvider();
        // TODO: implement an actual authorities mapper for production
        authenticationProvider.setAuthoritiesMapper((idToken, userInfo) -> ImmutableSet.of(ROLE_ADMIN, ROLE_USER));
        return authenticationProvider;
    }

    /**
     * OIDC authentication filter which does the actual authentication. The OIDC server and this client are
     * registered through the respective services. We support only one OIDC issuer, hence the
     * {@link StaticSingleIssuerService}.
     */
    @Bean
    public OIDCAuthenticationFilter oidcAuthenticationFilter() throws Exception {
        OIDCAuthenticationFilter oidcFilter = new OIDCAuthenticationFilter();

        oidcFilter.setAuthenticationManager(authenticationManager());

        oidcFilter.setServerConfigurationService(serverConfigurationService());
        oidcFilter.setClientConfigurationService(clientConfigurationService());

        StaticSingleIssuerService issuer = new StaticSingleIssuerService();
        issuer.setIssuer(this.issuerUri);
        oidcFilter.setIssuerService(issuer);

        // TODO: for production, sign or encrypt requests
        oidcFilter.setAuthRequestUrlBuilder(new PlainAuthRequestUrlBuilder());

        return oidcFilter;
    }

    /**
     * Dynamic client configuration service: this application is dynamically registered as an OIDC client when
     * authentication first occurs. This registration is persisted in a JSON file to avoid re-registration every time
     * the application is restarted.
     * <p>
     * TODO: for production, we want a statically configured client
     */
    @Bean
    public ClientConfigurationService clientConfigurationService() {
        RegisteredClient client = new RegisteredClient();
        client.setClientName("Events Development");
        client.setScope(ImmutableSet.of("openid", "email", "phone", "profile"));
        client.setTokenEndpointAuthMethod(ClientDetailsEntity.AuthMethod.SECRET_BASIC);
        client.setRedirectUris(Collections.singleton(clientUri + loginPath));
        client.setRequestObjectSigningAlg(JWSAlgorithm.RS256);
        client.setJwksUri(clientUri + "/jwk");

        RegisteredClientService registeredClientService = new JsonFileRegisteredClientService
                ("oidc-client-registration.json");

        DynamicRegistrationClientConfigurationService clientConfigurationService = new
                DynamicRegistrationClientConfigurationService();
        clientConfigurationService.setRegisteredClientService(registeredClientService);
        clientConfigurationService.setTemplate(client);
        return clientConfigurationService;
    }

    /**
     * Dynamic server configuration service: the information at $issuerUri/.well-known/openid-configuration is used
     * to configure the OIDC server.
     */
    @Bean
    public ServerConfigurationService serverConfigurationService() {
        DynamicServerConfigurationService serverConfigurationService = new DynamicServerConfigurationService();
        serverConfigurationService.setWhitelist(Collections.singleton(issuerUri));
        return serverConfigurationService;
    }
}
