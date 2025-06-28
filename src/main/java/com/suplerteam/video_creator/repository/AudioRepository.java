package com.suplerteam.video_creator.repository;

import com.suplerteam.video_creator.entity.Audio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AudioRepository extends JpaRepository<Audio, Long> {
}