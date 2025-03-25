package com.retriage.retriage.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.saml2.provider.service.authentication.OpenSaml4AuthenticationProvider;
import org.springframework.security.saml2.provider.service.authentication.OpenSaml4AuthenticationProvider.ResponseToken;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.security.config.Customizer.withDefaults;


/**
 * SecurityConfiguration
 * <br></br>
 * Configures Spring Security for SAML 2.0 Service Provider functionality.
 * This class defines the security filter chain and customizes the SAML authentication provider
 * to handle user group/role mapping from SAML attributes to Spring Security authorities.
 *
 * @Author: John Botonakis
 * @PatientPool: With help provided by Matt Raible (https://developer.okta.com/blog/2022/08/05/spring-boot-saml)
 */
@Configuration
public class SecurityConfiguration {

    /**
     * configure
     * <br></br>
     * Configures the Spring Security filter chain for handling SAML authentication and authorization.
     * <p>
     * This method defines the security rules and sets up the SAML 2.0 login and logout processes.
     * It also configures a custom {@link OpenSaml4AuthenticationProvider} with a {@link #groupsConverter()}
     * to map SAML attributes to Spring Security GrantedAuthorities.
     *
     * @param http the {@link HttpSecurity} to configure
     * @return a {@link SecurityFilterChain} that is configured for SAML 2.0 security
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    SecurityFilterChain configure(HttpSecurity http) throws Exception {

        // Create a custom authentication provider for SAML 2.0
        OpenSaml4AuthenticationProvider authenticationProvider = new OpenSaml4AuthenticationProvider();
        // Set the custom response authentication converter to handle group/role mapping
        authenticationProvider.setResponseAuthenticationConverter(groupsConverter());

        //Authentication begins here
        // Configure HTTP security settings
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/topic/**").permitAll()
                        .requestMatchers("/active_event/**").permitAll()
                        .anyRequest().authenticated()) // Require authentication for any request to this application
                .saml2Login(saml2 -> saml2
                .authenticationManager(new ProviderManager(authenticationProvider)) // Use the custom SAML authentication provider
                        .defaultSuccessUrl("/", true)) // <- Forces redirect to home after successful login)
                //Logout Settings
                .saml2Logout(withDefaults())
                .logout(logout -> logout
                        .logoutUrl("/logout") // This is where Spring Security listens for logout requests
                        .logoutSuccessUrl("/") // Redirect after logout
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")); // Enable SAML Logout with default configurations

        return http.build(); // Build and return the configured SecurityFilterChain
    }

    /**
     * Define a CorsConfigurationSource bean that allows CORS requests from any origin.
     * For production, consider restricting origins and methods as needed.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Allow all origins. You can restrict this by listing specific origins.
        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true); // Allow cookies/credentials if needed

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Converter
     * <br></br>
     * Creates a custom converter to map SAML attributes (specifically "groups") to Spring Security GrantedAuthorities.
     * <p>
     * This converter is used by the {@link OpenSaml4AuthenticationProvider} to process SAML responses.
     * It extracts the "groups" attribute from the SAML assertion and converts each group name into a {@link SimpleGrantedAuthority}.
     * If the "groups" attribute is not present, it falls back to the default authorities provided by the SAML authentication.
     *
     * @return a {@link Converter} that maps {@link ResponseToken} to {@link Saml2Authentication},
     * extracting group information from SAML attributes to populate authorities.
     */
    private Converter<OpenSaml4AuthenticationProvider.ResponseToken, Saml2Authentication> groupsConverter() {
        // Create a default ResponseToken to Saml2Authentication converter as a base
        Converter<ResponseToken, Saml2Authentication> delegate =
                OpenSaml4AuthenticationProvider.createDefaultResponseAuthenticationConverter();

        // Return a lambda converter that enhances the default conversion
        return (responseToken) -> {
            // Perform the default conversion to get basic Saml2Authentication
            Saml2Authentication authentication = delegate.convert(responseToken);
            assert authentication != null; // Assertion to ensure authentication is not null after default conversion
            // Get the principal from the authentication, which is a Saml2AuthenticatedPrincipal
            Saml2AuthenticatedPrincipal principal = (Saml2AuthenticatedPrincipal) authentication.getPrincipal();
            // Extract the "groups" attribute from the principal's attributes. Expecting a List of group names.
            List<String> groups = principal.getAttribute("groups");
            // Initialize a Set to hold GrantedAuthorities (roles/permissions)
            Set<GrantedAuthority> authorities = new HashSet<>();
            // If groups attribute is present in the SAML response
            if (groups != null) {
                // Stream through each group name, map it to a SimpleGrantedAuthority, and add to the authorities set
                groups.stream()
                        .map(SimpleGrantedAuthority::new) // Convert each group name to SimpleGrantedAuthority
                        .forEach(authorities::add);       // Add to the set of authorities
            } else {
                // If "groups" attribute is not present, use the default authorities from SAML authentication
                authorities.addAll(authentication.getAuthorities());
            }
            // Return a new Saml2Authentication object, with the same principal and SAML response, but with the custom authorities set.
            return new Saml2Authentication(principal, authentication.getSaml2Response(), authorities);
        };
    }
}