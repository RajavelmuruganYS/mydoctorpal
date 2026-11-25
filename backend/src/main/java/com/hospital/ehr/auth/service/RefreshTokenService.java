package com.hospital.ehr.auth.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service
public class RefreshTokenService {

    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Generate a cryptographically secure refresh token.
     */
    public String generateRefreshToken() {

        byte[] randomBytes = new byte[64];

        secureRandom.nextBytes(randomBytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);
    }
}