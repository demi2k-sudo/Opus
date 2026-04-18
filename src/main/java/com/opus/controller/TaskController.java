package com.opus.controller;

import com.opus.dto.AssignTaskRequest;
import com.opus.dto.CreateTaskRequest;
import com.opus.dto.TaskResponse;
import com.opus.dto.UpdateTaskRequest;
import com.opus.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/zones/{zoneId}/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @AuthenticationPrincipal com.opus.model.User user,
            @PathVariable Long zoneId,
            @RequestBody CreateTaskRequest request) {
        return ResponseEntity.ok(taskService.createTask(zoneId, user.getUserId(), request));
    }

    @GetMapping
    public ResponseEntity<java.util.List<TaskResponse>> getAllTasks(@PathVariable Long zoneId) {
        return ResponseEntity.ok(taskService.getAllTasks(zoneId));
    }

    @GetMapping("/{taskKey}")
    public ResponseEntity<TaskResponse> getTask(
            @PathVariable Long zoneId,
            @PathVariable String taskKey) {
        return ResponseEntity.ok(taskService.getTask(zoneId, taskKey));
    }

    @PutMapping("/{taskKey}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long zoneId,
            @PathVariable String taskKey,
            @RequestBody UpdateTaskRequest request) {
        return ResponseEntity.ok(taskService.updateTask(zoneId, taskKey, request));
    }

    @PutMapping("/{taskKey}/assign")
    public ResponseEntity<TaskResponse> assignTask(
            @PathVariable Long zoneId,
            @PathVariable String taskKey,
            @RequestBody AssignTaskRequest request) {
        return ResponseEntity.ok(taskService.assignTask(zoneId, taskKey, request));
    }

    @DeleteMapping("/{taskKey}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long zoneId,
            @PathVariable String taskKey) {
        taskService.deleteTask(zoneId, taskKey);
        return ResponseEntity.noContent().build();
    }
}
