package com.suplerteam.video_creator.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "workspace", uniqueConstraints = @UniqueConstraint(columnNames = "title"))
@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Workspace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "script")
    private String script;

    @Column(name = "images_set", columnDefinition = "text[]")
    private String[] imagesSet;

    @ManyToOne
    @JoinColumn(name = "audio_id")
    private Audio audio;

    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "language")
    private String language;

    @Column(name = "short_script", columnDefinition = "text[]")
    private String[] shortScript;

    @Column(name = "writing_style")
    private String writingStyle;
}