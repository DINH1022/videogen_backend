package com.suplerteam.video_creator.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tiktok_videos")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TiktokVideo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "video_id", nullable = false)
    private String videoId;


    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "url")
    private String url;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "thumbnail")
    private String thumbnail;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "num_views")
    private Long numViews;

    @Column(name = "num_likes")
    private Integer numLikes;

    @Column(name = "num_comments")
    private Integer numComments;

    @Column(name = "num_shares")
    private Integer numShares;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tiktok_upload_id")
    private TiktokUploads tiktokUpload;
}