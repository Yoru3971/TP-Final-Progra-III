package com.viandasApp.api.Config;

import com.viandasApp.api.Security.jwt.JwtAuthenticationEntryPoint;
import com.viandasApp.api.Security.jwt.JwtAuthenticationFilter;
import com.viandasApp.api.Security.jwt.JwtUtil;
import com.viandasApp.api.Security.service.UsuarioDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final UsuarioDetailsServiceImpl usuarioDetailsServiceImpl;
    private final JwtUtil jwtUtil;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return authentication -> {
            var user = usuarioDetailsServiceImpl.loadUserByUsername(authentication.getName());

            if (!user.isEnabled()) {
                throw new DisabledException("La cuenta no está activada.");
            }

            if (!user.isAccountNonLocked()) {
                throw new LockedException("La cuenta está bloqueada.");
            }

            if (!passwordEncoder().matches(authentication.getCredentials().toString(), user.getPassword())) {
                throw new BadCredentialsException("Contraseña inválida.");
            }

            return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        };
    }

    @Value("${app.cors.allowed-origins:}")
    private String extraAllowedOrigins;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter(jwtUtil, usuarioDetailsServiceImpl);

        List<String> origins = new ArrayList<>();

        origins.add("http://localhost:4200");

        if (!extraAllowedOrigins.isBlank()) {
            origins.addAll(
                Arrays.stream(extraAllowedOrigins.split(","))
                        .map(String::trim)
                        .toList()
            );
        }

        http
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfig = new org.springframework.web.cors.CorsConfiguration();
                    corsConfig.setAllowedOrigins(origins);
                    corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
                    corsConfig.setAllowedHeaders(List.of("*"));
                    corsConfig.setAllowCredentials(true);
                    return corsConfig;
                }))

                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/public/logout-all").authenticated()
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/auth/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/dueno/**").hasAnyRole("DUENO", "ADMIN")
                        .requestMatchers("/api/cliente/**").hasAnyRole("CLIENTE", "ADMIN")
                        .requestMatchers("/api/logged/**").hasAnyRole("CLIENTE", "DUENO", "ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}