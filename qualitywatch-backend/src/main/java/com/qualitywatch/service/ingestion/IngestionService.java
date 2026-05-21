package com.qualitywatch.service.ingestion;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qualitywatch.domain.common.Build;
import com.qualitywatch.domain.common.Project;
import com.qualitywatch.domain.telemetry.TelemetryEvent;
import com.qualitywatch.dto.request.TelemetryUploadRequest;
import com.qualitywatch.messaging.producer.TelemetryProducer;
import com.qualitywatch.repository.BuildRepository;
import com.qualitywatch.repository.ProjectRepository;
import com.qualitywatch.repository.TelemetryEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
public class IngestionService {

    private static final Logger log = LoggerFactory.getLogger(IngestionService.class);

    private final ProjectRepository projectRepository;
    private final BuildRepository buildRepository;
    private final TelemetryEventRepository telemetryEventRepository;
    private final TelemetryProducer telemetryProducer;
    private final ObjectMapper objectMapper;

    public IngestionService(
            ProjectRepository projectRepository,
            BuildRepository buildRepository,
            TelemetryEventRepository telemetryEventRepository,
            TelemetryProducer telemetryProducer,
            ObjectMapper objectMapper) {
        this.projectRepository = projectRepository;
        this.buildRepository = buildRepository;
        this.telemetryEventRepository = telemetryEventRepository;
        this.telemetryProducer = telemetryProducer;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public UUID ingestTelemetry(TelemetryUploadRequest request) {
        Instant now = Instant.now();

        Project project = projectRepository.findByName(request.getProjectName())
                .map(existing -> {
                    existing.setUpdatedAt(now);
                    return projectRepository.save(existing);
                })
                .orElseGet(() -> {
                    Project created = new Project();
                    created.setName(request.getProjectName());
                    created.setCreatedAt(now);
                    created.setUpdatedAt(now);
                    return projectRepository.save(created);
                });

        Build build = buildRepository.findByProject_IdAndBuildNumber(project.getId(), request.getBuildNumber())
                .map(existing -> {
                    existing.setBranch(request.getBranch());
                    existing.setCommitHash(request.getCommitHash());
                    existing.setBuildTimestamp(Instant.ofEpochMilli(request.getTimestamp()));
                    existing.setStatus("REPORTED");
                    return buildRepository.save(existing);
                })
                .orElseGet(() -> {
                    Build created = new Build();
                    created.setProject(project);
                    created.setBuildNumber(request.getBuildNumber());
                    created.setBranch(request.getBranch());
                    created.setCommitHash(request.getCommitHash());
                    created.setBuildTimestamp(Instant.ofEpochMilli(request.getTimestamp()));
                    created.setStatus("REPORTED");
                    created.setCreatedAt(now);
                    return buildRepository.save(created);
                });

        Map<String, Object> payloadMap = objectMapper.convertValue(request, new TypeReference<>() {});

        TelemetryEvent event = new TelemetryEvent();
        event.setBuild(build);
        event.setEventType("TELEMETRY_UPLOAD");
        event.setPayload(payloadMap);
        event.setReceivedAt(now);
        event.setProcessingStatus("PENDING");

        event = telemetryEventRepository.save(event);

        telemetryProducer.sendTelemetryEvent(event.getId());

        log.info("Telemetry event created: {}", event.getId());
        return event.getId();
    }
}
