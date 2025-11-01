package com.opus.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "test_entity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TestEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
}
