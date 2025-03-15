package com.swaps.swap_cards.controller;

import com.swaps.swap_cards.entity.Achievement;
import com.swaps.swap_cards.service.AchievementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/achievements")
@Tag(name = "Достижения", description = "Методы для работы с достижениями")
public class AchievementController {
    private final AchievementService achievementService;

    public AchievementController(AchievementService achievementService) {
        this.achievementService = achievementService;
    }

    @GetMapping
    @Operation(summary = "Вывести все достижения", description = "Возвращает список достижений.")
    @ApiResponse(responseCode = "200", description = "Список достижений успешно получен")
    public ResponseEntity<List<Achievement>> getAllAchievements() {
        return ResponseEntity.ok(achievementService.getAllAchievements());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить достижение по ID", description = "Возвращает информацию о достижении.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Достижение найдено"),
            @ApiResponse(responseCode = "404", description = "Достижение не найдено")
    })
    public ResponseEntity<Achievement> getAchievementById(
            @Parameter(description = "ID достижения", required = true) @PathVariable Integer id
    ) {
        Achievement achievement = achievementService.getAchievementById(id);
        return achievement != null ? ResponseEntity.ok(achievementService.getAchievementById(id)) : ResponseEntity.notFound().build();
    }

}
