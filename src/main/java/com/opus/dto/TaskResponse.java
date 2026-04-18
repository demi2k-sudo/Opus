package com.opus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {
    private Long taskId;
    private String taskKey;
    private Long zoneId;
    private String title;
    private String description;
    
    private Long statusId;
    private String statusName;
    
    private Long priorityId;
    private String priorityName;
    
    private Long typeId;
    private String typeName;
    
    private Long createdBy;
    private String createdByName;
    
    private Long assignedTo;
    private String assignedToName;
    
    private LocalDateTime dueAt;
    private LocalDateTime startAt;
    private LocalDateTime completedAt;
    private Integer estimatedMinutes;
    private Integer actualMinutes;
    
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
