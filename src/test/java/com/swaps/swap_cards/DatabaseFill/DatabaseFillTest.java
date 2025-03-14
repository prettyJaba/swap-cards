package com.swaps.swap_cards.DatabaseFill;

import com.swaps.swap_cards.entity.SwapCard;
import com.swaps.swap_cards.entity.User;
import com.swaps.swap_cards.service.AchievementService;
import com.swaps.swap_cards.service.SwapCardService;
import com.swaps.swap_cards.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class DatabaseFillTest {
    @Autowired
    private UserService userService;

    @Autowired
    private AchievementService achievementService;

    @Autowired
    private SwapCardService swapCardService;

    @BeforeEach
    public void setup() {
    }

    @Test
    public void testFillDatabase() {
        // Создание пользователей
        User user1 = userService.createUser("Anya", "1anya@example.com", "123");
        User user2 = userService.createUser("Vova", "1vova@example.com", "123");
        User user3 = userService.createUser("Ilya", "1ilya@example.com", "123");

        // Создание карточек
        SwapCard card1 = swapCardService.createSwapCard("1anya", "0", "annoying little girl", user1);
        SwapCard card2 = swapCardService.createSwapCard("1vova", "0", "annoying boy", user2);
        SwapCard card3 = swapCardService.createSwapCard("1ilya", "0", "annoying boy 2", user3);
        SwapCard card4 = swapCardService.createSwapCard("1dog", "0", "very loud dog", user2);

        assertNotNull(user1.getId());
        assertNotNull(user2.getId());
        assertNotNull(user3.getId());

        assertNotNull(card1.getId());
        assertNotNull(card2.getId());
        assertNotNull(card3.getId());
        assertNotNull(card4.getId());
    }
}
