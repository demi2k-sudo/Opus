package com.opus.repository;

import com.opus.model.TaskType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskTypeRepository extends JpaRepository<TaskType, Long> {
    List<TaskType> findByZone_ZoneId(Long zoneId);
    Optional<TaskType> findByTypeIdAndZone_ZoneId(Long typeId, Long zoneId);
}
