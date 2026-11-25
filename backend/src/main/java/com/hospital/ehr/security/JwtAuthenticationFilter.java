package com.hospital.ehr.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader =
                request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        /*
         * Expected:
         *
         * Authorization: Bearer <JWT>
         */
        if (authHeader != null
                && authHeader.startsWith("Bearer ")) {

            jwt = authHeader.substring(7);

            try {
                username = jwtService.extractUsername(jwt);
            } catch (Exception e) {
                // Invalid JWT.
            }
        }

        /*
         * Authenticate the request when:
         *
         * - JWT exists
         * - username can be extracted
         * - no authentication already exists
         */
        if (username != null
                && SecurityContextHolder
                        .getContext()
                        .getAuthentication() == null) {

            try {

                /*
                 * Validate JWT first.
                 */
                if (!jwtService.isTokenValid(jwt)) {
                    filterChain.doFilter(request, response);
                    return;
                }

                /*
                 * Load user from database.
                 */
                UserDetails userDetails =
                        userDetailsService
                                .loadUserByUsername(username);

                /*
                 * Get roles from JWT.
                 *
                 * Example:
                 *
                 * ["PATIENT"]
                 */
                List<String> roles =
                        jwtService.extractRoles(jwt);

                /*
                 * Convert:
                 *
                 * PATIENT
                 *
                 * into:
                 *
                 * ROLE_PATIENT
                 */
                Set<String> normalizedAuthorities = new HashSet<>();
                if (userDetails != null) {
                    userDetails.getAuthorities().stream()
                            .map(grantedAuthority -> grantedAuthority.getAuthority())
                            .filter(authority -> authority != null && !authority.isBlank())
                            .map(this::normalizeAuthority)
                            .forEach(normalizedAuthorities::add);
                }

                if (roles != null) {
                    roles.stream()
                            .filter(role -> role != null && !role.isBlank())
                            .map(this::normalizeAuthority)
                            .forEach(normalizedAuthorities::add);
                }

                List<SimpleGrantedAuthority> authorities =
                        normalizedAuthorities.stream()
                                .sorted()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList());

                /*
                 * Create authenticated user.
                 */
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                authorities
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                /*
                 * Put authentication into SecurityContext.
                 */
                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);

            } catch (Exception e) {

                /*
                 * Never authenticate an invalid request.
                 */
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    private String normalizeAuthority(String roleName) {
        String normalized = roleName == null ? "" : roleName.trim();
        if (normalized.isEmpty()) {
            return "";
        }
        return normalized.startsWith("ROLE_")
                ? normalized
                : "ROLE_" + normalized;
    }
}