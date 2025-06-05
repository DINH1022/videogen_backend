package com.suplerteam.video_creator.repository;

import com.suplerteam.video_creator.entity.YoutubeUploads;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface YoutubeVideosRepository extends JpaRepository<YoutubeUploads,Long> {
    Page<YoutubeUploads> findByUser_Username(String username, Pageable pageable);
}
