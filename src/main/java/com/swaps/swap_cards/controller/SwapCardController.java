package com.swaps.swap_cards.controller;

import com.swaps.swap_cards.entity.SwapCard;
import com.swaps.swap_cards.entity.User;
import com.swaps.swap_cards.service.SwapCardService;
import com.swaps.swap_cards.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cards")
public class SwapCardController {
    private final SwapCardService swapCardService;

    public SwapCardController(SwapCardService swapCardService, UserService userService) {
        this.swapCardService = swapCardService;
    }

    @PostMapping
    public ResponseEntity<SwapCard> createSwapCard(@RequestBody SwapCard swapCard) {
        return ResponseEntity.ok(swapCardService.createSwapCard(
                swapCard.getName(),
                swapCard.getLinkToImage(),
                swapCard.getDescription(),
                swapCard.getCreator()
        ));
    }

    @PatchMapping("/{cardId}/transfer")
    public ResponseEntity<?> transferCard(@PathVariable Integer cardId, @RequestParam Integer currentOwnerId, @RequestParam Integer newOwnerId) {
        try {
            swapCardService.transferCard(cardId, currentOwnerId, newOwnerId);
            return ResponseEntity.ok("Card transferred successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<SwapCard>> getAllCards() {
        return ResponseEntity.ok(swapCardService.getAllCards());
    }

    @GetMapping("/search")
    public ResponseEntity<List<SwapCard>> searchCardByName(@RequestParam String nameQuery) {
        return ResponseEntity.ok(swapCardService.searchCardByName(nameQuery));
    }

}
