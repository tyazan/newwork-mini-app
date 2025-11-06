package com.tekin.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // use the CorsConfigurationSource bean you already have
                .cors(Customizer.withDefaults())
                // simple stateless API setup
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/health", "/h2-console/**").permitAll()
                        .requestMatchers("/api/**").permitAll()
                        .anyRequest().permitAll()
                )
                // allow H2 console to render in a frame
                .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }
}
