package com.opus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ZoneResponse {
	private Long zoneId;
	private String zoneName;
	private String zoneType;
	private String zoneHash;
	private Long userId;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}

