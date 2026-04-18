package com.opus.model;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table
	(
		name = "zone_table",
		uniqueConstraints = {
			@UniqueConstraint(columnNames = "zoneHash")
		}
	)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Zone
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long zoneId;

	@Column(nullable = false, length = 100)
	private String zoneName;

	@Column(nullable = false, length=10)
	@Enumerated(EnumType.STRING)
	private ZoneType zoneType;

	@Column(nullable = true, length = 32)
	private String zoneHash;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	@Column(nullable = false, updatable = false)
	private Long userId;

	@JdbcTypeCode(SqlTypes.JSON)
	private Map<String, Object> metadata;

	@PrePersist
	protected void onCreate()
	{
		this.createdAt = LocalDateTime.now();
		this.updatedAt = this.createdAt;
	}

	@PreUpdate
	protected void onUpdate()
	{
		this.updatedAt = LocalDateTime.now();
	}

	public enum ZoneType {
		DESK,
		BOARD
	}
}


