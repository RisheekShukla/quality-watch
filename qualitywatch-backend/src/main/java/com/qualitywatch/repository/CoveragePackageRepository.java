package com.qualitywatch.repository;

import com.qualitywatch.domain.telemetry.CoveragePackage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CoveragePackageRepository extends JpaRepository<CoveragePackage, UUID> {
}
