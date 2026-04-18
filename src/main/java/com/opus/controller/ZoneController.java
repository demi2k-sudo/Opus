package com.opus.controller;

import com.opus.dto.CreateZoneRequest;
import com.opus.dto.UpdateZoneRequest;
import com.opus.dto.ZoneResponse;
import com.opus.model.User;
import com.opus.service.ZoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/zones")
public class ZoneController {

	private final ZoneService zoneService;

	@Autowired
	public ZoneController(ZoneService zoneService) {
		this.zoneService = zoneService;
	}

	@PostMapping
	public ResponseEntity<ZoneResponse> createZone(@AuthenticationPrincipal User user, @RequestBody CreateZoneRequest request) {
		ZoneResponse response = zoneService.createZone(user.getUserId(), request);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{zoneId}")
	public ResponseEntity<ZoneResponse> getZoneById(@AuthenticationPrincipal User user, @PathVariable Long zoneId) {
		return ResponseEntity.ok(zoneService.getZoneById(user.getUserId(), zoneId));
	}

	@GetMapping("/hash/{zoneHash}")
	public ResponseEntity<ZoneResponse> getZoneByHash(@AuthenticationPrincipal User user, @PathVariable String zoneHash) {
		return ResponseEntity.ok(zoneService.getZoneByHash(user.getUserId(), zoneHash));
	}

	@GetMapping
	public ResponseEntity<List<ZoneResponse>> getAllZones(@AuthenticationPrincipal User user) {
		return ResponseEntity.ok(zoneService.getAllZonesForUser(user.getUserId()));
	}

	@PutMapping("/{zoneId}")
	public ResponseEntity<ZoneResponse> updateZone(@AuthenticationPrincipal User user, @PathVariable Long zoneId, @RequestBody UpdateZoneRequest request) {
		return ResponseEntity.ok(zoneService.updateZone(user.getUserId(), zoneId, request));
	}

	@DeleteMapping("/{zoneId}")
	public ResponseEntity<String> deleteZone(@AuthenticationPrincipal User user, @PathVariable Long zoneId) {
		zoneService.deleteZone(user.getUserId(), zoneId);
		return ResponseEntity.ok("Zone deleted successfully");
	}
}
