package com.swaps.swap_cards.controller;

import com.swaps.swap_cards.entity.Achievement;
import com.swaps.swap_cards.entity.SwapCard;
import com.swaps.swap_cards.entity.User;
import com.swaps.swap_cards.service.AchievementService;
import com.swaps.swap_cards.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final AchievementService achievementService;

    public UserController(UserService userService, AchievementService achievementService) {
        this.userService = userService;
        this.achievementService = achievementService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(
                user.getUserName(),
                user.getEmail(),
                user.getPassword()
        ));
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        User user = userService.getUserById(id);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUserByName(@RequestParam String name) {
        return ResponseEntity.ok(userService.searchUserByName(name));
    }

    @GetMapping("/{id}/cards")
    public ResponseEntity<List<SwapCard>> getCardsFromUser(@PathVariable Integer id) {
        User user = userService.getUserById(id);
        return user != null ? ResponseEntity.ok(userService.getCardsFromUser(id)) : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}/update-pic")
    public ResponseEntity<User> updateUserPic(@PathVariable Integer id, @RequestParam String newPic) {
        User user = userService.getUserById(id);
        return user != null ? ResponseEntity.ok(userService.updateUserPic(id, newPic)) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}/delete-pic")
    public ResponseEntity<Void> deleteUserPic(@PathVariable Integer id) {
        User user = userService.getUserById(id);
        if (user != null) {
            userService.deleteUserPic(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/achievements")
    public ResponseEntity<List<Achievement>> getAchievements(@PathVariable Integer id) {
        User user = userService.getUserById(id);
        return user != null ? ResponseEntity.ok(userService.getAchievements(id)) : ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/achievements/{achievementId}")
    public ResponseEntity<String> grantAchievementToUser(@PathVariable Integer id, @PathVariable Integer achievementId) {
        User user = userService.getUserById(id);
        Achievement achievement = achievementService.getAchievementById(achievementId);
        try {
            achievementService.grantAchievementToUser(user, achievement);
            return ResponseEntity.ok("Achievement granted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
