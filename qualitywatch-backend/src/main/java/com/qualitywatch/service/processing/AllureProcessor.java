package com.qualitywatch.service.processing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qualitywatch.domain.telemetry.TestExecution;
import com.qualitywatch.domain.telemetry.TelemetryEvent;
import com.qualitywatch.repository.TestExecutionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;

@Service
public class AllureProcessor {

    private static final Logger log = LoggerFactory.getLogger(AllureProcessor.class);

    private final TestExecutionRepository testExecutionRepository;
    private final ObjectMapper objectMapper;

    public AllureProcessor(TestExecutionRepository testExecutionRepository, ObjectMapper objectMapper) {
        this.testExecutionRepository = testExecutionRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void process(TelemetryEvent event) {
        Map<String, Object> payload = event.getPayload();
        if (payload == null || payload.get("testExecution") == null) {
            return;
        }

        testExecutionRepository.deleteAllByTelemetryEvent_Id(event.getId());

        JsonNode root = objectMapper.valueToTree(payload.get("testExecution"));
        JsonNode tests = root.get("tests");
        if (tests == null || !tests.isArray()) {
            return;
        }

        Instant executedAt = event.getBuild().getBuildTimestamp();
        Instant createdAt = Instant.now();

        for (JsonNode testNode : tests) {
            TestExecution row = new TestExecution();
            row.setBuild(event.getBuild());
            row.setTelemetryEvent(event);
            row.setTestSuite(text(testNode, "suiteName", "unknown"));
            row.setTestClass(text(testNode, "className", "unknown"));
            row.setTestMethod(text(testNode, "methodName", "unknown"));
            row.setStatus(normalizeStatus(text(testNode, "status", "UNKNOWN")));
            row.setDurationMs(longVal(testNode, "durationMs"));
            row.setErrorMessage(text(testNode, "errorMessage", null));
            row.setStackTrace(text(testNode, "stackTrace", null));
            row.setExecutedAt(executedAt);
            row.setCreatedAt(createdAt);
            testExecutionRepository.save(row);
        }

        log.debug("Persisted {} test rows for build {}", tests.size(), event.getBuild().getId());
    }

    private static String text(JsonNode node, String field, String defaultValue) {
        JsonNode v = node.get(field);
        if (v == null || v.isNull()) {
            return defaultValue;
        }
        String s = v.asText(null);
        return s != null ? s : defaultValue;
    }

    private static Long longVal(JsonNode node, String field) {
        JsonNode v = node.get(field);
        if (v == null || v.isNull() || !v.isNumber()) {
            return null;
        }
        return v.longValue();
    }

    private static String normalizeStatus(String status) {
        if (status == null || status.isBlank()) {
            return "UNKNOWN";
        }
        return status.trim().toUpperCase();
    }
}
