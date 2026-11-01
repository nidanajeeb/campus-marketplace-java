package com.campus.market.service;

import com.campus.market.dto.AuthRequest;
import com.campus.market.dto.RegisterRequest;
import com.campus.market.dto.UserResponse;
import com.campus.market.entity.User;
import com.campus.market.repository.UserRepository;
import com.campus.market.util.PasswordUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    public static final String SESSION_USER_ID = "LOGGED_IN_USER_ID";

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse register(RegisterRequest req, HttpSession session) {
        String email = req.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }

        String hashedPassword = PasswordUtil.hashPassword(req.getPassword());
        User user = new User(email, hashedPassword, req.getFullName().trim(), "ROLE_USER");
        User saved = userRepository.save(user);

        // Auto-login after registration
        session.setAttribute(SESSION_USER_ID, saved.getUserId());
        return UserResponse.fromEntity(saved);
    }

    public UserResponse login(AuthRequest req, HttpSession session) {
        String email = req.getEmail().trim().toLowerCase();
        Optional<User> opt = userRepository.findByEmail(email);

        if (opt.isEmpty() || !PasswordUtil.verify(req.getPassword(), opt.get().getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        User user = opt.get();
        session.setAttribute(SESSION_USER_ID, user.getUserId());
        return UserResponse.fromEntity(user);
    }

    public void logout(HttpSession session) {
        if (session != null) {
            session.removeAttribute(SESSION_USER_ID);
            session.invalidate();
        }
    }

    public User getCurrentUser(HttpSession session) {
        if (session == null) return null;
        Object userIdObj = session.getAttribute(SESSION_USER_ID);
        if (userIdObj == null) return null;

        Long userId = (Long) userIdObj;
        return userRepository.findById(userId).orElse(null);
    }

    public User requireAuthenticatedUser(HttpSession session) {
        User user = getCurrentUser(session);
        if (user == null) {
            throw new IllegalStateException("Authentication required. Please sign in.");
        }
        return user;
    }

    public User requireAdmin(HttpSession session) {
        User user = requireAuthenticatedUser(session);
        if (!user.isAdmin()) {
            throw new SecurityException("Access denied: Admin privileges required.");
        }
        return user;
    }
}

