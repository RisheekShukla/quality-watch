package com.qualitywatch.repository.analytics;

import com.qualitywatch.dto.response.BuildHealthResponse;
import com.qualitywatch.dto.response.CoverageTrendResponse;
import com.qualitywatch.dto.response.LatestCoverageResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class AnalyticsRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<CoverageTrendResponse> findCoverageTrends(UUID projectId, String branch, LocalDate fromDate) {
        Query query = entityManager.createNativeQuery("""
                SELECT date, avg_line_coverage, avg_branch_coverage, build_count
                FROM coverage_trends
                WHERE project_id = :projectId
                  AND (CAST(:branch AS VARCHAR) IS NULL OR branch = CAST(:branch AS VARCHAR))
                  AND date >= :fromDate
                ORDER BY date ASC
                """);
        query.setParameter("projectId", projectId);
        query.setParameter("branch", branch);
        query.setParameter("fromDate", fromDate);

        List<Object[]> rows = query.getResultList();
        List<CoverageTrendResponse> out = new ArrayList<>();
        for (Object[] row : rows) {
            CoverageTrendResponse r = new CoverageTrendResponse();
            if (row[0] instanceof Date sqlDate) {
                r.setDate(sqlDate.toLocalDate());
            } else if (row[0] instanceof LocalDate ld) {
                r.setDate(ld);
            }
            r.setAvgLineCoverage(toBigDecimal(row[1]));
            r.setAvgBranchCoverage(toBigDecimal(row[2]));
            r.setBuildCount(row[3] != null ? ((Number) row[3]).longValue() : null);
            out.add(r);
        }
        return out;
    }

    public LatestCoverageResponse findLatestCoverage(UUID projectId, String branch) {
        Query query = entityManager.createNativeQuery("""
                SELECT cr.line_coverage_percent,
                       cr.branch_coverage_percent,
                       cr.instruction_coverage_percent,
                       b.build_timestamp,
                       b.build_number,
                       b.branch,
                       b.status
                FROM coverage_reports cr
                JOIN builds b ON cr.build_id = b.id
                WHERE b.project_id = :projectId
                  AND (CAST(:branch AS VARCHAR) IS NULL OR b.branch = CAST(:branch AS VARCHAR))
                ORDER BY b.build_timestamp DESC
                LIMIT 1
                """);
        query.setParameter("projectId", projectId);
        query.setParameter("branch", branch);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.getResultList();
        if (rows.isEmpty()) {
            return null;
        }
        Object[] row = rows.get(0);
        LatestCoverageResponse r = new LatestCoverageResponse();
        r.setLineCoveragePercent(toBigDecimal(row[0]));
        r.setBranchCoveragePercent(toBigDecimal(row[1]));
        r.setInstructionCoveragePercent(toBigDecimal(row[2]));
        if (row[3] instanceof Instant instant) {
            r.setBuildTimestamp(instant);
        } else if (row[3] instanceof java.sql.Timestamp ts) {
            r.setBuildTimestamp(ts.toInstant());
        }
        r.setBuildNumber((String) row[4]);
        r.setBranch((String) row[5]);
        r.setStatus((String) row[6]);
        return r;
    }

    @SuppressWarnings("unchecked")
    public List<BuildHealthResponse> findRecentBuildHealth(UUID projectId, int limit) {
        Query query = entityManager.createNativeQuery("""
                SELECT b.id,
                       b.build_number,
                       b.branch,
                       b.status,
                       b.build_timestamp,
                       COUNT(te.id) AS total_tests,
                       SUM(CASE WHEN te.status IN ('FAILED','BROKEN') THEN 1 ELSE 0 END) AS failed_tests
                FROM builds b
                LEFT JOIN test_executions te ON te.build_id = b.id
                WHERE b.project_id = :projectId
                GROUP BY b.id, b.build_number, b.branch, b.status, b.build_timestamp
                ORDER BY b.build_timestamp DESC
                LIMIT :limit
                """);
        query.setParameter("projectId", projectId);
        query.setParameter("limit", limit);

        List<Object[]> rows = query.getResultList();
        List<BuildHealthResponse> out = new ArrayList<>();
        for (Object[] row : rows) {
            BuildHealthResponse r = new BuildHealthResponse();
            r.setBuildId(row[0] != null ? UUID.fromString(row[0].toString()) : null);
            r.setBuildNumber((String) row[1]);
            r.setBranch((String) row[2]);
            r.setStatus((String) row[3]);
            if (row[4] instanceof Instant instant) {
                r.setBuildTimestamp(instant);
            } else if (row[4] instanceof java.sql.Timestamp ts) {
                r.setBuildTimestamp(ts.toInstant());
            }
            r.setTotalTests(row[5] != null ? ((Number) row[5]).longValue() : 0L);
            r.setFailedTests(row[6] != null ? ((Number) row[6]).longValue() : 0L);
            out.add(r);
        }
        return out;
    }

    private static BigDecimal toBigDecimal(Object v) {
        if (v == null) {
            return null;
        }
        if (v instanceof BigDecimal bd) {
            return bd;
        }
        if (v instanceof Number n) {
            return BigDecimal.valueOf(n.doubleValue());
        }
        return null;
    }
}
