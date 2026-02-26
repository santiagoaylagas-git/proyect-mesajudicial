package com.sojus.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${app.cors.allowed-origins:http://localhost:3000,http://localhost:8081,http://localhost:19006}")
    private String allowedOrigins;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // --- Públicos ---
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/swagger-ui/**", "/api-docs/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()

                        // --- Auditoría: solo ADMINISTRADOR ---
                        .requestMatchers("/api/audit/**").hasRole("ADMINISTRADOR")

                        // --- Usuarios: solo ADMINISTRADOR (CRUD completo) ---
                        .requestMatchers("/api/users/**").hasRole("ADMINISTRADOR")

                        // --- Dashboard: ADMINISTRADOR u OPERADOR ---
                        .requestMatchers("/api/dashboard/**").hasAnyRole("ADMINISTRADOR", "OPERADOR")

                        // --- Tickets ---
                        .requestMatchers(HttpMethod.GET, "/api/tickets/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/tickets/**").hasAnyRole("ADMINISTRADOR", "OPERADOR")
                        .requestMatchers(HttpMethod.PATCH, "/api/tickets/**").hasAnyRole("ADMINISTRADOR", "TECNICO")
                        .requestMatchers(HttpMethod.DELETE, "/api/tickets/**").hasRole("ADMINISTRADOR")

                        // --- Inventario: lectura todos, escritura ADMIN/TECNICO ---
                        .requestMatchers(HttpMethod.GET, "/api/inventory/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/inventory/**").hasAnyRole("ADMINISTRADOR", "TECNICO")
                        .requestMatchers(HttpMethod.PUT, "/api/inventory/**").hasAnyRole("ADMINISTRADOR", "TECNICO")
                        .requestMatchers(HttpMethod.DELETE, "/api/inventory/**").hasAnyRole("ADMINISTRADOR", "TECNICO")

                        // --- Contratos: lectura ADMIN/OPERADOR, escritura solo ADMIN ---
                        .requestMatchers(HttpMethod.GET, "/api/contracts/**").hasAnyRole("ADMINISTRADOR", "OPERADOR")
                        .requestMatchers(HttpMethod.POST, "/api/contracts/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/contracts/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/contracts/**").hasRole("ADMINISTRADOR")

                        // --- Ubicaciones: lectura para todos ---
                        .requestMatchers(HttpMethod.GET, "/api/locations/**").authenticated()

                        // --- Todo lo demás requiere autenticación ---
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // Para H2 console
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
