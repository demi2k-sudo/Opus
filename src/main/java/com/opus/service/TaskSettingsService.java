package com.opus.service;

import com.opus.dto.TaskAttributeRequest;
import com.opus.dto.TaskAttributeResponse;
import com.opus.model.TaskPriority;
import com.opus.model.TaskStatus;
import com.opus.model.TaskType;
import com.opus.model.Zone;
import com.opus.repository.TaskPriorityRepository;
import com.opus.repository.TaskStatusRepository;
import com.opus.repository.TaskTypeRepository;
import com.opus.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskSettingsService {

    private final TaskPriorityRepository priorityRepository;
    private final TaskTypeRepository typeRepository;
    private final TaskStatusRepository statusRepository;
    private final ZoneRepository zoneRepository;

    @Transactional(readOnly = true)
    public List<TaskAttributeResponse> getPriorities(Long zoneId) {
        return priorityRepository.findByZone_ZoneId(zoneId).stream()
                .map(p -> TaskAttributeResponse.builder()
                        .id(p.getPriorityId())
                        .name(p.getPriorityName())
                        .rank(p.getRank())
                        .color(p.getColor())
                        .metadata(p.getMetadata())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public TaskAttributeResponse createPriority(Long zoneId, TaskAttributeRequest req) {
        Zone zone = zoneRepository.findById(zoneId).orElseThrow(() -> new RuntimeException("Zone not found"));
        TaskPriority priority = TaskPriority.builder()
                .zone(zone)
                .priorityName(req.getName())
                .rank(req.getRank())
                .color(req.getColor())
                .metadata(req.getMetadata())
                .build();
        priority = priorityRepository.save(priority);
        return TaskAttributeResponse.builder()
                .id(priority.getPriorityId())
                .name(priority.getPriorityName())
                .rank(priority.getRank())
                .color(priority.getColor())
                .metadata(priority.getMetadata())
                .build();
    }

    @Transactional(readOnly = true)
    public List<TaskAttributeResponse> getTypes(Long zoneId) {
        return typeRepository.findByZone_ZoneId(zoneId).stream()
                .map(t -> TaskAttributeResponse.builder()
                        .id(t.getTypeId())
                        .name(t.getTypeName())
                        .icon(t.getIcon())
                        .metadata(t.getMetadata())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public TaskAttributeResponse createType(Long zoneId, TaskAttributeRequest req) {
        Zone zone = zoneRepository.findById(zoneId).orElseThrow(() -> new RuntimeException("Zone not found"));
        TaskType type = TaskType.builder()
                .zone(zone)
                .typeName(req.getName())
                .icon(req.getIcon())
                .metadata(req.getMetadata())
                .build();
        type = typeRepository.save(type);
        return TaskAttributeResponse.builder()
                .id(type.getTypeId())
                .name(type.getTypeName())
                .icon(type.getIcon())
                .metadata(type.getMetadata())
                .build();
    }

    @Transactional(readOnly = true)
    public List<TaskAttributeResponse> getStatuses(Long zoneId) {
        return statusRepository.findByZone_ZoneIdOrderByDisplayOrderAsc(zoneId).stream()
                .map(s -> TaskAttributeResponse.builder()
                        .id(s.getStatusId())
                        .name(s.getStatusName())
                        .displayOrder(s.getDisplayOrder())
                        .color(s.getColor())
                        .isInitial(s.getIsInitial())
                        .isFinal(s.getIsFinal())
                        .metadata(s.getMetadata())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public TaskAttributeResponse createStatus(Long zoneId, TaskAttributeRequest req) {
        Zone zone = zoneRepository.findById(zoneId).orElseThrow(() -> new RuntimeException("Zone not found"));
        TaskStatus status = TaskStatus.builder()
                .zone(zone)
                .statusName(req.getName())
                .displayOrder(req.getDisplayOrder())
                .color(req.getColor())
                .isInitial(req.getIsInitial() != null ? req.getIsInitial() : false)
                .isFinal(req.getIsFinal() != null ? req.getIsFinal() : false)
                .metadata(req.getMetadata())
                .build();
        status = statusRepository.save(status);
        return TaskAttributeResponse.builder()
                .id(status.getStatusId())
                .name(status.getStatusName())
                .displayOrder(status.getDisplayOrder())
                .color(status.getColor())
                .isInitial(status.getIsInitial())
                .isFinal(status.getIsFinal())
                .metadata(status.getMetadata())
                .build();
    }
}
