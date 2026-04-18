package com.opus.repository;

import com.opus.model.TaskPriority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskPriorityRepository extends JpaRepository<TaskPriority, Long> {
    List<TaskPriority> findByZone_ZoneId(Long zoneId);
    Optional<TaskPriority> findByPriorityIdAndZone_ZoneId(Long priorityId, Long zoneId);
}
