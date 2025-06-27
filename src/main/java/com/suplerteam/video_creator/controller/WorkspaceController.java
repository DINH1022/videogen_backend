package com.suplerteam.video_creator.controller;

import com.suplerteam.video_creator.request.workspace.WorkspaceCreateRequest;
import com.suplerteam.video_creator.request.workspace.WorkspaceUpdateRequest;
import com.suplerteam.video_creator.response.workspace.WorkspaceDetailResponse;
import com.suplerteam.video_creator.response.workspace.WorkspaceSummaryResponse;
import com.suplerteam.video_creator.service.workspace.WorkspaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workspace")
public class WorkspaceController {

    @Autowired
    private WorkspaceService workspaceService;

    @PostMapping
    public ResponseEntity<WorkspaceDetailResponse> createWorkspace(@RequestBody WorkspaceCreateRequest request) {
        WorkspaceDetailResponse response = workspaceService.createWorkspace(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{workspaceId}")
    public ResponseEntity<WorkspaceDetailResponse> updateWorkspace(
            @PathVariable Long workspaceId,
            @RequestBody WorkspaceUpdateRequest request) {
        WorkspaceDetailResponse response = workspaceService.updateWorkspace(workspaceId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<WorkspaceSummaryResponse>> getUserWorkspaces() {
        List<WorkspaceSummaryResponse> workspaces = workspaceService.getAllWorkspaces();
        return ResponseEntity.ok(workspaces);
    }

    @GetMapping("/{workspaceId}")
    public ResponseEntity<WorkspaceDetailResponse> getWorkspaceDetail(@PathVariable Long workspaceId) {
        WorkspaceDetailResponse workspace = workspaceService.getWorkspaceDetail(workspaceId);
        return ResponseEntity.ok(workspace);
    }
}