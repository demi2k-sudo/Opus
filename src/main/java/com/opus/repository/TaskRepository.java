package com.opus.repository;

import com.opus.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByZone_ZoneId(Long zoneId);
    Optional<Task> findByTaskKeyAndZone_ZoneId(String taskKey, Long zoneId);
    long countByZone_ZoneId(Long zoneId);
    boolean existsByStatus_StatusId(Long statusId);
}
