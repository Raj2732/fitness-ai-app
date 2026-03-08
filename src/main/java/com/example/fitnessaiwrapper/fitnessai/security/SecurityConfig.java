package com.example.fitnessaiwrapper.fitnessai.security;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        // Allow all static resources
                        .requestMatchers("/", "/index.html", "/css/**", "/js/**", "/images/**").permitAll()
                        // Allow all API endpoints
                        .requestMatchers("/api/**").permitAll()
                        // Allow H2 console
                        .requestMatchers("/h2-console/**").permitAll()
                        // Allow login page
                        .requestMatchers("/login").permitAll()
                        // Everything else requires authentication
                        .anyRequest().authenticated()
                )
                // Disable CSRF for API testing
                .csrf(csrf -> csrf.disable())
                // Allow frames for H2 console
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));

        return http.build();
    }
}

