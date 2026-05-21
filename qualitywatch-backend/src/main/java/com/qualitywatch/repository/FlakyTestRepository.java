package com.qualitywatch.repository;

import com.qualitywatch.domain.analytics.FlakyTest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FlakyTestRepository extends JpaRepository<FlakyTest, UUID> {

    List<FlakyTest> findByProject_IdOrderByFailureRateDesc(UUID projectId);

    Optional<FlakyTest> findByProject_IdAndTestClassAndTestMethod(UUID projectId, String testClass, String testMethod);
}
