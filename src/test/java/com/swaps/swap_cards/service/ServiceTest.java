package com.swaps.swap_cards.service;

import com.swaps.swap_cards.entity.SwapCard;
import com.swaps.swap_cards.entity.User;
import com.swaps.swap_cards.entity.Achievement;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ServiceTest {
    @Autowired
    private UserService userService;

    @Autowired
    private AchievementService achievementService;

    @Autowired
    private SwapCardService swapCardService;

    @BeforeEach
    public void setUp() {
    }

    //AchievementService
    @Test
    @Transactional
    public void testGrantAchievementToUser_Success() {
        assertDoesNotThrow(() -> achievementService.grantAchievementToUser(userService.getUserById(2), achievementService.getAchievementById(1)));
        List<Achievement> achievements = achievementService.getAchievementsForUser(2);
        assertTrue(achievements.contains(achievementService.getAchievementById(1)));
    }

    @Test
    public void testGetAchievementsForUser() {
        List<Achievement> achievements = achievementService.getAchievementsForUser(1);
        assertNotNull(achievements);
        assertEquals(1, achievements.size());
    }

    // UserService
    @Test
    public void testGetAllUsers() {
        List<User> users = userService.getAllUsers();

        assertNotNull(users);
        assertEquals(3, users.size());
    }

    @Test
    public void testGetUserById() {
        User user = userService.getUserById(1);

        assertNotNull(user);
        assertEquals(1, user.getId());
    }

    @Test
    public void testSearchUserByName() {
        String nameQuery = "Anya";
        List<User> users = userService.searchUserByName(nameQuery);

        assertNotNull(users);
        assertEquals(1, users.size());

        users.forEach(user -> assertTrue(user.getUserName().contains(nameQuery)));
    }

    @Test
    @Transactional
    public void testGetCardsFromUser() {
        List<SwapCard> cards = userService.getCardsFromUser(1);

        assertNotNull(cards);
        assertEquals(1, cards.size());
    }

    @Test
    @Transactional
    public void testUpdateUserPic() {
        Integer userId = 1;
        String newPicUrl = "0";

        userService.updateUserPic(userId, newPicUrl);

        User user = userService.getUserById(userId);
        assertNotNull(user);
        assertEquals(newPicUrl, user.getLinkToUserPic());
    }

    @Test
    @Transactional
    public void testDeleteUserPic() {
        Integer userId = 1;

        userService.deleteUserPic(userId);

        User user = userService.getUserById(userId);
        assertNotNull(user);
        assertNull(user.getLinkToUserPic());
    }



    // SwapCardService
    @Test
    public void testGetAllCards() {
        List<SwapCard> cards = swapCardService.getAllCards();

        assertNotNull(cards);
        assertEquals(4, cards.size());
    }

    @Test
    public void testSearchCardByName() {
        String nameQuery = "anya";
        List<SwapCard> cards = swapCardService.searchCardByName(nameQuery);

        assertNotNull(cards);
        assertEquals(1, cards.size());
    }

    @Test
    @Transactional
    public void testTransferCard() {
        User currentOwner = userService.getUserById(1);
        User newOwner = userService.getUserById(2);
        SwapCard card = userService.getCardsFromUser(1).get(0);

        assertEquals(currentOwner, card.getOwner());

        // Выполняем передачу карточки
        swapCardService.transferCard(card.getId(), currentOwner, newOwner);


        assertNotNull(card);
        assertEquals(newOwner, card.getOwner());
    }

}
