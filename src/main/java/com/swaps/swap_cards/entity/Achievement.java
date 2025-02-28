import jakarta.persistence.*;

@Entity
@Table(name = "achievements")
public class Achievement implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "achievement_id")
    private Integer id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "icon", nullable = false)
    private String linkToIcon;

    @Column(name = "description", nullable = false)
    private String description;

    public Achievement() { }

    public Integer getId() { return id; }
    public String getTitle() { return title; }
    public String getLinkToIcon() { return linkToIcon; }
    public String getDescription() { return description; }

    public void setTitle(String title) { this.title = title; }
    public void setLinkToIcon(String linkToIcon) { this.linkToIcon = linkToIcon; }
    public void setDescription(String description) { this.description = description; }
}