package com.opus.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.opus.model.Zone;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, Long>
{
	Optional<Zone> findZoneByZoneHash(String zoneHash);
	List<Zone> findAllByUserId(Long userId);
	boolean existsByZoneHash(String zoneHash);
}
