package com.qualitywatch.integration;

import com.qualitywatch.domain.telemetry.TelemetryEvent;
import com.qualitywatch.repository.CoverageReportRepository;
import com.qualitywatch.repository.TelemetryEventRepository;
import com.qualitywatch.repository.TestExecutionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers(disabledWithoutDocker = true)
class TelemetryIngestionIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("qualitywatch")
            .withUsername("qualitywatch")
            .withPassword("qualitywatch");

    @Container
    static final RabbitMQContainer rabbit = new RabbitMQContainer("rabbitmq:3-management-alpine");

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.rabbitmq.host", rabbit::getHost);
        registry.add("spring.rabbitmq.port", rabbit::getAmqpPort);
        registry.add("spring.rabbitmq.username", () -> "guest");
        registry.add("spring.rabbitmq.password", () -> "guest");
        registry.add("spring.task.scheduling.enabled", () -> "false");
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TelemetryEventRepository telemetryEventRepository;

    @Autowired
    private CoverageReportRepository coverageReportRepository;

    @Autowired
    private TestExecutionRepository testExecutionRepository;

    @Test
    void postUpload_resultsInCompletedEventAndPersistedRows() {
        String json = """
                {
                  "projectName": "it-demo-service",
                  "buildNumber": "integration-1",
                  "branch": "main",
                  "commitHash": "deadbeef",
                  "timestamp": 1715000000000,
                  "coverage": {
                    "lineCoveragePercent": 82.5,
                    "branchCoveragePercent": 71.0,
                    "instructionCoveragePercent": 77.0,
                    "linesCovered": 82,
                    "linesTotal": 100,
                    "branchesCovered": 15,
                    "branchesTotal": 20,
                    "packages": {}
                  },
                  "testExecution": {
                    "tests": [
                      {
                        "suiteName": "suite-a",
                        "className": "com.example.DemoTest",
                        "methodName": "shouldPass",
                        "status": "PASSED",
                        "durationMs": 12
                      }
                    ]
                  }
                }
                """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<Map> response =
                restTemplate.postForEntity("/api/v1/telemetry/upload", new HttpEntity<>(json, headers), Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).isNotNull();
        Object rawId = response.getBody().get("eventId");
        assertThat(rawId).isNotNull();
        UUID eventId = UUID.fromString(rawId.toString());

        await().atMost(Duration.ofSeconds(45)).pollInterval(Duration.ofMillis(150)).untilAsserted(() -> {
            TelemetryEvent ev = telemetryEventRepository.findById(eventId).orElseThrow();
            assertThat(ev.getProcessingStatus()).isEqualTo("COMPLETED");
        });

        assertThat(coverageReportRepository.count()).isGreaterThanOrEqualTo(1);
        assertThat(testExecutionRepository.count()).isGreaterThanOrEqualTo(1);
    }
}
