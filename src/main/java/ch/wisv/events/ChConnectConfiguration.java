package ch.wisv.events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


/**
 * CHConnectConfiguration class.
 */
@Component
@ConfigurationProperties(prefix = "wisvch.connect")
@Validated
@Profile("!test")
public class ChConnectConfiguration extends WebSecurityConfigurerAdapter {

    /**
     * Groups that are admin in the system.
     */
    @Getter
    @Setter
    private List<String> adminGroups;

    /**
     * List of all the users that are allowed in the beta.
     */
    @Getter
    @Setter
    private List<String> betaUsers;

    public void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().permitAll()
                .and()
                .oauth2Login().userInfoEndpoint().oidcUserService(oidcUserService());
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }

    private OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        return (userRequest) -> {
            SimpleGrantedAuthority ROLE_ADMIN = new SimpleGrantedAuthority("ROLE_ADMIN");
            SimpleGrantedAuthority ROLE_USER = new SimpleGrantedAuthority("ROLE_USER");
            OidcIdToken idToken = userRequest.getIdToken();
            Collection<String> groups = (Collection<String>) idToken.getClaims().get("ldap_groups");
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(ROLE_USER);
            if(groups.stream().anyMatch(o -> adminGroups.contains(o))) {
                authorities.add(ROLE_ADMIN);
            }
            return new DefaultOidcUser(authorities, idToken);
        };
    }
}
