package com.opus.service;

import com.opus.dto.AssignTaskRequest;
import com.opus.dto.CreateTaskRequest;
import com.opus.dto.TaskResponse;
import com.opus.dto.UpdateTaskRequest;
import com.opus.model.*;
import com.opus.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ZoneRepository zoneRepository;
    private final TaskStatusRepository statusRepository;
    private final TaskPriorityRepository priorityRepository;
    private final TaskTypeRepository typeRepository;
    private final UserRepository userRepository;

    @Transactional
    public TaskResponse createTask(Long zoneId, Long userId, CreateTaskRequest req) {
        Zone zone = zoneRepository.findById(zoneId).orElseThrow(() -> new RuntimeException("Zone not found"));
        User creator = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        TaskPriority priority = priorityRepository.findById(req.getPriorityId()).orElseThrow(() -> new RuntimeException("Priority not found"));
        TaskType type = typeRepository.findById(req.getTypeId()).orElseThrow(() -> new RuntimeException("Type not found"));
        
        TaskStatus status = statusRepository.findByZone_ZoneIdAndIsInitialTrue(zoneId)
                .orElseGet(() -> statusRepository.findByZone_ZoneIdOrderByDisplayOrderAsc(zoneId).stream()
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("No status found for zone")));

        User assignee = null;
        if (req.getAssignedTo() != null) {
            assignee = userRepository.findById(req.getAssignedTo()).orElse(null);
        }

        long count = taskRepository.countByZone_ZoneId(zoneId);
        String taskKey = zone.getZoneCode() + "-" + (count + 1);

        Task task = Task.builder()
                .taskKey(taskKey)
                .zone(zone)
                .title(req.getTitle())
                .description(req.getDescription())
                .status(status)
                .priority(priority)
                .type(type)
                .createdBy(creator)
                .assignedTo(assignee)
                .estimatedMinutes(req.getEstimatedMinutes())
                .metadata(req.getMetadata())
                .build();

        task = taskRepository.save(task);
        return mapToResponse(task);
    }

    @Transactional(readOnly = true)
    public TaskResponse getTask(Long zoneId, String taskKey) {
        Task task = taskRepository.findByTaskKeyAndZone_ZoneId(taskKey, zoneId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return mapToResponse(task);
    }

    @Transactional(readOnly = true)
    public java.util.List<TaskResponse> getAllTasks(Long zoneId) {
        return taskRepository.findByZone_ZoneId(zoneId).stream()
                .map(this::mapToResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    @Transactional
    public TaskResponse updateTask(Long zoneId, String taskKey, UpdateTaskRequest req) {
        Task task = taskRepository.findByTaskKeyAndZone_ZoneId(taskKey, zoneId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (req.getTitle() != null) task.setTitle(req.getTitle());
        if (req.getDescription() != null) task.setDescription(req.getDescription());
        if (req.getEstimatedMinutes() != null) task.setEstimatedMinutes(req.getEstimatedMinutes());
        if (req.getMetadata() != null) task.setMetadata(req.getMetadata());

        if (req.getStatusId() != null) {
            task.setStatus(statusRepository.findById(req.getStatusId()).orElseThrow(() -> new RuntimeException("Status not found")));
        }
        if (req.getPriorityId() != null) {
            task.setPriority(priorityRepository.findById(req.getPriorityId()).orElseThrow(() -> new RuntimeException("Priority not found")));
        }
        if (req.getTypeId() != null) {
            task.setType(typeRepository.findById(req.getTypeId()).orElseThrow(() -> new RuntimeException("Type not found")));
        }
        if (req.getAssignedTo() != null) {
            task.setAssignedTo(userRepository.findById(req.getAssignedTo()).orElse(null));
        }

        task = taskRepository.save(task);
        return mapToResponse(task);
    }

    @Transactional
    public TaskResponse assignTask(Long zoneId, String taskKey, AssignTaskRequest req) {
        Task task = taskRepository.findByTaskKeyAndZone_ZoneId(taskKey, zoneId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        User assignee = null;
        if (req.getAssignedTo() != null) {
            assignee = userRepository.findById(req.getAssignedTo()).orElseThrow(() -> new RuntimeException("User not found"));
        }
        task.setAssignedTo(assignee);
        task = taskRepository.save(task);
        return mapToResponse(task);
    }

    @Transactional
    public void deleteTask(Long zoneId, String taskKey) {
        Task task = taskRepository.findByTaskKeyAndZone_ZoneId(taskKey, zoneId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        taskRepository.delete(task); // Triggers soft delete via @SQLDelete
    }

    private TaskResponse mapToResponse(Task task) {
        return TaskResponse.builder()
                .taskId(task.getTaskId())
                .taskKey(task.getTaskKey())
                .zoneId(task.getZone().getZoneId())
                .title(task.getTitle())
                .description(task.getDescription())
                .statusId(task.getStatus() != null ? task.getStatus().getStatusId() : null)
                .statusName(task.getStatus() != null ? task.getStatus().getStatusName() : null)
                .priorityId(task.getPriority() != null ? task.getPriority().getPriorityId() : null)
                .priorityName(task.getPriority() != null ? task.getPriority().getPriorityName() : null)
                .typeId(task.getType() != null ? task.getType().getTypeId() : null)
                .typeName(task.getType() != null ? task.getType().getTypeName() : null)
                .createdBy(task.getCreatedBy() != null ? task.getCreatedBy().getUserId() : null)
                .createdByName(task.getCreatedBy() != null ? task.getCreatedBy().getName() : null)
                .assignedTo(task.getAssignedTo() != null ? task.getAssignedTo().getUserId() : null)
                .assignedToName(task.getAssignedTo() != null ? task.getAssignedTo().getName() : null)
                .dueAt(task.getDueAt())
                .startAt(task.getStartAt())
                .completedAt(task.getCompletedAt())
                .estimatedMinutes(task.getEstimatedMinutes())
                .actualMinutes(task.getActualMinutes())
                .metadata(task.getMetadata())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
