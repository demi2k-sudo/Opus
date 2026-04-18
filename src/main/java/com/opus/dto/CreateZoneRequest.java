package com.opus.dto;

import jakarta.validation.constraints.NotBlank;
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
public class CreateZoneRequest {
	@NotBlank(message = "Zone name is required")
	@Size(max = 100, message = "Zone name must not exceed 100 characters")
	private String zoneName;

	@NotBlank(message = "Zone type is required")
	private String zoneType;
}
