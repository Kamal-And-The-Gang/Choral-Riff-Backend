package fr.afpa.choral_riff.security;

import fr.afpa.choral_riff.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtFilter jwtFilter;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService, JwtFilter jwtFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Active le CORS avec la config ci-dessous
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Désactive CSRF (car REST API sans session)
                .csrf(csrf -> csrf.disable())
                // Autorise tout pour le moment (tu pourras restreindre plus tard)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().permitAll())

                // (Optionnel) politique de sécurité de contenu
                .headers(headers -> headers
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives(
                                        "default-src 'self' http://localhost:5173; " +
                                        "script-src 'self' http://localhost:5173; " +
                                        "connect-src 'self' http://localhost:8080 http://localhost:5173; " +
                                        "img-src 'self' data: blob: http://localhost:5173; " +
                                        "style-src 'self' 'unsafe-inline' http://localhost:5173;")))
                // Ajoute le filtre JWT si tu l’utilises
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // --- Configuration CORS ---
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Origines autorisées
        config.setAllowedOrigins(List.of(
                frontendUrl,
                "https://choral-riff.com"));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
