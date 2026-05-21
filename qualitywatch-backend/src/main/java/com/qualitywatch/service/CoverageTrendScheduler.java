package com.qualitywatch.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Fallback refresh so {@code coverage_trends} stays usable if inline/async refresh misses.
 */
@Component
public class CoverageTrendScheduler {

    private static final Logger log = LoggerFactory.getLogger(CoverageTrendScheduler.class);

    private final CoverageTrendRefreshService coverageTrendRefreshService;

    public CoverageTrendScheduler(CoverageTrendRefreshService coverageTrendRefreshService) {
        this.coverageTrendRefreshService = coverageTrendRefreshService;
    }

    @Scheduled(fixedDelayString = "${qualitywatch.schedule.coverage-trends-ms:300000}")
    public void refreshCoverageTrendsPeriodically() {
        try {
            coverageTrendRefreshService.refreshCoverageTrends();
            log.trace("Scheduled coverage_trends refresh finished");
        } catch (Exception e) {
            log.warn("Scheduled coverage_trends refresh failed: {}", e.getMessage());
        }
    }
}
