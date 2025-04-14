package tn.fst.spring.projet_spring.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tn.fst.spring.projet_spring.security.jwt.JwtAuthenticationFilter;
import tn.fst.spring.projet_spring.security.jwt.CustomAuthEntryPoint;
import tn.fst.spring.projet_spring.security.jwt.JwtAuthEntryPoint;
import tn.fst.spring.projet_spring.security.jwt.CustomAccessDeniedHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;

    @Autowired
    private CustomAuthEntryPoint customAuthEntryPoint;

    @Autowired
    private JwtAuthEntryPoint jwtAuthEntryPoint;

    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * 🔓 Chaîne pour /api/auth/** : tout est public (login, register, refresh)
     */
    @Bean
    @Order(1)
    public SecurityFilterChain authSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/auth/**")
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(eh -> eh.authenticationEntryPoint(customAuthEntryPoint))
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );

        return http.build();
    }

    /**
     * 🔐 Chaîne principale sécurisée pour toutes les autres routes
     */
    @Bean
    @Order(2)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/**")
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Swagger
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html", "/api-docs/**").permitAll()

                        // Produits
                        .requestMatchers(HttpMethod.GET, "/api/products").hasAnyRole("ADMIN", "PRODUCT_MANAGER", "CUSTOMER")
                        .requestMatchers(HttpMethod.GET, "/api/products/*").hasAnyRole("ADMIN", "PRODUCT_MANAGER", "CUSTOMER")
                        .requestMatchers(HttpMethod.GET, "/api/products/search").hasAnyRole("ADMIN", "PRODUCT_MANAGER", "CUSTOMER")
                        .requestMatchers(HttpMethod.GET, "/api/products/verify-barcode/*").hasAnyRole("ADMIN", "PRODUCT_MANAGER", "CUSTOMER")
                        .requestMatchers(HttpMethod.POST, "/api/products").hasAnyRole("ADMIN", "PRODUCT_MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/products/*").hasAnyRole("ADMIN", "PRODUCT_MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/*").hasAnyRole("ADMIN", "PRODUCT_MANAGER")

                        // Extraction de code-barres depuis une image
                        .requestMatchers(HttpMethod.POST, "/api/products/extract-barcode").hasAnyRole("ADMIN", "PRODUCT_MANAGER", "CUSTOMER")
                        .requestMatchers(HttpMethod.POST, "/api/products/extract-product").hasAnyRole("ADMIN", "PRODUCT_MANAGER", "CUSTOMER")

                        // Utilisateur connecté
                        .requestMatchers(HttpMethod.GET, "/api/users/me").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/users/me").authenticated()

                        // Admin uniquement
                        .requestMatchers("/api/users/**").hasRole("ADMIN")

                        // Autres routes → protégées
                        .anyRequest().authenticated()
                )
                .exceptionHandling(eh -> eh
                        .authenticationEntryPoint(jwtAuthEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
