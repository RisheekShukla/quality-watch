package com.qualitywatch.repository;

import com.qualitywatch.domain.telemetry.CoverageReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CoverageReportRepository extends JpaRepository<CoverageReport, UUID> {

    long deleteAllByTelemetryEvent_Id(UUID telemetryEventId);
}
