package com.suplerteam.video_creator.repository;

import com.suplerteam.video_creator.entity.SocialAccountConnection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SocialAccountConnectionRepository extends JpaRepository<SocialAccountConnection, Long> {
    Optional<SocialAccountConnection> findByUser_Username(String username);
}