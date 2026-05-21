package com.qualitywatch.service.analytics;

import com.qualitywatch.domain.analytics.FlakyTest;
import com.qualitywatch.dto.response.FlakyTestResponse;
import com.qualitywatch.repository.FlakyTestRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TestAnalyticsService {

    private final FlakyTestRepository flakyTestRepository;

    public TestAnalyticsService(FlakyTestRepository flakyTestRepository) {
        this.flakyTestRepository = flakyTestRepository;
    }

    public List<FlakyTestResponse> getFlakyTests(UUID projectId) {
        return flakyTestRepository.findByProject_IdOrderByFailureRateDesc(projectId).stream()
                .map(this::toResponse)
                .toList();
    }

    private FlakyTestResponse toResponse(FlakyTest ft) {
        FlakyTestResponse r = new FlakyTestResponse();
        r.setId(ft.getId());
        r.setTestClass(ft.getTestClass());
        r.setTestMethod(ft.getTestMethod());
        r.setFailureRate(ft.getFailureRate());
        r.setTotalExecutions(ft.getTotalExecutions());
        r.setFailedExecutions(ft.getFailedExecutions());
        r.setLastFailedAt(ft.getLastFailedAt());
        r.setDetectionConfidence(ft.getDetectionConfidence());
        return r;
    }
}
