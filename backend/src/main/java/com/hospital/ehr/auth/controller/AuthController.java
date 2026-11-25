package com.hospital.ehr.auth.controller;

import com.hospital.ehr.auth.dto.LoginRequest;
import com.hospital.ehr.auth.dto.LoginResponse;
import com.hospital.ehr.auth.dto.RefreshTokenRequest;
import com.hospital.ehr.auth.dto.RegisterRequest;
import com.hospital.ehr.auth.dto.VerifyEmailRequest;
import com.hospital.ehr.auth.entity.User;
import com.hospital.ehr.auth.service.AuthService;
import com.hospital.ehr.auth.service.OtpService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hospital.ehr.auth.dto.ForgotPasswordRequest;
import com.hospital.ehr.auth.dto.ResetPasswordRequest;
import com.hospital.ehr.auth.dto.VerifyResetOtpRequest;
import com.hospital.ehr.auth.dto.ResendOtpRequest;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final OtpService otpService;

    public AuthController(
            AuthService authService,
            OtpService otpService
    ) {
        this.authService = authService;
        this.otpService = otpService;
    }

    /**
     * Register a new patient.
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request
    ) {

        try {

            User user = authService.register(request);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(
                            Map.of(
                                    "success", true,
                                    "message",
                                    "Registration successful. Please check your email for the OTP.",
                                    "email",
                                    user.getEmail(),
                                    "emailVerified",
                                    user.isEmailVerified(),
                                    "accountStatus",
                                    user.getAccountStatus()
                            )
                    );

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "success", false,
                                    "message", e.getMessage()
                            )
                    );

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            Map.of(
                                    "success", false,
                                    "message",
                                    "Registration failed. Please try again."
                            )
                    );
        }
    }

    /**
     * Verify registration email using OTP.
     */
    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(
            @Valid @RequestBody VerifyEmailRequest request
    ) {

        try {

            otpService.verifyRegistrationOtp(
                    request.getEmail(),
                    request.getOtp()
            );

            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message",
                            "Email verified successfully. Your account is now active."
                    )
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            Map.of(
                                    "success", false,
                                    "message", e.getMessage()
                            )
                    );

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            Map.of(
                                    "success", false,
                                    "message",
                                    "Email verification failed. Please try again."
                            )
                    );
        }
    }

    /**
     * Login.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request
    ) {

        try {

            LoginResponse response =
                    authService.login(request);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                            Map.of(
                                    "success", false,
                                    "message", e.getMessage()
                            )
                    );

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            Map.of(
                                    "success", false,
                                    "message",
                                    "Login failed. Please try again."
                            )
                    );
        }
    }

    /**
     * Refresh access token.
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            @Valid @RequestBody RefreshTokenRequest request
    ) {

        try {

            LoginResponse response =
                    authService.refreshAccessToken(
                            request.getRefreshToken()
                    );

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                            Map.of(
                                    "success", false,
                                    "message", e.getMessage()
                            )
                    );

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            Map.of(
                                    "success", false,
                                    "message",
                                    "Token refresh failed. Please login again."
                            )
                    );
        }
    }

    /**
     * Logout.
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @Valid @RequestBody RefreshTokenRequest request
    ) {

        try {

            authService.logout(
                    request.getRefreshToken()
            );

            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message",
                            "Logout successful."
                    )
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                            Map.of(
                                    "success", false,
                                    "message", e.getMessage()
                            )
                    );

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            Map.of(
                                    "success", false,
                                    "message",
                                    "Logout failed. Please try again."
                            )
                    );
        }
    }


    /**
 * Request password reset OTP.
 */
@PostMapping("/forgot-password")
public ResponseEntity<?> forgotPassword(
        @Valid @RequestBody ForgotPasswordRequest request
) {

    try {

        authService.forgotPassword(request);

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message",
                        "Password reset OTP has been sent to your email."
                )
        );

    } catch (IllegalArgumentException e) {

        return ResponseEntity
                .badRequest()
                .body(
                        Map.of(
                                "success", false,
                                "message", e.getMessage()
                        )
                );

    } catch (Exception e) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        Map.of(
                                "success", false,
                                "message",
                                "Unable to process password reset request."
                        )
                );
    }
}


/**
 * Verify password reset OTP.
 */
@PostMapping("/verify-reset-otp")
public ResponseEntity<?> verifyResetOtp(
        @Valid @RequestBody VerifyResetOtpRequest request
) {

    try {

        authService.verifyResetOtp(
                request.getEmail(),
                request.getOtp()
        );

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message",
                        "Password reset OTP verified successfully."
                )
        );

    } catch (IllegalArgumentException e) {

        return ResponseEntity
                .badRequest()
                .body(
                        Map.of(
                                "success", false,
                                "message", e.getMessage()
                        )
                );

    } catch (Exception e) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        Map.of(
                                "success", false,
                                "message",
                                "OTP verification failed."
                        )
                );
    }
}


/**
 * Reset password.
 */
@PostMapping("/reset-password")
public ResponseEntity<?> resetPassword(
        @Valid @RequestBody ResetPasswordRequest request
) {

    try {

        authService.resetPassword(request);

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message",
                        "Password reset successfully. Please login with your new password."
                )
        );

    } catch (IllegalArgumentException e) {

        return ResponseEntity
                .badRequest()
                .body(
                        Map.of(
                                "success", false,
                                "message", e.getMessage()
                        )
                );

    } catch (Exception e) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        Map.of(
                                "success", false,
                                "message",
                                "Password reset failed."
                        )
                );
    }
}


/**
 * Resend registration OTP.
 */
@PostMapping("/resend-otp")
public ResponseEntity<?> resendOtp(
        @Valid @RequestBody ResendOtpRequest request
) {

    try {

        User user = authService
                .findUserByEmail(
                        request.getEmail()
                );

        if (user.isEmailVerified()) {

            throw new IllegalArgumentException(
                    "Email is already verified."
            );
        }

        otpService.generateAndSendRegistrationOtp(
                user
        );

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message",
                        "A new verification OTP has been sent to your email."
                )
        );

    } catch (IllegalArgumentException e) {

        return ResponseEntity
                .badRequest()
                .body(
                        Map.of(
                                "success", false,
                                "message", e.getMessage()
                        )
                );

    } catch (Exception e) {

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        Map.of(
                                "success", false,
                                "message",
                                "Unable to resend OTP."
                        )
                );
    }
}
}