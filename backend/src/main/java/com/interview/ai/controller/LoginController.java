package com.interview.ai.controller;

import com.interview.ai.dto.LoginRequest;
import com.interview.ai.entity.User;
import com.interview.ai.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody LoginRequest request) {
        try {
            User user = userService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(toSafeUser(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            User user = userService.login(request);
            return ResponseEntity.ok(toSafeUser(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", e.getMessage()));
        }
    }

    private Map<String, Object> toSafeUser(User user) {
        return Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "email", user.getEmail() == null ? "" : user.getEmail(),
                "fullName", user.getFullName() == null ? "" : user.getFullName()
        );
    }
}
