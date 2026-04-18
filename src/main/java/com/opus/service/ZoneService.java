package com.opus.service;

import com.opus.dto.CreateZoneRequest;
import com.opus.dto.UpdateZoneRequest;
import com.opus.dto.ZoneResponse;
import com.opus.exception.InvalidZoneDataException;
import com.opus.exception.ZoneAccessDeniedException;
import com.opus.exception.ZoneNotFoundException;
import com.opus.model.UserZoneMap;
import com.opus.model.Zone;
import com.opus.repository.UserZoneMapRepository;
import com.opus.repository.ZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ZoneService {

	private final ZoneRepository zoneRepository;
	private final UserZoneMapRepository userZoneMapRepository;

	@Autowired
	public ZoneService(ZoneRepository zoneRepository, UserZoneMapRepository userZoneMapRepository) {
		this.zoneRepository = zoneRepository;
		this.userZoneMapRepository = userZoneMapRepository;
	}

	@Transactional
	public ZoneResponse createZone(Long userId, CreateZoneRequest request) {
		validateCreateRequest(request);

		Zone.ZoneType zoneType = parseZoneType(request.getZoneType());

		String zoneHash = generateUniqueHash();

		Zone zone = Zone.builder()
			.zoneName(request.getZoneName().trim())
			.zoneType(zoneType)
			.zoneHash(zoneHash)
			.userId(userId)
			.build();

		zone = zoneRepository.save(zone);

		UserZoneMap mapping = UserZoneMap.builder()
			.userId(userId)
			.zoneId(zone.getZoneId())
			.role(UserZoneMap.Role.OWNER)
			.build();

		userZoneMapRepository.save(mapping);

		return toResponse(zone);
	}

	public ZoneResponse getZoneById(Long userId, Long zoneId) {
		Zone zone = zoneRepository.findById(zoneId)
			.orElseThrow(ZoneNotFoundException::new);

		if (!userZoneMapRepository.existsByUserIdAndZoneId(userId, zoneId)) {
			throw new ZoneAccessDeniedException();
		}

		return toResponse(zone);
	}

	public ZoneResponse getZoneByHash(Long userId, String zoneHash) {
		Zone zone = zoneRepository.findZoneByZoneHash(zoneHash)
			.orElseThrow(ZoneNotFoundException::new);

		if (!userZoneMapRepository.existsByUserIdAndZoneId(userId, zone.getZoneId())) {
			throw new ZoneAccessDeniedException();
		}

		return toResponse(zone);
	}

	public List<ZoneResponse> getAllZonesForUser(Long userId) {
		List<UserZoneMap> mappings = userZoneMapRepository.findAllByUserId(userId);
		List<Long> zoneIds = mappings.stream().map(UserZoneMap::getZoneId).collect(Collectors.toList());
		return zoneRepository.findAllById(zoneIds).stream()
			.map(this::toResponse)
			.collect(Collectors.toList());
	}

	@Transactional
	public ZoneResponse updateZone(Long userId, Long zoneId, UpdateZoneRequest request) {
		Zone zone = zoneRepository.findById(zoneId)
			.orElseThrow(ZoneNotFoundException::new);

		UserZoneMap mapping = userZoneMapRepository.findByUserIdAndZoneId(userId, zoneId)
			.orElseThrow(ZoneAccessDeniedException::new);

		if (mapping.getRole() != UserZoneMap.Role.OWNER) {
			throw new ZoneAccessDeniedException();
		}

		if (request.getZoneName() != null) {
			String name = request.getZoneName().trim();
			if (name.isEmpty() || name.length() > 100) {
				throw new InvalidZoneDataException("Zone name must be between 1 and 100 characters");
			}
			zone.setZoneName(name);
		}

		if (request.getZoneType() != null) {
			zone.setZoneType(parseZoneType(request.getZoneType()));
		}

		zone = zoneRepository.save(zone);
		return toResponse(zone);
	}

	@Transactional
	public void deleteZone(Long userId, Long zoneId) {
		Zone zone = zoneRepository.findById(zoneId)
			.orElseThrow(ZoneNotFoundException::new);

		UserZoneMap mapping = userZoneMapRepository.findByUserIdAndZoneId(userId, zoneId)
			.orElseThrow(ZoneAccessDeniedException::new);

		if (mapping.getRole() != UserZoneMap.Role.OWNER) {
			throw new ZoneAccessDeniedException();
		}

		// Remove all user-zone mappings for this zone
		List<UserZoneMap> allMappings = userZoneMapRepository.findAllByZoneId(zoneId);
		userZoneMapRepository.deleteAll(allMappings);

		zoneRepository.delete(zone);
	}

	private void validateCreateRequest(CreateZoneRequest request) {
		if (request.getZoneName() == null || request.getZoneName().trim().isEmpty()) {
			throw new InvalidZoneDataException("Zone name is required");
		}
		if (request.getZoneName().trim().length() > 100) {
			throw new InvalidZoneDataException("Zone name must not exceed 100 characters");
		}
		if (request.getZoneType() == null || request.getZoneType().trim().isEmpty()) {
			throw new InvalidZoneDataException("Zone type is required");
		}
	}

	private Zone.ZoneType parseZoneType(String zoneType) {
		try {
			return Zone.ZoneType.valueOf(zoneType.trim().toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new InvalidZoneDataException("Invalid zone type. Must be DESK or BOARD");
		}
	}

	private String generateUniqueHash() {
		String hash;
		do {
			hash = UUID.randomUUID().toString().replace("-", "").substring(0, 32);
		} while (zoneRepository.existsByZoneHash(hash));
		return hash;
	}

	private ZoneResponse toResponse(Zone zone) {
		return ZoneResponse.builder()
			.zoneId(zone.getZoneId())
			.zoneName(zone.getZoneName())
			.zoneType(zone.getZoneType().name())
			.zoneHash(zone.getZoneHash())
			.userId(zone.getUserId())
			.createdAt(zone.getCreatedAt())
			.updatedAt(zone.getUpdatedAt())
			.build();
	}
}

