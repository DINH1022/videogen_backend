package com.suplerteam.video_creator.repository;

import com.suplerteam.video_creator.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
    List<Workspace> findByUserId(Long userId);
    Optional<Workspace> findByIdAndUserId(Long id, Long userId);
    boolean existsByTitle(String title);
}