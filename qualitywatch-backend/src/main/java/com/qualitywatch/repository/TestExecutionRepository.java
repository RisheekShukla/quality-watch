package com.qualitywatch.repository;

import com.qualitywatch.domain.telemetry.TestExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TestExecutionRepository extends JpaRepository<TestExecution, UUID> {

    long deleteAllByTelemetryEvent_Id(UUID telemetryEventId);

    @Query(value = """
            SELECT te.test_class,
                   te.test_method,
                   COUNT(*) AS total_executions,
                   SUM(CASE WHEN te.status IN ('FAILED','BROKEN') THEN 1 ELSE 0 END) AS failed_executions,
                   SUM(CASE WHEN te.status = 'PASSED' THEN 1 ELSE 0 END) AS passed_executions
            FROM test_executions te
            INNER JOIN builds b ON te.build_id = b.id
            WHERE b.project_id = :projectId
            GROUP BY te.test_class, te.test_method
            """, nativeQuery = true)
    List<Object[]> aggregateOutcomesByTest(@Param("projectId") UUID projectId);
}
