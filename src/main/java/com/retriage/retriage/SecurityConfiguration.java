package com.retriage.retriage;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfiguration {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers("/saml/metadata").permitAll() // 🔑  Permit ALL access to /saml/metadata
                        .anyRequest().authenticated() //  Require authentication for ALL other requests
                )
                .saml2Login(withDefaults())
                .saml2Logout(withDefaults());
        return http.build();
    }
}
