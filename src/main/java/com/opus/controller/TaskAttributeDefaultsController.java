package com.opus.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks/attributes/defaults")
public class TaskAttributeDefaultsController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> getDefaults() {
        List<Map<String, Object>> priorities = List.of(
                Map.of("name", "LOW", "rank", 1, "color", "green"),
                Map.of("name", "MEDIUM", "rank", 2, "color", "blue"),
                Map.of("name", "HIGH", "rank", 3, "color", "orange"),
                Map.of("name", "CRITICAL", "rank", 4, "color", "red")
        );

        List<Map<String, Object>> types = List.of(
                Map.of("name", "TASK", "icon", "task-icon"),
                Map.of("name", "BUG", "icon", "bug-icon"),
                Map.of("name", "FEATURE", "icon", "feature-icon"),
                Map.of("name", "EPIC", "icon", "epic-icon")
        );

        List<Map<String, Object>> statuses = List.of(
                Map.of("name", "TODO", "displayOrder", 1, "color", "gray", "isInitial", true, "isFinal", false),
                Map.of("name", "IN PROGRESS", "displayOrder", 2, "color", "blue", "isInitial", false, "isFinal", false),
                Map.of("name", "DONE", "displayOrder", 3, "color", "green", "isInitial", false, "isFinal", true)
        );

        return ResponseEntity.ok(Map.of(
                "priorities", priorities,
                "types", types,
                "statuses", statuses
        ));
    }
}
