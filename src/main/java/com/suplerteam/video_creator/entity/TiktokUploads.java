package com.suplerteam.video_creator.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tiktok_uploads")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TiktokUploads {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "upload_date")
    private LocalDateTime uploadDate = LocalDateTime.now();
}