package com.example.serviceBookBackend.security;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${CORS_ALLOWED_ORIGIN}")
    private String allowedOrigin;

    private final JWTAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // публічні
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/cars/*/photo").permitAll()
                        // каталог послуг: читати можуть усі авторизовані, змінювати - тільки OPERATOR
                        .requestMatchers(HttpMethod.GET, "/api/services-catalog/**").authenticated()
                        .requestMatchers("/api/services-catalog/**").hasRole("OPERATOR")
                        // всі авто - тільки SERVICE
                        .requestMatchers("/api/cars/all-cars").hasRole("SERVICE")
                        // оплата - тільки OWNER
                        .requestMatchers("/api/performed-maintenance/*/pay").hasRole("OWNER")
                        // додавання обслуговування - SERVICE
                        .requestMatchers(HttpMethod.POST, "/api/performed-maintenance/create").hasRole("SERVICE")
                        .requestMatchers(HttpMethod.POST, "/api/maintenance-jobs/create").hasRole("SERVICE")
                        // список клієнтів - тільки SERVICE
                        .requestMatchers("/api/users/clients").hasRole("SERVICE")
                        // машини клієнта - тільки OWNER
                        .requestMatchers("/api/cars/exist-cars").hasRole("OWNER")
                        // додавання авто: OWNER або SERVICE
                        .requestMatchers(HttpMethod.POST, "/api/cars/create").hasAnyRole("OWNER", "SERVICE")
                        .requestMatchers(HttpMethod.PUT, "/api/cars/update").hasRole("OWNER")
                        .requestMatchers(HttpMethod.DELETE, "/api/cars/delete/**").hasRole("OWNER")
                        .requestMatchers(HttpMethod.PATCH, "/api/cars/update/odometer").hasAnyRole("OWNER", "SERVICE")
                        // решта - будь-яка авторизація
                        .anyRequest().authenticated()
                )
                // 2. ТвійEntryPoint для 401
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"status\": \"401\", \"message\": \"Сесія користувача завершилась\"}");
                        })
                )
                // 3. Твій фільтр
                .addFilterBefore(jwtAuthFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of(allowedOrigin));

        configuration.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));

        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}