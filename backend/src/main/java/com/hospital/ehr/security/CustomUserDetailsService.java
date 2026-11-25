package com.hospital.ehr.security;

import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.auth.repository.UserRepository;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(
            UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found with email: " + email
                        )
                );

        /*
         * Convert application roles into
         * Spring Security authorities.
         *
         * Example:
         * ADMIN   -> ROLE_ADMIN
         * DOCTOR  -> ROLE_DOCTOR
         * NURSE   -> ROLE_NURSE
         */
        List<GrantedAuthority> authorities =
                user.getRoles()
                        .stream()
                        .map(role -> role.getName())
                        .filter(Objects::nonNull)
                        .map(this::normalizeAuthority)
                        .filter(name -> !name.isBlank())
                        .distinct()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .authorities(authorities)
                .accountLocked(
                        "LOCKED".equalsIgnoreCase(
                                user.getAccountStatus()
                        )
                )
                .disabled(
                        !"ACTIVE".equalsIgnoreCase(
                                user.getAccountStatus()
                        )
                )
                .build();
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