package com.swaps.swap_cards.controller;

import com.swaps.swap_cards.entity.Achievement;
import com.swaps.swap_cards.entity.SwapCard;
import com.swaps.swap_cards.entity.User;
import com.swaps.swap_cards.service.AchievementService;
import com.swaps.swap_cards.service.SwapCardService;
import com.swaps.swap_cards.service.UserService;
import com.swaps.swap_cards.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class PageController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final SwapCardService swapCardService;
    private final AchievementService achievementService;

    public PageController(UserService userService, JwtUtil jwtUtil, SwapCardService swapCardService, AchievementService achievementService) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.swapCardService = swapCardService;
        this.achievementService = achievementService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String userName,
            @RequestParam String email,
            @RequestParam String password,
            HttpServletResponse response,
            Model model
    ) {
        try {
            String token = userService.registerUser(userName, email, password);

            Cookie cookie = new Cookie("token", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            response.addCookie(cookie);

            return "redirect:/home"; // или переадресация на /home после входа
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(
            @RequestParam String email,
            @RequestParam String password,
            HttpServletResponse response,
            Model model
    ) {
        try {
            String token = userService.authenticate(email, password);

            Cookie cookie = new Cookie("token", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(3600); // 1 час
            response.addCookie(cookie);

            return "redirect:/home";

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        String token = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    token = cookie.getValue();
                }
            }
        }

        if (token != null) {
            jwtUtil.blacklistToken(token);
        }

        Cookie cookie = new Cookie("token", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        SecurityContextHolder.clearContext();

        return "redirect:/login";
    }

    @GetMapping("/home")
    public String homePage(Model model) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByEmail(email);
        model.addAttribute("user", user);
        return "home";
    }

    @GetMapping("/user/{id}")
    public String userProfile(@PathVariable Integer id, Model model,
                              @CookieValue(value = "token", required = false) String token) {
        if (token == null || !jwtUtil.validateToken(token)) {
            return "redirect:/login";
        }

        String email = jwtUtil.extractEmail(token);
        User currentUser = userService.getUserByEmail(email);
        User profileUser = userService.getUserById(id);

        List<SwapCard> cards = userService.getCardsFromUser(id);
        List<Achievement> achievements = userService.getAchievements(id);

        model.addAttribute("user", profileUser);
        model.addAttribute("cards", cards);
        model.addAttribute("achievements", achievements);
        model.addAttribute("isCurrentUser", currentUser.getId().equals(id));

        return "user";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 RedirectAttributes redirectAttributes,
                                 HttpServletRequest request) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            userService.changePassword(email, oldPassword, newPassword);
            redirectAttributes.addFlashAttribute("successMessage", "Пароль успешно изменён");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        String userId = userService.getUserByEmail(
                SecurityContextHolder.getContext().getAuthentication().getName()
        ).getId().toString();
        return "redirect:/user/" + userId;
    }

    @GetMapping("/cards")
    public String viewAllCards(Model model,
                               @CookieValue(value = "token", required = false) String token) {
        if (token == null || !jwtUtil.validateToken(token)) {
            return "redirect:/login";
        }

        List<SwapCard> cards = swapCardService.getAllCards();
        model.addAttribute("cards", cards);
        return "cards";
    }

    @GetMapping("/cards/{id}")
    public String viewCard(@PathVariable Integer id,
                           Model model,
                           @CookieValue(value = "token", required = false) String token) {
        if (token == null || !jwtUtil.validateToken(token)) {
            return "redirect:/login";
        }

        SwapCard card = swapCardService.getCardById(id);
        if (card == null) {
            return "redirect:/cards";
        }

        String email = jwtUtil.extractEmail(token);
        User currentUser = userService.getUserByEmail(email);

        boolean isOwner = currentUser != null && card.getOwner().getId().equals(currentUser.getId());

        model.addAttribute("card", card);
        model.addAttribute("isOwner", isOwner);

        return "card";
    }

    @PostMapping("/cards/{id}/transfer")
    public String transferCard(@PathVariable Integer id,
                               @RequestParam Integer newOwnerId,
                               @CookieValue(value = "token", required = false) String token,
                               RedirectAttributes redirectAttributes) {
        if (token == null || !jwtUtil.validateToken(token)) {
            return "redirect:/login";
        }

        String email = jwtUtil.extractEmail(token);
        User currentUser = userService.getUserByEmail(email);

        try {
            swapCardService.transferCard(id, currentUser.getId(), newOwnerId);
            redirectAttributes.addFlashAttribute("success", "Карточка успешно передана!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/cards/" + id;
    }

    @GetMapping("/cards/create")
    public String showCreateCardForm(@CookieValue(value = "token", required = false) String token) {
        if (token == null || !jwtUtil.validateToken(token)) {
            return "redirect:/login";
        }
        return "create_card";
    }

    @PostMapping("/cards/create")
    public String createCard(@RequestParam String name,
                             @RequestParam String description,
                             @RequestParam(required = false) String linkToImage,
                             @CookieValue(value = "token", required = false) String token) {
        if (token == null || !jwtUtil.validateToken(token)) {
            return "redirect:/login";
        }

        String email = jwtUtil.extractEmail(token);
        User creator = userService.getUserByEmail(email);

        swapCardService.createSwapCard(name, linkToImage, description, creator);

        return "redirect:/cards";
    }

    @GetMapping("/search")
    public String searchUsers(@RequestParam(value = "query", required = false) String query,
                              Model model,
                              @CookieValue(value = "token", required = false) String token) {
        if (token == null || !jwtUtil.validateToken(token)) {
            return "redirect:/login";
        }

        if (query != null && !query.isEmpty()) {
            List<User> users = userService.searchUserByName(query);
            if (!users.isEmpty()) {
                model.addAttribute("users", users);
            } else {
                model.addAttribute("notFound", "Пользователи не найдены");
            }
        }

        return "search";
    }

    @GetMapping("/achievements")
    public String viewAchievements(Model model,
                                   @CookieValue(value = "token", required = false) String token) {
        if (token == null || !jwtUtil.validateToken(token)) {
            return "redirect:/login";
        }

        String email = jwtUtil.extractEmail(token);
        User user = userService.getUserByEmail(email);

        List<Achievement> all = achievementService.getAllAchievements();
        List<Achievement> userAchievements = achievementService.getAchievementsForUser(user.getId());

        // ID всех полученных достижений
        List<Integer> userAchievementsIds = userAchievements.stream()
                .map(Achievement::getId)
                .toList();

        model.addAttribute("allAchievements", all);
        model.addAttribute("userAchievementsIds", userAchievementsIds);

        return "achievements";
    }

    @PostMapping("/achievements/{id}/grant")
    public String grantAchievement(@PathVariable("id") Integer achievementId,
                                   @CookieValue(value = "token", required = false) String token,
                                   RedirectAttributes redirectAttributes) {
        if (token == null || !jwtUtil.validateToken(token)) {
            return "redirect:/login";
        }

        String email = jwtUtil.extractEmail(token);
        User user = userService.getUserByEmail(email);
        Achievement achievement = achievementService.getAchievementById(achievementId);

        if (achievement == null) {
            redirectAttributes.addFlashAttribute("error", "Достижение не найдено.");
            return "redirect:/achievements";
        }

        try {
            achievementService.grantAchievementToUser(user, achievement);
            redirectAttributes.addFlashAttribute("success", "Достижение успешно получено!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/achievements";
    }

}

