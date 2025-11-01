package com.opus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.opus.model.TestEntity;

@Repository
public interface TestRepository extends JpaRepository<TestEntity, Long>
{
}
