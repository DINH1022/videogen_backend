package com.suplerteam.video_creator.repository;

import com.suplerteam.video_creator.entity.TiktokUploads;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TiktokUploadRepository extends JpaRepository<TiktokUploads, Long> {
    TiktokUploads findByTitle(String title);
}
