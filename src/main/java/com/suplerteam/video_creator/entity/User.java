package com.suplerteam.video_creator.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "fullname")
    private String fullName;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name="email")
    private String email;

    @Column(name="avatar")
    private String avatar;

    @Column(name="created_at")
    private Date createdAt;

    @Column(name = "role")
    private String role;

    @OneToOne(cascade = CascadeType.ALL,mappedBy = "user")
    private SocialAccountConnection socialConnection;

    @OneToMany(cascade = CascadeType.ALL,
            mappedBy = "user",fetch = FetchType.LAZY)
    private List<YoutubeUploads> youtubeUploads;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TiktokUploads> tiktokUploads;
}
