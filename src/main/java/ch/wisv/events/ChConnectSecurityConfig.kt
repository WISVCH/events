package ch.wisv.events

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.oidc.OidcIdToken
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource


/**
 * CH Connect Security Configuration
 */
@Configuration
@EnableWebSecurity
@ConfigurationProperties("wisvch.connect")
@Profile("!test")
open class CHConnectSecurityConfiguration : WebSecurityConfigurerAdapter() {
    private var adminGroups: MutableSet<String?>? = null
    fun setAdminGroups(adminGroups: MutableSet<String?>?) {
        this.adminGroups = adminGroups
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity?) {
        http
            ?.cors()
            ?.and()
            ?.csrf()?.disable()
            ?.authorizeRequests()
            ?.antMatchers("/admin/**")?.hasRole("ADMIN")
            ?.anyRequest()?.permitAll()
            ?.and()
            ?.oauth2Login()?.userInfoEndpoint()?.oidcUserService(oidcUserService())
    }

    @Bean
    open fun corsConfigurationSource(): CorsConfigurationSource? {
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", CorsConfiguration().applyPermitDefaultValues())
        return source
    }

    private fun oidcUserService(): OAuth2UserService<OidcUserRequest?, OidcUser?>? {
        return OAuth2UserService<OidcUserRequest?, OidcUser?> { userRequest ->
            val ROLE_ADMIN = SimpleGrantedAuthority("ROLE_ADMIN")
            val ROLE_USER = SimpleGrantedAuthority("ROLE_USER")
            val idToken: OidcIdToken = userRequest!!.idToken
            val groups = idToken.claims["ldap_groups"] as MutableCollection<String>
            val authorities: MutableList<SimpleGrantedAuthority?> = if (groups.stream().anyMatch { o: String? -> adminGroups!!.contains(o) }) mutableListOf(ROLE_ADMIN, ROLE_USER) else mutableListOf(ROLE_USER)
            DefaultOidcUser(authorities, idToken)
        }
    }
}
