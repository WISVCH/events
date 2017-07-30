package ch.wisv.events;

import com.google.common.collect.ImmutableSet;
import com.nimbusds.jose.JWSAlgorithm;
import org.mitre.oauth2.model.ClientDetailsEntity;
import org.mitre.oauth2.model.RegisteredClient;
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

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

import static org.mitre.openid.connect.client.OIDCAuthenticationFilter.FILTER_PROCESSES_URL;

@Component
@ConfigurationProperties(prefix = "wisvch.connect")
@Validated
@Profile("!test")
public class CHConnectConfiguration {

    /**
     * OIDC Issuer URI; see $issuerUri/.well-known/openid-configuration
     */
    @NotNull
    private String issuerUri;

    /**
     * URI of this application, without trailing slash
     */
    @NotNull
    private String clientUri;

    /**
     * Groups that are admin in the system
     */
    private List<String> adminGroups;

    private RegisteredClient registeredClient;

    public CHConnectConfiguration() {
        registeredClient = new RegisteredClient();
        registeredClient.setScope(ImmutableSet.of("openid", "email", "profile", "ldap", "offline_access"));
        registeredClient.setTokenEndpointAuthMethod(ClientDetailsEntity.AuthMethod.SECRET_BASIC);
        registeredClient.setGrantTypes(ImmutableSet.of("refresh_token", "authorization_code"));
        registeredClient.setResponseTypes(Collections.singleton("code"));
        registeredClient.setRequestObjectSigningAlg(JWSAlgorithm.RS256);
    }

    public String getIssuerUri() {
        return issuerUri;
    }

    public void setIssuerUri(String issuerUri) {
        this.issuerUri = issuerUri;
    }

    public String getClientUri() {
        return clientUri;
    }

    public void setClientUri(String clientUri) {
        this.clientUri = clientUri;
    }

    public List<String> getAdminGroups() {
        return adminGroups;
    }

    public void setAdminGroups(List<String> adminGroups) {
        this.adminGroups = adminGroups;
    }

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

    public void setRegisteredClient(RegisteredClient registeredClient) {
        this.registeredClient = registeredClient;
    }

    /**
     * Dynamic client configuration service: this application is dynamically registered as an OIDC client when
     * authentication first occurs. This registration is persisted in a JSON file to avoid re-registration every time
     * the application is restarted.
     *
     * @return ClientConfigurationService
     */
    @Bean
    @Profile("!prod")
    public ClientConfigurationService clientConfigurationService() {
        RegisteredClientService registeredClientService = new JsonFileRegisteredClientService("config/oidc-client-registration.json");

        DynamicRegistrationClientConfigurationService clientConfigurationService = new
                DynamicRegistrationClientConfigurationService();
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
    @Profile("prod")
    public ClientConfigurationService clientProdConfigurationService() {
        StaticClientConfigurationService clientConfigurationService = new StaticClientConfigurationService();
        clientConfigurationService.setClients(Collections.singletonMap(issuerUri, getRegisteredClient()));

        return clientConfigurationService;
    }
}