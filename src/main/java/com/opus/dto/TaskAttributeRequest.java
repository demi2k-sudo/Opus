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
public class TaskAttributeRequest {
    private String name;
    private String color;
    private String icon;
    private Integer rank;
    private Integer displayOrder;
    private Boolean isInitial;
    private Boolean isFinal;
    private Map<String, Object> metadata;
}
