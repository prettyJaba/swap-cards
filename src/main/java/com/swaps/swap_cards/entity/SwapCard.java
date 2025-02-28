import jakarta.persistence.*;

@Entity
@Table(name = "swap_cards")
public class SwapCard implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "image", nullable = false)
    private String linkToImage;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    public SwapCard() { }

    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getLinkToImage() { return linkToImage; }
    public String getDescription() { return description; }
    public User getCreator() { return creator; }
    public User getOwner() { return owner; }

    public void setName(String name) { this.name = name; }
    public void setLinkToImage(String linkToImage) { this.linkToImage = linkToImage; }
    public void setDescription(String description) { this.description = description; }
    public void setOwner(User owner) { this.owner = owner; }
}