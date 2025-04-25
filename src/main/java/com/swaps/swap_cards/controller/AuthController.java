package com.swaps.swap_cards.controller;

import com.swaps.swap_cards.entity.User;
import com.swaps.swap_cards.service.UserService;
import com.swaps.swap_cards.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Авторизация", description = "Методы для работы с аккаунтом")
public class AuthController {
        private final UserService userService;
        private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    @Operation(summary = "Регистрация пользователя", description = "Регистрирует нового пользователя в системе.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно зарегистрирован"),
            @ApiResponse(responseCode = "400", description = "Ошибка регистрации")
    })
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
    @Operation(summary = "Аутентификация пользователя", description = "Проверяет email и пароль пользователя. Возвращает JWT-токен при успешной аутентификации.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешная аутентификация"),
            @ApiResponse(responseCode = "401", description = "Неверный email или пароль")
    })
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
    @Operation(summary = "Смена пароля", description = "Позволяет авторизованному пользователю изменить текущий пароль.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пароль успешно изменён"),
            @ApiResponse(responseCode = "400", description = "Ошибка смены пароля")
    })
    public ResponseEntity<String> changePassword(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> request
    ) {
        try {
            String oldPassword = request.get("oldPassword");
            String newPassword = request.get("newPassword");

            if (oldPassword == null || newPassword == null) {
                return ResponseEntity.badRequest().body("Необходимо указать старый и новый пароль.");
            }

            String email = SecurityContextHolder.getContext().getAuthentication().getName();

            userService.changePassword(email, oldPassword, newPassword);
            return ResponseEntity.ok("Пароль успешно изменен!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "Выход из системы", description = "Добавляет JWT-токен в черный список, тем самым завершает сеанс авторизации пользователя.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно завершил сеанс"),
            @ApiResponse(responseCode = "400", description = "Не удалось завершить сеанс")
    })
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
