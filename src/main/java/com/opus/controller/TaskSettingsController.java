package com.opus.controller;

import com.opus.dto.TaskAttributeRequest;
import com.opus.dto.TaskAttributeResponse;
import com.opus.dto.ReorderRequest;
import com.opus.service.TaskSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/zones/{zoneId}/settings")
@RequiredArgsConstructor
public class TaskSettingsController {

    private final TaskSettingsService taskSettingsService;

    @GetMapping("/attributes")
    public ResponseEntity<Map<String, Object>> getAllAttributes(@PathVariable Long zoneId) {
        return ResponseEntity.ok(Map.of(
                "priorities", taskSettingsService.getPriorities(zoneId),
                "types", taskSettingsService.getTypes(zoneId),
                "statuses", taskSettingsService.getStatuses(zoneId)
        ));
    }

    @PostMapping("/priorities")
    public ResponseEntity<TaskAttributeResponse> createPriority(@PathVariable Long zoneId, @RequestBody TaskAttributeRequest request) {
        return ResponseEntity.ok(taskSettingsService.createPriority(zoneId, request));
    }

    @PostMapping("/types")
    public ResponseEntity<TaskAttributeResponse> createType(@PathVariable Long zoneId, @RequestBody TaskAttributeRequest request) {
        return ResponseEntity.ok(taskSettingsService.createType(zoneId, request));
    }

    @PostMapping("/statuses")
    public ResponseEntity<TaskAttributeResponse> createStatus(@PathVariable Long zoneId, @RequestBody TaskAttributeRequest request) {
        return ResponseEntity.ok(taskSettingsService.createStatus(zoneId, request));
    }
    @PutMapping("/statuses/{statusId}")
    public ResponseEntity<TaskAttributeResponse> updateStatus(@PathVariable Long zoneId, @PathVariable Long statusId, @RequestBody TaskAttributeRequest request) {
        return ResponseEntity.ok(taskSettingsService.updateStatus(zoneId, statusId, request));
    }

    @DeleteMapping("/statuses/{statusId}")
    public ResponseEntity<Void> deleteStatus(@PathVariable Long zoneId, @PathVariable Long statusId) {
        taskSettingsService.deleteStatus(zoneId, statusId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/statuses/reorder")
    public ResponseEntity<Void> reorderStatuses(@PathVariable Long zoneId, @RequestBody ReorderRequest request) {
        taskSettingsService.reorderStatuses(zoneId, request);
        return ResponseEntity.noContent().build();
    }
}
