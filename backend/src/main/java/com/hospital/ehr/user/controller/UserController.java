package com.hospital.ehr.user.controller;

import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.auth.repository.UserRepository;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Get currently authenticated user's profile.
     *
     * This endpoint requires a valid JWT access token.
     * The email is obtained from the authenticated JWT.
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(
            Authentication authentication
    ) {

        String email = authentication.getName();

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Authenticated user not found."
                        )
                );

        List<String> roles = user.getRoles()
                .stream()
                .map(role -> role.getName())
                .toList();

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "userId", user.getId(),
                        "email", user.getEmail(),
                        "firstName", user.getFirstName(),
                        "lastName", user.getLastName(),
                        "phone", user.getPhone() != null
                                ? user.getPhone()
                                : "",
                        "emailVerified", user.isEmailVerified(),
                        "accountStatus", user.getAccountStatus(),
                        "roles", roles
                )
        );
    }
}