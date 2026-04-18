package com.opus.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateZoneRequest {
	@Size(max = 100, message = "Zone name must not exceed 100 characters")
	private String zoneName;

	private String zoneType;
}
