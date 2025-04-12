package com.swaps.swap_cards.controller;

import com.swaps.swap_cards.entity.User;
import com.swaps.swap_cards.service.UserService;
import com.swaps.swap_cards.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
        private final UserService userService;
        private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        try {
            String token = userService.registerUser(
                    user.getUserName(),
                    user.getEmail(),
                    user.getPassword()
            );
            return ResponseEntity.ok(token);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> authenticate(@RequestBody Map<String, String> credentials) {
        try {
            String email = credentials.get("email");
            String password = credentials.get("password");
            String token = userService.authenticate(email, password);
            return ResponseEntity.ok(token);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> request
    ) {
        try {
            String oldPassword = request.get("oldPassword");
            String newPassword = request.get("newPassword");

            String token = authHeader.substring(7);
            String email = jwtUtil.extractEmail(token);

            userService.changePassword(email, oldPassword, newPassword);
            return ResponseEntity.ok("Пароль успешно изменен!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            jwtUtil.blacklistToken(token);
            return ResponseEntity.ok("Вы успешно вышли из системы");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка выхода: " + e.getMessage());
        }
    }
}
