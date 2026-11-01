package com.campus.market.controller;

import com.campus.market.dto.AuthRequest;
import com.campus.market.dto.RegisterRequest;
import com.campus.market.dto.UserResponse;
import com.campus.market.entity.User;
import com.campus.market.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest req, HttpSession session) {
        UserResponse response = authService.register(req, session);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody AuthRequest req, HttpSession session) {
        UserResponse response = authService.login(req, session);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpSession session) {
        authService.logout(session);
        return ResponseEntity.ok(Collections.singletonMap("message", "Logged out successfully"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        User user = authService.getCurrentUser(session);
        if (user == null) {
            return ResponseEntity.ok(Collections.singletonMap("authenticated", false));
        }
        return ResponseEntity.ok(UserResponse.fromEntity(user));
    }
}

