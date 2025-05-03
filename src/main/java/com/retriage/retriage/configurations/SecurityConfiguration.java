package com.retriage.retriage.configurations;

import com.retriage.retriage.exceptions.SamlAuthenticationSuccessHandler;
import jakarta.servlet.http.Cookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.saml2.provider.service.authentication.OpenSaml4AuthenticationProvider;
import org.springframework.security.saml2.provider.service.authentication.OpenSaml4AuthenticationProvider.ResponseToken;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Configures Spring Security for SAML 2.0 Service Provider functionality.
 * This class defines the security filter chain and customizes the SAML authentication provider
 * to handle user group/role mapping from SAML attributes to Spring Security authorities.
 * <p>
 * Help provided by Matt Raible (<a href="https://developer.okta.com/blog/2022/08/05/spring-boot-saml">https://developer.okta.com/blog/2022/08/05/spring-boot-saml</a>)
 *
 * @author John Botonakis
 * @author Matt Raible // Or include the contribution note here if preferred
 */

@Configuration
public class SecurityConfiguration {
    private final SamlAuthenticationSuccessHandler samlAuthenticationSuccessHandler;
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfiguration.class);

    /**
     * Constructor to inject the custom SAML authentication success handler.
     *
     * @param samlAuthenticationSuccessHandler the success handler for SAML login
     */
    public SecurityConfiguration(SamlAuthenticationSuccessHandler samlAuthenticationSuccessHandler) {
        this.samlAuthenticationSuccessHandler = samlAuthenticationSuccessHandler;
    }


    /**
     * Configures Spring Security filter chain for SAML authentication.
     * Sets up access rules, login/logout behavior, security headers, and
     * a custom authentication provider with group mapping.
     *
     * @param http the {@link HttpSecurity} to configure
     * @return configured {@link SecurityFilterChain}
     * @throws Exception if configuration fails
     */
    @Bean
    SecurityFilterChain configure(HttpSecurity http) throws Exception {

        // Create a custom authentication provider for SAML 2.0
        OpenSaml4AuthenticationProvider authenticationProvider = new OpenSaml4AuthenticationProvider();
        // Set the custom response authentication converter to handle group/role mapping
        authenticationProvider.setResponseAuthenticationConverter(groupsConverter());

        // Configure HTTP security settings
        http
                .csrf(AbstractHttpConfigurer::disable) // Disables CSRF for API access
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/topic/**").permitAll()
                        .requestMatchers("/active_event").permitAll()
                        .requestMatchers( "favicon.ico","/api/debug/cookies").permitAll()
                        .requestMatchers("/index.html").authenticated()
                        .anyRequest().authenticated() // All other endpoints require authentication
                )
                // Login Settings
                .saml2Login(saml2 -> saml2
                        .authenticationManager(new ProviderManager(authenticationProvider)) // Maps SAML groups to Roles
                        .successHandler(samlAuthenticationSuccessHandler) // Sets JWT after successful login
                )
                // Logout Settings
                .saml2Logout(Customizer.withDefaults()) // Enable default SAML logout handling
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "POST"))
                        .addLogoutHandler((request, response, authentication) -> {
                            Cookie jwtCookie = new Cookie("token", null);
                            jwtCookie.setPath("/");
                            jwtCookie.setHttpOnly(true);
                            jwtCookie.setSecure(false); // ✅ for localhost
                            jwtCookie.setMaxAge(0);
                            response.addCookie(jwtCookie);
                        })
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.sendRedirect("https://dev-32534403.okta.com/login/signout");
                        })


                )
                // Security Headers
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny) // Clickjacking protection
                        .contentTypeOptions(withDefaults()) // Prevent MIME sniffing
                        .referrerPolicy(referrer -> referrer
                                .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.NO_REFERRER)) // Hide reefer
                );


        return http.build(); // Build and return the configured SecurityFilterChain
    }

    /**
     * Maps the "groups" SAML attribute to a list of {@link GrantedAuthority} objects.
     * If "groups" is absent, falls back to default authorities from the SAML provider.
     *
     * @return a converter from {@link ResponseToken} to {@link Saml2Authentication}
     */
    private Converter<OpenSaml4AuthenticationProvider.ResponseToken, Saml2Authentication> groupsConverter() {
        // Base converter from ResponseToken to Saml2Authentication
        Converter<ResponseToken, Saml2Authentication> delegate =
                OpenSaml4AuthenticationProvider.createDefaultResponseAuthenticationConverter();

        return (responseToken) -> {
            // Default conversion
            Saml2Authentication authentication = delegate.convert(responseToken);
            assert authentication != null; // Ensure authentication isn't null
            // Get user details from SAML response
            Saml2AuthenticatedPrincipal principal = (Saml2AuthenticatedPrincipal) authentication.getPrincipal();
            List<String> groups = principal.getAttribute("groups"); // Extract "groups" from attributes

            Set<GrantedAuthority> authorities = new HashSet<>();
            if (groups != null) {
                // Convert group names to Spring Security authorities
                groups.stream()
                        .map(SimpleGrantedAuthority::new)
                        .forEach(authorities::add);
            } else {
                // Use default authorities if no group info is provided
                authorities.addAll(authentication.getAuthorities());
            }

            // Return new authentication with custom authorities
            return new Saml2Authentication(principal, authentication.getSaml2Response(), authorities);
        };
    }
}