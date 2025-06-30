package com.suplerteam.video_creator.service.workspace;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.suplerteam.video_creator.entity.Audio;
import com.suplerteam.video_creator.entity.User;
import com.suplerteam.video_creator.entity.Workspace;
import com.suplerteam.video_creator.repository.AudioRepository;
import com.suplerteam.video_creator.repository.UserRepository;
import com.suplerteam.video_creator.repository.WorkspaceRepository;
import com.suplerteam.video_creator.request.workspace.WorkspaceCreateRequest;
import com.suplerteam.video_creator.request.workspace.WorkspaceUpdateRequest;
import com.suplerteam.video_creator.response.workspace.WorkspaceDetailResponse;
import com.suplerteam.video_creator.response.workspace.WorkspaceSummaryResponse;
import com.suplerteam.video_creator.util.AuthenticationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkspaceService {

    @Autowired
    private WorkspaceRepository workspaceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AudioRepository audioRepository;

    @Autowired
    private AuthenticationUtil authenticationUtil;

    public WorkspaceDetailResponse createWorkspace(WorkspaceCreateRequest request) {
        User user = authenticationUtil.getCurrentUser();

        Workspace workspace = Workspace.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        Workspace savedWorkspace = workspaceRepository.save(workspace);
        return mapToDetailResponse(savedWorkspace);
    }

    public WorkspaceDetailResponse updateWorkspace(Long workspaceId, WorkspaceUpdateRequest request) {
        User user = authenticationUtil.getCurrentUser();

        Workspace workspace = workspaceRepository.findByIdAndUserId(workspaceId, user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Workspace not found for this user"));

        Audio audio = null;
        if (request.getAudioId() != null) {
            audio = audioRepository.findById(request.getAudioId()).orElse(null);
        }

        workspace.setScript(request.getScript());
        workspace.setImagesSet(request.getImagesSet());
        workspace.setAudio(audio);
        workspace.setVideoUrl(request.getVideoUrl());
        workspace.setLanguage(request.getLanguage());
        workspace.setShortScript(request.getShortScript());
        workspace.setWritingStyle(request.getWritingStyle());

        Workspace updatedWorkspace = workspaceRepository.save(workspace);
        return mapToDetailResponse(updatedWorkspace);
    }

    public List<WorkspaceDetailResponse> getAllWorkspaces() {
        User user = authenticationUtil.getCurrentUser();

        List<Workspace> workspaces = workspaceRepository.findByUserId(user.getId());
        return workspaces.stream()
                .map(this::mapToDetailResponse)
                .collect(Collectors.toList());
    }

    public WorkspaceDetailResponse getWorkspaceDetail(Long workspaceId) {
        User user = authenticationUtil.getCurrentUser();

        Workspace workspace = workspaceRepository.findByIdAndUserId(workspaceId, user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workspace not found for this user"));

        return mapToDetailResponse(workspace);
    }

    private WorkspaceSummaryResponse mapToSummaryResponse(Workspace workspace) {
        return WorkspaceSummaryResponse.builder()
                .id(workspace.getId())
                .title(workspace.getTitle())
                .description(workspace.getDescription())
                .build();
    }

    private WorkspaceDetailResponse mapToDetailResponse(Workspace workspace) {
        return WorkspaceDetailResponse.builder()
                .id(workspace.getId())
                .title(workspace.getTitle())
                .description(workspace.getDescription())
                .userId(workspace.getUser().getId())
                .script(workspace.getScript())
                .imagesSet(workspace.getImagesSet())
                .audioUrl(workspace.getAudio() != null ? workspace.getAudio().getUrl() : null)
                .videoUrl(workspace.getVideoUrl())
                .createdAt(workspace.getCreatedAt())
                .language(workspace.getLanguage())
                .shortScript(workspace.getShortScript())
                .writingStyle(workspace.getWritingStyle())
                .build();
    }
}