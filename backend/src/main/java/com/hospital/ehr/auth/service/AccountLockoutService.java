package com.hospital.ehr.auth.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AccountLockoutService {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCKOUT_MINUTES = 15;

    public boolean isLocked(LocalDateTime lockedUntil) {

        if (lockedUntil == null) {
            return false;
        }

        return lockedUntil.isAfter(LocalDateTime.now());
    }

    public boolean shouldLock(int failedAttempts) {
        return failedAttempts >= MAX_FAILED_ATTEMPTS;
    }

    public LocalDateTime getLockoutTime() {
        return LocalDateTime.now().plusMinutes(LOCKOUT_MINUTES);
    }

    public int getMaxFailedAttempts() {
        return MAX_FAILED_ATTEMPTS;
    }

    public int getLockoutMinutes() {
        return LOCKOUT_MINUTES;
    }
}