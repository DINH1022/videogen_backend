package com.suplerteam.video_creator.repository;

import com.suplerteam.video_creator.entity.TiktokVideo;
import com.suplerteam.video_creator.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TiktokVideoRepository extends JpaRepository<TiktokVideo, Long> {
    Optional<TiktokVideo> findByVideoId(String videoId);
    Optional<TiktokVideo> findByTiktokUpload_Id(Long uploadId);
    List<TiktokVideo> findByTitle(String title);
    List<TiktokVideo> findByTiktokUpload_User(User user);
}
