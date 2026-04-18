package com.opus.model;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(
	name = "user_zone_map_table",
	uniqueConstraints = {
		@UniqueConstraint(columnNames = {"userId", "zoneId"})
	}
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserZoneMap {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long uzmapId;

	@Column(nullable = false)
	private Long userId;   // FK → users.user_id

	@Column(nullable = false)
	private Long zoneId;   // FK → zones.zone_id

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private Role role;

	@Column(nullable = false, updatable = false)
	private LocalDateTime joinedAt;

	@JdbcTypeCode(SqlTypes.JSON)
	private Map<String, Object> metadata;

	@PrePersist
	protected void onCreate() {
		this.joinedAt = LocalDateTime.now();
	}

	public enum Role {
		OWNER,
		MEMBER
	}
}
