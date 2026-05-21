package com.qualitywatch.repository;

import com.qualitywatch.domain.telemetry.TelemetryEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface TelemetryEventRepository extends JpaRepository<TelemetryEvent, UUID> {

    @Query("SELECT e FROM TelemetryEvent e JOIN FETCH e.build b JOIN FETCH b.project WHERE e.id = :id")
    Optional<TelemetryEvent> findWithAssociations(@Param("id") UUID id);
}
