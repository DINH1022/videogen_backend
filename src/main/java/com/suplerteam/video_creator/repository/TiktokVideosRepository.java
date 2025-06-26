package com.suplerteam.video_creator.repository;

import com.suplerteam.video_creator.entity.TiktokUploads;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TiktokVideosRepository extends JpaRepository<TiktokUploads, Long> {
    Page<TiktokUploads> findByUser_Username(String username, Pageable pageable);
    Optional<TiktokUploads> findByVideoId(String videoId);
}