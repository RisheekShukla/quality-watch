package com.qualitywatch.api.analytics;

import com.qualitywatch.dto.response.CoverageTrendResponse;
import com.qualitywatch.dto.response.LatestCoverageResponse;
import com.qualitywatch.service.analytics.CoverageAnalyticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/analytics/coverage")
public class CoverageController {

    private final CoverageAnalyticsService coverageAnalyticsService;

    public CoverageController(CoverageAnalyticsService coverageAnalyticsService) {
        this.coverageAnalyticsService = coverageAnalyticsService;
    }

    @GetMapping("/trends")
    public List<CoverageTrendResponse> trends(
            @RequestParam UUID projectId,
            @RequestParam(required = false) String branch,
            @RequestParam(defaultValue = "30") int days) {
        return coverageAnalyticsService.getCoverageTrends(projectId, branch, days);
    }

    @GetMapping("/latest")
    public LatestCoverageResponse latest(
            @RequestParam UUID projectId,
            @RequestParam(required = false) String branch) {
        return coverageAnalyticsService.getLatestCoverage(projectId, branch);
    }
}
