package com.opus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTaskRequest {
    private String title;
    private String description;
    private Long statusId;
    private Long priorityId;
    private Long typeId;
    private Long assignedTo;
    private Integer estimatedMinutes;
    private Map<String, Object> metadata;
}
