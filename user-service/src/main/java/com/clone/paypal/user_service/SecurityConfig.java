package com.clone.paypal.user_service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for stateless APIs
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/users/register", "/api/users/login", "/api/users/me", "/api/users/email/**", "/api/users/search", "/api/users/analytics/**", "/api/requests/**", "/api/users/**").permitAll() // Allow public access
                        .anyRequest().permitAll() // Allow all requests for now (you can secure later)
                )
                .cors(cors -> cors.disable()) // Disable CORS (handled by API Gateway)
                .headers(headers -> headers.frameOptions().disable()); // Allow frames for development
        return http.build();
    }
}