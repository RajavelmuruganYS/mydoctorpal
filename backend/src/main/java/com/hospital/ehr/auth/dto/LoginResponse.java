package com.hospital.ehr.auth.dto;

import java.util.List;

public class LoginResponse {

    private boolean success;
    private String message;
    private Long userId;
    private String email;
    private List<String> roles;
    private String accessToken;
    private String refreshToken;

    public LoginResponse() {
    }

    public LoginResponse(
            boolean success,
            String message,
            Long userId,
            String email,
            List<String> roles,
            String accessToken,
            String refreshToken
    ) {
        this.success = success;
        this.message = message;
        this.userId = userId;
        this.email = email;
        this.roles = roles;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}