package com.qualitywatch.service.analytics;

import com.qualitywatch.dto.response.CoverageTrendResponse;
import com.qualitywatch.dto.response.LatestCoverageResponse;
import com.qualitywatch.repository.analytics.AnalyticsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class CoverageAnalyticsService {

    private final AnalyticsRepository analyticsRepository;

    public CoverageAnalyticsService(AnalyticsRepository analyticsRepository) {
        this.analyticsRepository = analyticsRepository;
    }

    public List<CoverageTrendResponse> getCoverageTrends(UUID projectId, String branch, int days) {
        LocalDate from = LocalDate.now().minusDays(Math.max(days, 1));
        return analyticsRepository.findCoverageTrends(projectId, branch, from);
    }

    public LatestCoverageResponse getLatestCoverage(UUID projectId, String branch) {
        return analyticsRepository.findLatestCoverage(projectId, branch);
    }
}
