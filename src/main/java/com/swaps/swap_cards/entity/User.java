package com.swaps.swap_cards.entity;

import com.swaps.swap_cards.util.PasswordUtil;
import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "users")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer id;

    @Column(name = "username", nullable = false)
    private String userName;

    @Column(name = "userpic")
    private String linkToUserPic;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    public User() { }

    public Integer getId() { return id; }
    public String getUserName() { return userName; }
    public String getLinkToUserPic() { return linkToUserPic; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }

    public void setUserName(String userName) { this.userName = userName; }
    public void setLinkToUserPic(String linkToUserPic) { this.linkToUserPic = linkToUserPic; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
}
