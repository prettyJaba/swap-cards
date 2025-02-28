import jakarta.persistence.*;

@Entity
@Table(name = "user_achievements")
public class UserAchievement implements Serializable {
    @EmbeddedId
    private UserAchievementId id;

    @Column(name = "date_earned", nullable = false)
    private String dateEarned;

    public UserAchievement() { }

    public UserAchievementId getId() { return id; }
    public String getDateEarned() { return dateEarned; }
    public void setDateEarned(String dateEarned) { this.dateEarned = dateEarned; }

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