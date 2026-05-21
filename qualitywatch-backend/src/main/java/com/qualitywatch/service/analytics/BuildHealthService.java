package com.qualitywatch.service.analytics;

import com.qualitywatch.dto.response.BuildHealthResponse;
import com.qualitywatch.repository.analytics.AnalyticsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BuildHealthService {

    private final AnalyticsRepository analyticsRepository;

    public BuildHealthService(AnalyticsRepository analyticsRepository) {
        this.analyticsRepository = analyticsRepository;
    }

    public List<BuildHealthResponse> getRecentBuilds(UUID projectId, int limit) {
        return analyticsRepository.findRecentBuildHealth(projectId, Math.min(Math.max(limit, 1), 100));
    }
}
