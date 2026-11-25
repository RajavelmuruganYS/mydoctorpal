package com.hospital.ehr.auth.service;

import com.hospital.ehr.auth.dto.LoginRequest;
import com.hospital.ehr.auth.dto.LoginResponse;
import com.hospital.ehr.auth.dto.RegisterRequest;
import com.hospital.ehr.auth.entity.RefreshToken;
import com.hospital.ehr.auth.entity.Role;
import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.auth.repository.RefreshTokenRepository;
import com.hospital.ehr.auth.repository.RoleRepository;
import com.hospital.ehr.auth.repository.UserRepository;
import com.hospital.ehr.security.JwtService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import com.hospital.ehr.auth.dto.ForgotPasswordRequest;
import com.hospital.ehr.auth.dto.ResetPasswordRequest;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            OtpService otpService,
            JwtService jwtService,
            RefreshTokenService refreshTokenService
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.otpService = otpService;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    /**
     * Register a new patient.
     *
     * Registration creates the account and
     * sends an email OTP for verification.
     */
    @Transactional
    public User register(RegisterRequest request) {

        // 1. Check whether email already exists
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new IllegalArgumentException(
                    "An account with this email already exists."
            );
        }

        // 2. Find PATIENT role
        Role patientRole = roleRepository.findByName("PATIENT")
                .orElseThrow(() ->
                        new IllegalStateException(
                                "PATIENT role is not configured."
                        )
                );

        // 3. Create user
        User user = new User();

        user.setEmail(request.getEmail().trim().toLowerCase());

        user.setPasswordHash(
                passwordEncoder.encode(request.getPassword())
        );

        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setPhone(request.getPhone());

        // 4. Email verification required
        user.setEmailVerified(false);

        // 5. Account pending until OTP verification
        user.setAccountStatus("PENDING_EMAIL_VERIFICATION");

        // 6. Save user first to receive ID
        User savedUser = userRepository.save(user);

        // 7. Assign PATIENT role
        savedUser.getRoles().add(patientRole);

        savedUser = userRepository.save(savedUser);

        // 8. Generate and send email OTP
        otpService.generateAndSendRegistrationOtp(savedUser);

        return savedUser;
    }

    /**
     * Authenticate a verified user and generate
     * access + refresh tokens.
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {

        // 1. Normalize email
        String email = request.getEmail()
                .trim()
                .toLowerCase();

        // 2. Find user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invalid email or password."
                        )
                );

        // 3. Check account status
        if ("LOCKED".equalsIgnoreCase(user.getAccountStatus())) {
            throw new IllegalArgumentException(
                    "Your account is locked. Please contact the administrator."
            );
        }

        if (!"ACTIVE".equalsIgnoreCase(user.getAccountStatus())) {
            throw new IllegalArgumentException(
                    "Please verify your email before logging in."
            );
        }

        // 4. Check email verification
        if (!user.isEmailVerified()) {
            throw new IllegalArgumentException(
                    "Please verify your email before logging in."
            );
        }

        // 5. Verify password
        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash()
        )) {
            throw new IllegalArgumentException(
                    "Invalid email or password."
            );
        }

        // 6. Generate JWT access token
        String accessToken =
                jwtService.generateAccessToken(user);

        // 7. Generate refresh token
        String refreshToken =
                refreshTokenService.generateRefreshToken();

        // 8. Hash refresh token before storing
        String refreshTokenHash =
                hashToken(refreshToken);

        // 9. Store refresh token in database
        RefreshToken refreshTokenEntity =
                new RefreshToken();

        refreshTokenEntity.setUser(user);

        refreshTokenEntity.setTokenHash(
                refreshTokenHash
        );

        refreshTokenEntity.setExpiresAt(
                LocalDateTime.now().plusNanos(
                        jwtService.getRefreshTokenExpiration()
                                * 1_000_000
                )
        );

        refreshTokenRepository.save(refreshTokenEntity);

        // 10. Extract roles
        List<String> roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .toList();

        // 11. Return login response
        return new LoginResponse(
                true,
                "Login successful.",
                user.getId(),
                user.getEmail(),
                roles,
                accessToken,
                refreshToken
        );
    }

    /**
     * SHA-256 hash for refresh-token storage.
     *
     * The actual refresh token is returned to the client,
     * while only its hash is stored in PostgreSQL.
     */
    private String hashToken(String token) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash =
                    digest.digest(
                            token.getBytes(StandardCharsets.UTF_8)
                    );

            return HexFormat.of().formatHex(hash);

        } catch (Exception e) {

            throw new IllegalStateException(
                    "Unable to securely hash refresh token.",
                    e
            );
        }
    }

    /**
 * Refresh an access token using a valid refresh token.
 */
@Transactional
public LoginResponse refreshAccessToken(String refreshToken) {

    if (refreshToken == null || refreshToken.isBlank()) {
        throw new IllegalArgumentException(
                "Refresh token is required."
        );
    }

    String tokenHash = hashToken(refreshToken);

    RefreshToken storedToken =
            refreshTokenRepository
                    .findByTokenHash(tokenHash)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Invalid refresh token."
                            )
                    );

    // Check expiration
    if (storedToken.getExpiresAt()
            .isBefore(LocalDateTime.now())) {

        throw new IllegalArgumentException(
                "Refresh token has expired. Please login again."
        );
    }

    // Check whether token has already been revoked
    if (storedToken.getRevokedAt() != null) {

        throw new IllegalArgumentException(
                "Refresh token has been revoked. Please login again."
        );
    }

    User user = storedToken.getUser();

    // Check account status
    if (!"ACTIVE".equalsIgnoreCase(
            user.getAccountStatus())) {

        throw new IllegalArgumentException(
                "User account is not active."
        );
    }

    // Generate new access token
    String accessToken =
            jwtService.generateAccessToken(user);

    List<String> roles = user.getRoles()
            .stream()
            .map(Role::getName)
            .toList();

    return new LoginResponse(
            true,
            "Access token refreshed successfully.",
            user.getId(),
            user.getEmail(),
            roles,
            accessToken,
            refreshToken
    );
}


/**
 * Logout by revoking the refresh token.
 */
@Transactional
public void logout(String refreshToken) {

    if (refreshToken == null || refreshToken.isBlank()) {
        throw new IllegalArgumentException(
                "Refresh token is required."
        );
    }

    String tokenHash = hashToken(refreshToken);

    RefreshToken storedToken =
            refreshTokenRepository
                    .findByTokenHash(tokenHash)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Invalid refresh token."
                            )
                    );

    storedToken.setRevokedAt(LocalDateTime.now());

    refreshTokenRepository.save(storedToken);
}

/**
 * Start password reset process.
 */
@Transactional
public void forgotPassword(
        ForgotPasswordRequest request
) {

    String email =
            request.getEmail()
                    .trim()
                    .toLowerCase();

    User user = userRepository
            .findByEmail(email)
            .orElseThrow(() ->
                    new IllegalArgumentException(
                            "No account found with this email."
                    )
            );

    if (!user.isEmailVerified()) {

        throw new IllegalArgumentException(
                "Please verify your email before resetting your password."
        );
    }

    if (!"ACTIVE".equalsIgnoreCase(
            user.getAccountStatus())) {

        throw new IllegalArgumentException(
                "This account is not active."
        );
    }

    otpService.generateAndSendPasswordResetOtp(user);
}


/**
 * Verify password reset OTP.
 */
@Transactional
public void verifyResetOtp(
        String email,
        String otp
) {

    otpService.verifyPasswordResetOtp(
            email,
            otp
    );
}


/**
 * Reset password after OTP verification.
 */
@Transactional
public void resetPassword(
        ResetPasswordRequest request
) {

    String email =
            request.getEmail()
                    .trim()
                    .toLowerCase();

    User user = userRepository
            .findByEmail(email)
            .orElseThrow(() ->
                    new IllegalArgumentException(
                            "User not found."
                    )
            );

    /*
     * The OTP must be verified before
     * allowing the password change.
     */
    otpService.verifyPasswordResetOtp(
            email,
            request.getOtp()
    );

    user.setPasswordHash(
            passwordEncoder.encode(
                    request.getNewPassword()
            )
    );

    userRepository.save(user);

    /*
     * Revoke existing refresh tokens so
     * old sessions cannot remain active
     * after password reset.
     */
    refreshTokenRepository
            .revokeAllTokensForUser(
                    user.getId(),
                    LocalDateTime.now()
            );
}
public User findUserByEmail(String email) {

    return userRepository
            .findByEmail(
                    email.trim().toLowerCase()
            )
            .orElseThrow(() ->
                    new IllegalArgumentException(
                            "User not found."
                    )
            );
}
}