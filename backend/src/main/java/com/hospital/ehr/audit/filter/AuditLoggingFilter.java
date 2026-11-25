package com.hospital.ehr.audit.filter;

import com.hospital.ehr.audit.service.AuditLogService;
import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.auth.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuditLoggingFilter extends OncePerRequestFilter {

    private final AuditLogService auditLogService;
    private final UserRepository userRepository;

    public AuditLoggingFilter(
            AuditLogService auditLogService,
            UserRepository userRepository
    ) {
        this.auditLogService = auditLogService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        long startTime = System.currentTimeMillis();

        try {
            filterChain.doFilter(request, response);
        } finally {
            recordAuditLog(request, response, startTime);
        }
    }

    private void recordAuditLog(
            HttpServletRequest request,
            HttpServletResponse response,
            long startTime
    ) {

        try {
            Authentication authentication =
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication();

            if (authentication == null
                    || !authentication.isAuthenticated()
                    || authentication.getName() == null
                    || "anonymousUser".equals(authentication.getName())) {
                return;
            }

            String path = request.getRequestURI();

            if (shouldSkip(path)) {
                return;
            }

            String email = authentication.getName();

            User user = userRepository
                    .findByEmail(email)
                    .orElse(null);

            if (user == null) {
                return;
            }

            String method = request.getMethod();

            String action = buildAction(method);

            String resourceType =
                    extractResourceType(path);

            String resourceId =
                    extractResourceId(path);

            Long patientId =
                    extractPatientId(path);

            String description =
                    method
                            + " "
                            + path
                            + " | HTTP "
                            + response.getStatus()
                            + " | "
                            + (System.currentTimeMillis() - startTime)
                            + " ms";

            auditLogService.log(
                    user.getId(),
                    patientId,
                    action,
                    resourceType,
                    resourceId,
                    description,
                    getClientIpAddress(request),
                    request.getHeader("User-Agent")
            );

        } catch (Exception ignored) {
            /*
             * Audit logging must never break the actual
             * patient/clinical request.
             */
        }
    }

    private boolean shouldSkip(String path) {

        return path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/actuator")
                || path.startsWith("/error")
                || path.startsWith("/favicon");
    }

    private String buildAction(String method) {

        return switch (method.toUpperCase()) {
            case "GET" -> "VIEW";
            case "POST" -> "CREATE";
            case "PUT", "PATCH" -> "UPDATE";
            case "DELETE" -> "DELETE";
            default -> method.toUpperCase();
        };
    }

    private String extractResourceType(String path) {

        String[] parts = path.split("/");

        for (int i = 0; i < parts.length; i++) {

            if ("v1".equals(parts[i])
                    && i + 1 < parts.length) {

                return parts[i + 1]
                        .replace("-", "_")
                        .toUpperCase();
            }
        }

        return "UNKNOWN";
    }

    private String extractResourceId(String path) {

        String[] parts = path.split("/");

        for (int i = parts.length - 1; i >= 0; i--) {

            String part = parts[i];

            if (part.matches("\\d+")) {
                return part;
            }
        }

        return null;
    }

    private Long extractPatientId(String path) {

        String[] parts = path.split("/");

        for (int i = 0; i < parts.length - 1; i++) {

            if ("patients".equalsIgnoreCase(parts[i])
                    && parts[i + 1].matches("\\d+")) {

                return Long.parseLong(parts[i + 1]);
            }
        }

        return null;
    }

    private String getClientIpAddress(
            HttpServletRequest request
    ) {

        String forwarded =
                request.getHeader("X-Forwarded-For");

        if (forwarded != null
                && !forwarded.isBlank()) {

            return forwarded.split(",")[0].trim();
        }

        String realIp =
                request.getHeader("X-Real-IP");

        if (realIp != null
                && !realIp.isBlank()) {

            return realIp;
        }

        return request.getRemoteAddr();
    }
}