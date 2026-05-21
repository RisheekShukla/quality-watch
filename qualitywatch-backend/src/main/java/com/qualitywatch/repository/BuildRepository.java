package com.qualitywatch.repository;

import com.qualitywatch.domain.common.Build;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BuildRepository extends JpaRepository<Build, UUID> {

    Optional<Build> findByProject_IdAndBuildNumber(UUID projectId, String buildNumber);
}
