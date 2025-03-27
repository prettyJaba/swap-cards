package com.swaps.swap_cards.controller;

import com.swaps.swap_cards.entity.Achievement;
import com.swaps.swap_cards.entity.SwapCard;
import com.swaps.swap_cards.entity.User;
import com.swaps.swap_cards.service.AchievementService;
import com.swaps.swap_cards.service.UserService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Пользователи", description = "Методы для работы с пользователями")
public class UserController {
    private final UserService userService;
    private final AchievementService achievementService;

    public UserController(UserService userService, AchievementService achievementService) {
        this.userService = userService;
        this.achievementService = achievementService;
    }

    @PostMapping
    @Operation(summary = "Создать нового пользователя", description = "Создает нового пользователя и возвращает его данные.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно создан"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации данных"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(
                user.getUserName(),
                user.getEmail(),
                user.getPassword()
        ));
    }

    @GetMapping
    @Operation(summary = "Вывести всех пользователей", description = "Возвращает список всех пользователях.")
    @ApiResponse(responseCode = "200", description = "Список пользователей успешно получен")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить пользователя по ID", description = "Возвращает информацию о пользователе.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    public ResponseEntity<User> getUserById(
            @Parameter(description = "ID пользователя", required = true) @PathVariable Integer id
    ) {
        User user = userService.getUserById(id);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Найти пользователя по никнейму", description = "Ищет пользователей по никнейму, возвращает все подходящие варианты.")
    @ApiResponse(responseCode = "200", description = "Результаты поиска успешно получены")
    public ResponseEntity<List<User>> searchUserByName(
            @Parameter(description = "Никнейм пользователя", required = true) @RequestParam String name
    ) {
        return ResponseEntity.ok(userService.searchUserByName(name));
    }

    @GetMapping("/{id}/cards")
    @Operation(summary = "Получить все карточки пользователя по его ID", description = "Возвращает список карточек пользователя.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Карточки успешно получены"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    public ResponseEntity<List<SwapCard>> getCardsFromUser(
            @Parameter(description = "ID пользователя", required = true) @PathVariable Integer id
    ) {
        User user = userService.getUserById(id);
        return user != null ? ResponseEntity.ok(userService.getCardsFromUser(id)) : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/update-pic")
    @Operation(summary = "Обновить аватар пользователя по его ID", description = "Обновляет аватар пользователя.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Аватар успешно обновлен"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    public ResponseEntity<User> updateUserPic(
            @Parameter(description = "ID пользователя", required = true) @PathVariable Integer id,
            @Parameter(description = "Ссылка на новое изображение", required = true) @RequestParam String newPic
    ) {
        User user = userService.getUserById(id);
        return user != null ? ResponseEntity.ok(userService.updateUserPic(id, newPic)) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}/delete-pic")
    @Operation(summary = "Удалить аватар пользователя по его ID", description = "Удаляет аватар пользователя.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Аватар успешно удален"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    public ResponseEntity<Void> deleteUserPic(
            @Parameter(description = "ID пользователя", required = true) @PathVariable Integer id
    ) {
        User user = userService.getUserById(id);
        if (user != null) {
            userService.deleteUserPic(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/achievements")
    @Operation(summary = "Получить все достижения пользователя по его ID", description = "Возвращает список достижений пользователя.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список достижений успешно получен"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    public ResponseEntity<List<Achievement>> getAchievements(
            @Parameter(description = "ID пользователя", required = true) @PathVariable Integer id
    ) {
        User user = userService.getUserById(id);
        return user != null ? ResponseEntity.ok(userService.getAchievements(id)) : ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/achievements/{achievementId}")
    @Operation(summary = "Выдать достижение пользователю", description = "Проверяет условия и назначает достижение пользователю.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Достижение успешно выдано"),
            @ApiResponse(responseCode = "400", description = "Ошибка: достижение не может быть выдано"),
            @ApiResponse(responseCode = "404", description = "Пользователь или достижение не найдены")
    })
    public ResponseEntity<String> grantAchievementToUser(
            @Parameter(description = "ID пользователя", required = true) @PathVariable Integer id,
            @Parameter(description = "ID достижения", required = true) @PathVariable Integer achievementId
    ) {
        User user = userService.getUserById(id);
        Achievement achievement = achievementService.getAchievementById(achievementId);
        if (user == null || achievement == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Пользователь или достижение не найдены.");
        }
        try {
            achievementService.grantAchievementToUser(user, achievement);
            return ResponseEntity.ok("Достижение успешно выдано.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @RestController
    @RequestMapping("/api/auth")
    public class AuthController {
        private final UserService userService;

        public AuthController(UserService userService) {
            this.userService = userService;
        }

        @PostMapping("/register")
        public ResponseEntity<String> register(@RequestBody User user) {
            try {
                String token = userService.registerUser(user.getUserName(), user.getEmail(), user.getPassword());
                return ResponseEntity.ok(token);
            } catch (RuntimeException e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
    }

}
