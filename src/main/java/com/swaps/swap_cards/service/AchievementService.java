package com.swaps.swap_cards.service;

import com.swaps.swap_cards.entity.Achievement;
import com.swaps.swap_cards.entity.UserAchievement;
import com.swaps.swap_cards.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AchievementService {
    @PersistenceContext
    private EntityManager entityManager;



    public List<Achievement> getAchievementsForUser(Integer userId) {
        String query = "SELECT ua.achievement FROM UserAchievement ua WHERE ua.user.id = :userId";
        return entityManager.createQuery(query, Achievement.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public boolean checkAchievementCondition(User user, Achievement achievement) {
        Long giftCount = entityManager.createQuery(
                        "SELECT COUNT(c) FROM SwapCard c WHERE c.creator.id = :userId AND c.owner.id != :userId", Long.class)
                .setParameter("userId", user.getId())
                .getSingleResult();

        Long createCount = entityManager.createQuery(
                        "SELECT COUNT(c) FROM SwapCard c WHERE c.creator.id = :userId", Long.class)
                .setParameter("userId", user.getId())
                .getSingleResult();

        Long haveCount = entityManager.createQuery(
                        "SELECT COUNT(c) FROM SwapCard c WHERE c.owner.id = :userId", Long.class)
                .setParameter("userId", user.getId())
                .getSingleResult();

        Long achievementCount = entityManager.createQuery(
                        "SELECT COUNT(ua) FROM UserAchievement ua WHERE ua.user.id = :userId", Long.class)
                .setParameter("userId", user.getId())
                .getSingleResult();

        Long haveForeignCount = entityManager.createQuery(
                        "SELECT COUNT(c) FROM SwapCard c WHERE c.owner.id = :userId AND c.creator.id != :userId", Long.class)
                .setParameter("userId", user.getId())
                .getSingleResult();

        return switch (achievement.getCondition()) {
            case "create 5 cards" -> createCount >= 5;
            case "create first card" -> createCount >= 1;
            case "gift your card" -> giftCount >= 1;
            case "get card from another user" -> haveForeignCount >= 1;
            case "have 10 cards" -> haveCount >= 10;
            case "get 5 achievements" -> achievementCount >= 5;
            case "get 10 cards from another users" -> haveForeignCount >= 10;
            case "have 50 cards" -> haveCount >= 50;
            case "gift your cards to 5 users" -> giftCount >= 5;
            case "create account" -> true;

            default -> false;
        };
    }

    public void grantAchievementToUser(User user, Achievement achievement) {
        if (checkAchievementCondition(user, achievement)) {
            UserAchievement userAchievement = entityManager.createQuery(
                            "SELECT ua FROM UserAchievement ua WHERE ua.user.id = :userId AND ua.achievement.id = :achievementId", UserAchievement.class)
                    .setParameter("userId", user.getId())
                    .setParameter("achievementId", achievement.getId())
                    .getResultList()
                    .stream()
                    .findFirst()
                    .orElse(null);

            if (userAchievement == null) {
                userAchievement = new UserAchievement();
                userAchievement.setUser(user);
                userAchievement.setAchievement(achievement);
                userAchievement.setDateEarned(LocalDate.now());

                entityManager.persist(userAchievement);
            }
        } else {
            throw new IllegalArgumentException("You have not met the conditions for the achievement");
        }
    }
}
