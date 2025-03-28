package com.swaps.swap_cards.service;

import com.swaps.swap_cards.entity.Achievement;
import com.swaps.swap_cards.entity.SwapCard;
import com.swaps.swap_cards.entity.User;
import com.swaps.swap_cards.util.JwtUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @PersistenceContext
    private EntityManager entityManager;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public String registerUser(String userName, String email, String password) {
        if (entityManager.createQuery("SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class)
                .setParameter("email", email)
                .getSingleResult() > 0) {
            throw new RuntimeException("Email уже используется!");
        }

        User user = new User();
        user.setUserName(userName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        entityManager.persist(user);

        return jwtUtil.generateToken(email);
    }

    @Transactional
    public User createUser(String userName, String email, String password) {
        User user = new User();
        user.setUserName(userName);
        user.setEmail(email);
        user.setPassword(password);
        entityManager.persist(user);
        return user;
    }
    public List<User> getAllUsers() {
        String query = "SELECT u FROM User u";
        return entityManager.createQuery(query, User.class).getResultList();
    }

    public User getUserById(Integer id) {
        return entityManager.find(User.class, id);
    }

    public List<User> searchUserByName(String nameQuery) {
        String query = "SELECT u FROM User u WHERE u.userName LIKE :nameQuery";
        return entityManager.createQuery(query, User.class)
                .setParameter("nameQuery", "%" + nameQuery + "%")
                .getResultList();
    }

    public List<SwapCard> getCardsFromUser(Integer userId) {
        String query = "SELECT c FROM SwapCard c WHERE c.owner.id = :userId";
        return entityManager.createQuery(query, SwapCard.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Transactional
    public User updateUserPic(Integer userId, String newPicUrl) {
        User user = entityManager.find(User.class, userId);
        if (user != null) {
            user.setLinkToUserPic(newPicUrl);
            entityManager.merge(user);
        }
        return user;
    }
    @Transactional
    public void deleteUserPic(Integer userId) {
        User user = entityManager.find(User.class, userId);
        if (user != null) {
            user.setLinkToUserPic(null);
            entityManager.merge(user);
        }
    }

    public List<Achievement> getAchievements(Integer userId) {
        String query = "SELECT ua.id.achievement FROM UserAchievement ua WHERE ua.id.user.id = :userId";
        return entityManager.createQuery(query, Achievement.class)
                .setParameter("userId", userId)
                .getResultList();
    }

}
