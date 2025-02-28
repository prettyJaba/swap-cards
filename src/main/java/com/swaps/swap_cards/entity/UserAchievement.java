package com.swaps.swap_cards.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "user_achievements")
public class UserAchievement implements Serializable {
    @EmbeddedId
    private UserAchievementId id;

    @Column(name = "date_earned", nullable = false)
    private LocalDate dateEarned;

    public UserAchievement() { }

    public UserAchievementId getId() { return id; }
    public LocalDate getDateEarned() { return dateEarned; }

    public void setUser(User user) { this.id.user = user; }
    public void setAchievement(Achievement achievement) { this.id.achievement = achievement; }
    public void setDateEarned(LocalDate dateEarned) { this.dateEarned = dateEarned; }

    @Embeddable
    public static class UserAchievementId implements Serializable {
        @ManyToOne
        @JoinColumn(name = "user_id")
        private User user;

        @ManyToOne
        @JoinColumn(name = "achievement_id")
        private Achievement achievement;

        public UserAchievementId() { }

    }
}