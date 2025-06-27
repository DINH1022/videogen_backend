package com.suplerteam.video_creator.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "workspace", uniqueConstraints = @UniqueConstraint(columnNames = "title"))
@Data
@Builder
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
}