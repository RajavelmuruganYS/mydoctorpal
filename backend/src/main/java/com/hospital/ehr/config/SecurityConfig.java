package com.hospital.ehr.config;

import com.hospital.ehr.audit.filter.AuditLoggingFilter;
import com.hospital.ehr.security.JwtAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuditLoggingFilter auditLoggingFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            AuditLoggingFilter auditLoggingFilter
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.auditLoggingFilter = auditLoggingFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                /*
                 * REST API does not use browser sessions,
                 * so CSRF protection is disabled.
                */
                .csrf(csrf -> csrf.disable())

                .cors(Customizer.withDefaults())

                /*
                 * JWT-based authentication is stateless.
                 */
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                /*
                 * Return JSON 401 (not authenticated / token expired) and
                 * JSON 403 (authenticated but not allowed).
                 *
                 * Without this Spring answers unauthenticated requests
                 * with 403, and the frontend could never tell that its
                 * access token expired and must be refreshed.
                 */
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                writeJsonError(
                                        response,
                                        HttpServletResponse.SC_UNAUTHORIZED,
                                        "Unauthorized",
                                        "Authentication is required or your session has expired."
                                )
                        )
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                writeJsonError(
                                        response,
                                        HttpServletResponse.SC_FORBIDDEN,
                                        "Forbidden",
                                        "You do not have permission to perform this action."
                                )
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        /*
                         * ============================
                         * PUBLIC AUTH ENDPOINTS
                         * ============================
                         */
                        .requestMatchers(
                                "/api/v1/auth/register",
                                "/api/v1/auth/verify-email",
                                "/api/v1/auth/resend-otp",
                                "/api/v1/auth/login",
                                "/api/v1/auth/refresh",
                                "/api/v1/auth/logout",
                                "/api/v1/auth/forgot-password",
                                "/api/v1/auth/verify-reset-otp",
                                "/api/v1/auth/reset-password"
                        ).permitAll()

                        /*
                         * ============================
                         * SWAGGER
                         * ============================
                         */
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()

                        /*
                         * ============================
                         * ALL OTHER APIs
                         * ============================
                         */
                        .anyRequest().authenticated()
                )

                /*
                 * JWT filter authenticates the request first.
                 */
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                /*
                 * Audit filter runs after JWT authentication,
                 * so the authenticated user is available
                 * through SecurityContextHolder.
                 */
                .addFilterAfter(
                        auditLoggingFilter,
                        JwtAuthenticationFilter.class
                );

        return http.build();
    }

    private static void writeJsonError(
            HttpServletResponse response,
            int status,
            String error,
            String message
    ) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
                "{\"success\":false,\"status\":" + status
                        + ",\"error\":\"" + error
                        + "\",\"message\":\"" + message + "\"}"
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
