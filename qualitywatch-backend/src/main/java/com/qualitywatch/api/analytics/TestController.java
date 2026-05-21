package com.qualitywatch.api.analytics;

import com.qualitywatch.dto.response.FlakyTestResponse;
import com.qualitywatch.service.analytics.TestAnalyticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/analytics/tests")
public class TestController {

    private final TestAnalyticsService testAnalyticsService;

    public TestController(TestAnalyticsService testAnalyticsService) {
        this.testAnalyticsService = testAnalyticsService;
    }

    @GetMapping("/flaky")
    public List<FlakyTestResponse> flaky(@RequestParam UUID projectId) {
        return testAnalyticsService.getFlakyTests(projectId);
    }
}
