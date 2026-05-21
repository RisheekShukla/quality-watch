package com.qualitywatch.api.analytics;

import com.qualitywatch.dto.response.BuildHealthResponse;
import com.qualitywatch.service.analytics.BuildHealthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/analytics/builds")
public class BuildHealthController {

    private final BuildHealthService buildHealthService;

    public BuildHealthController(BuildHealthService buildHealthService) {
        this.buildHealthService = buildHealthService;
    }

    @GetMapping("/health")
    public List<BuildHealthResponse> health(
            @RequestParam UUID projectId,
            @RequestParam(defaultValue = "20") int limit) {
        return buildHealthService.getRecentBuilds(projectId, limit);
    }
}
