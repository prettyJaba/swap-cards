package com.swaps.swap_cards.controller;

import com.swaps.swap_cards.entity.SwapCard;
import com.swaps.swap_cards.service.SwapCardService;
import com.swaps.swap_cards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cards")
@Tag(name = "Карточки", description = "Методы для работы с карточками")
public class SwapCardController {
    private final SwapCardService swapCardService;

    public SwapCardController(SwapCardService swapCardService, UserService userService) {
        this.swapCardService = swapCardService;
    }

    @PostMapping
    @Operation(summary = "Создать новую карточку", description = "Создает карточку и возвращает информацию о ней.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Карточка успешно создана"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации данных"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<SwapCard> createSwapCard(@RequestBody SwapCard swapCard) {
        return ResponseEntity.ok(swapCardService.createSwapCard(
                swapCard.getName(),
                swapCard.getLinkToImage(),
                swapCard.getDescription(),
                swapCard.getCreator()
        ));
    }

    @PatchMapping("/{cardId}/transfer")
    @Operation(summary = "Передать карточку другому пользователю", description = "Передает карточку от пользователя владеющего ей другому пользователю.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Карточка успешно передана"),
            @ApiResponse(responseCode = "400", description = "Ошибка запроса, например, если передача невозможна")
    })
    public ResponseEntity<?> transferCard(
            @Parameter(description = "ID карточки, которая передается", required = true) @PathVariable Integer cardId,
            @Parameter(description = "ID владельца карточки", required = true) @RequestParam Integer currentOwnerId,
            @Parameter(description = "ID нового владельца карточки", required = true) @RequestParam Integer newOwnerId
    ) {
        try {
            swapCardService.transferCard(cardId, currentOwnerId, newOwnerId);
            return ResponseEntity.ok("Card transferred successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "Вывести все карточки", description = "Возвращает список карточек.")
    @ApiResponse(responseCode = "200", description = "Список карточек успешно получен")
    public ResponseEntity<List<SwapCard>> getAllCards() {
        return ResponseEntity.ok(swapCardService.getAllCards());
    }

    @GetMapping("/search")
    @Operation(summary = "Найти карточку по названию", description = "Ищет карточку по названию, возвращает все подходящие варианты.")
    public ResponseEntity<List<SwapCard>> searchCardByName(
            @Parameter(description = "Название карточки", required = true) @RequestParam String nameQuery
    ) {
        return ResponseEntity.ok(swapCardService.searchCardByName(nameQuery));
    }

}
