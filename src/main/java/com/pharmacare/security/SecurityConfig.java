package com.pharmacare.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.SecurityFilterChain;

import java.nio.charset.StandardCharsets;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(withDefaults())
                .securityContext(sc -> sc.requireExplicitSave(false))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(eh -> eh
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                            Object jwtError = request.getAttribute("jwt_error");
                            String jwtErrorMsg = jwtError == null ? null : String.valueOf(jwtError).replace("\"", "'");

                            String authHeader = request.getHeader(org.springframework.http.HttpHeaders.AUTHORIZATION);
                            boolean hasAuthHeader = authHeader != null && !authHeader.isBlank();
                            boolean bearer = authHeader != null && authHeader.startsWith("Bearer ");
                            boolean filterSeen = Boolean.TRUE.equals(request.getAttribute("jwt_filter_seen"));
                            Object existingType = request.getAttribute("existing_auth_type");
                            Object existingPrincipal = request.getAttribute("existing_auth_principal");
                            Object jwtUsername = request.getAttribute("jwt_username");
                            boolean jwtAuthSet = Boolean.TRUE.equals(request.getAttribute("jwt_auth_set"));
                            Object finalAuthTypeAttr = request.getAttribute("final_auth_type");
                            Object finalAuthPrincipalAttr = request.getAttribute("final_auth_principal");
                            Object finalAuthIsAuthenticatedAttr = request.getAttribute("final_auth_is_authenticated");
                            Object finalAuthAuthoritiesAttr = request.getAttribute("final_auth_authorities");
                            Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
                            String currentAuthType = currentAuth == null ? null : currentAuth.getClass().getName();
                            Object currentPrincipal = currentAuth == null ? null : currentAuth.getPrincipal();
                            boolean currentIsAuthenticated = currentAuth != null && currentAuth.isAuthenticated();
                            Object currentAuthorities = currentAuth == null ? null : currentAuth.getAuthorities();

                            String msg = "Missing or invalid token";
                            if (jwtErrorMsg != null && !jwtErrorMsg.isBlank()) {
                                msg += " (jwtError=" + jwtErrorMsg + ")";
                            }
                            msg += " (hasAuthHeader=" + hasAuthHeader
                                    + ", bearer=" + bearer
                                    + ", jwtFilterSeen=" + filterSeen
                                    + ", jwtUsername=" + jwtUsername
                                    + ", jwtAuthSet=" + jwtAuthSet
                                    + ", existingAuthType=" + existingType
                                    + ", existingPrincipal=" + existingPrincipal
                                    + ", finalAuthType=" + finalAuthTypeAttr
                                    + ", finalAuthPrincipal=" + finalAuthPrincipalAttr
                                    + ", finalAuthIsAuthenticated=" + finalAuthIsAuthenticatedAttr
                                    + ", finalAuthAuthorities=" + finalAuthAuthoritiesAttr
                                    + ", currentAuthType=" + currentAuthType
                                    + ", currentPrincipal=" + currentPrincipal
                                    + ", currentIsAuthenticated=" + currentIsAuthenticated
                                    + ", currentAuthorities=" + currentAuthorities
                                    + ")";
                            response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"" + msg + "\"}");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                            response.getWriter().write("{\"error\":\"Forbidden\",\"message\":\"Not allowed\"}");
                        })
                )
                .addFilterBefore(jwtAuthFilter, AuthorizationFilter.class);

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsServiceImpl userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
