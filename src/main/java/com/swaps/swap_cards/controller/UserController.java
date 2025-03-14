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
    public ResponseEntity<User> createUser(@RequestParam String userName,@RequestParam String email,@RequestParam String password) {
        return ResponseEntity.ok(userService.createUser(userName, email, password));
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
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUserByName(@RequestParam String name) {
        return ResponseEntity.ok(userService.searchUserByName(name));
    }

    @GetMapping("/{id}/cards")
    public ResponseEntity<List<SwapCard>> getCardsFromUser(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.getCardsFromUser(id));
    }

    @PatchMapping("/{id}/update-pic")
    public ResponseEntity<User> updateUserPic(@PathVariable Integer id, @RequestParam String newPic) {
        return ResponseEntity.ok(userService.updateUserPic(id, newPic));
    }

    @DeleteMapping("/{id}/delete-pic")
    public ResponseEntity<Void> deleteUserPic(@PathVariable Integer id) {
        userService.deleteUserPic(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/achievements")
    public ResponseEntity<List<Achievement>> getAchievements(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.getAchievements(id));
    }

    @GetMapping("/{id}/achievements")
    public ResponseEntity<List<Achievement>> getAchievementsForUser(@PathVariable Integer id) {
        return ResponseEntity.ok(achievementService.getAchievementsForUser(id));
    }

    @PostMapping("/{userId}/achievements/{achievementId}")
    public ResponseEntity<String> grantAchievementToUser(@PathVariable Integer userId, @PathVariable Integer achievementId) {
        User user = userService.getUserById(userId);
        Achievement achievement = achievementService.getAchievementById(achievementId);
        try {
            achievementService.grantAchievementToUser(user, achievement);
            return ResponseEntity.ok("Achievement granted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
