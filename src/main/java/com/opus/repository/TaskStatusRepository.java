package com.opus.repository;

import com.opus.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskStatusRepository extends JpaRepository<TaskStatus, Long> {
    List<TaskStatus> findByZone_ZoneIdOrderByDisplayOrderAsc(Long zoneId);
    Optional<TaskStatus> findByStatusIdAndZone_ZoneId(Long statusId, Long zoneId);
    Optional<TaskStatus> findByZone_ZoneIdAndIsInitialTrue(Long zoneId);
}
