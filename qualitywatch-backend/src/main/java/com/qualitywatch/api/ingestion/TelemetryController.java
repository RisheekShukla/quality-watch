package com.qualitywatch.api.ingestion;

import com.qualitywatch.dto.request.TelemetryUploadRequest;
import com.qualitywatch.service.ingestion.IngestionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/telemetry")
public class TelemetryController {

    private static final Logger log = LoggerFactory.getLogger(TelemetryController.class);

    private final IngestionService ingestionService;

    public TelemetryController(IngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadTelemetry(@Valid @RequestBody TelemetryUploadRequest request) {
        log.info("Received telemetry upload for project {}, build {}", request.getProjectName(), request.getBuildNumber());

        UUID eventId = ingestionService.ingestTelemetry(request);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of(
                "status", "accepted",
                "eventId", eventId.toString(),
                "message", "Telemetry queued for processing"));
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "healthy"));
    }
}
