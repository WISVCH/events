package ch.wisv.events;

import com.google.common.collect.ImmutableSet;
import com.nimbusds.jose.JWSAlgorithm;
import java.util.Collections;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.mitre.oauth2.model.ClientDetailsEntity;
import org.mitre.oauth2.model.RegisteredClient;
import static org.mitre.openid.connect.client.OIDCAuthenticationFilter.FILTER_PROCESSES_URL;
import org.mitre.openid.connect.client.service.ClientConfigurationService;
import org.mitre.openid.connect.client.service.RegisteredClientService;
import org.mitre.openid.connect.client.service.impl.DynamicRegistrationClientConfigurationService;
import org.mitre.openid.connect.client.service.impl.JsonFileRegisteredClientService;
import org.mitre.openid.connect.client.service.impl.StaticClientConfigurationService;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * CHConnectConfiguration class.
 */
@Component
@ConfigurationProperties(prefix = "wisvch.connect")
@Validated
@Profile("!test")
public class ChConnectConfiguration {

    /**
     * OIDC Issuer URI; see $issuerUri/.well-known/openid-configuration.
     */
    @NotNull
    @Getter
    @Setter
    private String issuerUri;

    /**
     * URI of this application, without trailing slash.
     */
    @NotNull
    @Getter
    @Setter
    private String clientUri;

    /**
     * Groups that are admin in the system.
     */
    @NotNull
    @Getter
    @Setter
    private List<String> adminGroups;

    /**
     * List of all the users that are allowed in the beta.
     */
    @NotNull
    @Getter
    @Setter
    private List<String> betaUsers;

    /**
     * Registered oauth2 client.
     */
    @Setter
    private RegisteredClient registeredClient;

    /**
     * CHConnectConfiguration constructor.
     */
    public ChConnectConfiguration() {
        registeredClient = new RegisteredClient();
        registeredClient.setScope(ImmutableSet.of("openid", "email", "profile", "ldap"));
        registeredClient.setTokenEndpointAuthMethod(ClientDetailsEntity.AuthMethod.SECRET_BASIC);
        registeredClient.setGrantTypes(ImmutableSet.of("authorization_code"));
        registeredClient.setResponseTypes(Collections.singleton("code"));
        registeredClient.setRequestObjectSigningAlg(JWSAlgorithm.RS256);
    }

    /**
     * Get the Registerd Client.
     *
     * @return RegisteredClient
     */
    @Bean
    public RegisteredClient getRegisteredClient() {
        if (clientUri != null && registeredClient.getRedirectUris().isEmpty()) {
            registeredClient.getRedirectUris().add(clientUri + FILTER_PROCESSES_URL);
        }
        if (issuerUri != null && registeredClient.getJwksUri() == null) {
            registeredClient.setJwksUri(issuerUri + "/jwk");
        }
        return registeredClient;
    }

    /**
     * Dynamic client configuration service: this application is dynamically registered as an OIDC client when
     * authentication first occurs. This registration is persisted in a JSON file to avoid re-registration every time
     * the application is restarted.
     *
     * @return ClientConfigurationService
     */
    @Bean
    @Profile("!production")
    public ClientConfigurationService clientConfigurationService() {
        RegisteredClientService registeredClientService = new JsonFileRegisteredClientService("config/oidc-client-registration.json");

        DynamicRegistrationClientConfigurationService clientConfigurationService = new DynamicRegistrationClientConfigurationService();
        clientConfigurationService.setRegisteredClientService(registeredClientService);
        clientConfigurationService.setTemplate(getRegisteredClient());
        return clientConfigurationService;
    }

    /**
     * Static client configuration: in production, we use a client configured from Spring properties.
     *
     * @return ClientConfigurationService with a single statically configured client
     */
    @Bean
    @Profile("production")
    public ClientConfigurationService clientProdConfigurationService() {
        StaticClientConfigurationService clientConfigurationService = new StaticClientConfigurationService();
        clientConfigurationService.setClients(Collections.singletonMap(issuerUri, getRegisteredClient()));

        return clientConfigurationService;
    }
}
