package com.pharmacare.security;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthFilter(JwtService jwtService, UserDetailsServiceImpl userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        request.setAttribute("jwt_filter_seen", true);

        String token = authHeader.substring(7);

        try {
            String username = jwtService.extractUsername(token);
            request.setAttribute("jwt_username", username);

            var existingAuth = SecurityContextHolder.getContext().getAuthentication();
            Object existingPrincipal = existingAuth == null ? null : existingAuth.getPrincipal();
            request.setAttribute("existing_auth_type", existingAuth == null ? null : existingAuth.getClass().getName());
            request.setAttribute("existing_auth_principal", existingPrincipal == null ? null : String.valueOf(existingPrincipal));

            boolean canSetAuth = existingAuth == null
                    || existingAuth instanceof AnonymousAuthenticationToken
                    || "anonymousUser".equals(existingPrincipal);

            if (username != null && canSetAuth) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                request.setAttribute("jwt_auth_set", true);
            }
        } catch (Exception ex) {
            log.warn("JWT validation failed for {} {}: {}", request.getMethod(), request.getRequestURI(), ex.getMessage());
            request.setAttribute("jwt_error", ex.getMessage());
            SecurityContextHolder.clearContext();
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            var finalAuth = SecurityContextHolder.getContext().getAuthentication();
            request.setAttribute("final_auth_type", finalAuth == null ? null : finalAuth.getClass().getName());
            request.setAttribute("final_auth_principal", finalAuth == null ? null : String.valueOf(finalAuth.getPrincipal()));
            request.setAttribute("final_auth_is_authenticated", finalAuth != null && finalAuth.isAuthenticated());
            request.setAttribute("final_auth_authorities", finalAuth == null ? null : String.valueOf(finalAuth.getAuthorities()));
        }
    }
}
