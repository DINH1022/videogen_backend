package com.suplerteam.video_creator.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "user_social_token")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SocialAccountConnection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "youtube_token")
    private String youtubeToken;

    @OneToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private User user;

}
