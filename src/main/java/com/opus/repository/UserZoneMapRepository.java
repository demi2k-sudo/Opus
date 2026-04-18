package com.opus.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.opus.model.UserZoneMap;

public interface UserZoneMapRepository extends JpaRepository<UserZoneMap, Long>
{
	boolean existsByUserIdAndZoneId(Long userId, Long zoneId);
	List<UserZoneMap> findAllByUserId(Long userId);
	List<UserZoneMap> findAllByZoneId(Long zoneId);
	Optional<UserZoneMap> findByUserIdAndZoneId(Long userId, Long zoneId);

}
