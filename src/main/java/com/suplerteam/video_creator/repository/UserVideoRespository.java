package com.suplerteam.video_creator.repository;

import com.suplerteam.video_creator.entity.UserVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserVideoRespository extends JpaRepository<UserVideo, Long> {
}
