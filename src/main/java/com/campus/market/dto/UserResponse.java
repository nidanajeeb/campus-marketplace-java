package com.campus.market.dto;

import com.campus.market.entity.User;

public class UserResponse {

    private Long userId;
    private String email;
    private String fullName;
    private String role;

    public UserResponse() {
    }

    public UserResponse(Long userId, String email, String fullName, String role) {
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
    }

    public static UserResponse fromEntity(User user) {
        if (user == null) return null;
        return new UserResponse(user.getUserId(), user.getEmail(), user.getFullName(), user.getRole());
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

