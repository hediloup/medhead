package com.medhead.poc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

/**
 * Configuration de sécurité pour l'API MedHead.
 * Implémente les bonnes pratiques de sécurité pour la protection des données patients.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Désactiver CSRF pour les API REST
            .csrf(AbstractHttpConfigurer::disable)
            
            // Configuration des sessions
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // Configuration des autorisations
            .authorizeHttpRequests(authz -> authz
                // Endpoints publics (sans authentification)
                .requestMatchers("/api/allocate", "/api/health").permitAll()
                
                // Endpoints de monitoring (authentification basique)
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                .requestMatchers("/actuator/**").hasRole("ADMIN")
                
                // Endpoints de gestion des patients (authentification requise)
                .requestMatchers("/api/patients/**").hasRole("MEDICAL_STAFF")
                
                // Endpoints de statistiques (authentification requise)
                .requestMatchers("/api/statistics/**").hasRole("ADMIN")
                
                // Tous les autres endpoints nécessitent une authentification
                .anyRequest().authenticated()
            )
            
            // Configuration des headers de sécurité
            .headers(headers -> headers
                .frameOptions().deny()
                .contentTypeOptions()
            )
            
            // Configuration CORS pour permettre les appels depuis le frontend
            .cors(cors -> cors
                .configurationSource(request -> {
                    var corsConfig = new org.springframework.web.cors.CorsConfiguration();
                    corsConfig.setAllowedOriginPatterns(java.util.List.of("*"));
                    corsConfig.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    corsConfig.setAllowedHeaders(java.util.List.of("*"));
                    corsConfig.setAllowCredentials(true);
                    corsConfig.setMaxAge(3600L);
                    return corsConfig;
                })
            );
        
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
