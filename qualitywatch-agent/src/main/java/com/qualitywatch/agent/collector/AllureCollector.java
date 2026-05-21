package com.qualitywatch.agent.collector;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qualitywatch.agent.model.TelemetryPayload;
import org.apache.maven.plugin.logging.Log;

import java.io.File;

public class AllureCollector implements ReportCollector {

    private final File resultsDir;
    private final Log log;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AllureCollector(File resultsDir, Log log) {
        this.resultsDir = resultsDir;
        this.log = log;
    }

    @Override
    public boolean isAvailable() {
        boolean exists = resultsDir.exists() && resultsDir.isDirectory();
        if (exists) {
            log.info("Found Allure results at: " + resultsDir.getAbsolutePath());
        }
        return exists;
    }

    @Override
    public void collect(TelemetryPayload payload) throws Exception {
        log.info("Parsing Allure results...");

        TelemetryPayload.TestExecutionData testData = new TelemetryPayload.TestExecutionData();

        File[] resultFiles = resultsDir.listFiles((dir, name) -> name.endsWith("-result.json"));
        if (resultFiles == null || resultFiles.length == 0) {
            log.warn("No Allure result files found");
            return;
        }

        int total = 0;
        int passed = 0;
        int failed = 0;
        int skipped = 0;
        long totalDuration = 0;

        for (File resultFile : resultFiles) {
            AllureResult result = objectMapper.readValue(resultFile, AllureResult.class);

            TelemetryPayload.TestResult test = new TelemetryPayload.TestResult();
            test.setSuiteName(result.getHistoryId() != null ? result.getHistoryId() : "unknown");
            test.setClassName(resolveClassName(result.getFullName()));
            test.setMethodName(result.getName());
            test.setStatus(mapStatus(result.getStatus()));
            if (result.getStart() != null && result.getStop() != null) {
                test.setDurationMs(result.getStop() - result.getStart());
            }

            if (result.getStatusDetails() != null) {
                test.setErrorMessage(result.getStatusDetails().getMessage());
                test.setStackTrace(result.getStatusDetails().getTrace());
            }

            testData.getTests().add(test);

            total++;
            if (test.getDurationMs() != null) {
                totalDuration += test.getDurationMs();
            }

            switch (test.getStatus()) {
                case "PASSED" -> passed++;
                case "FAILED", "BROKEN" -> failed++;
                case "SKIPPED" -> skipped++;
                default -> {
                }
            }
        }

        testData.setTotalTests(total);
        testData.setPassedTests(passed);
        testData.setFailedTests(failed);
        testData.setSkippedTests(skipped);
        testData.setTotalDurationMs(totalDuration);

        payload.setTestExecution(testData);
        log.info("Collected " + total + " test results (Passed: " + passed + ", Failed: " + failed + ", Skipped: " + skipped + ")");
    }

    private static String resolveClassName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return "unknown";
        }
        int dot = fullName.lastIndexOf('.');
        if (dot <= 0) {
            return fullName;
        }
        return fullName.substring(0, dot);
    }

    private static String mapStatus(String allureStatus) {
        return switch (allureStatus != null ? allureStatus.toLowerCase() : "unknown") {
            case "passed" -> "PASSED";
            case "failed" -> "FAILED";
            case "broken" -> "BROKEN";
            case "skipped" -> "SKIPPED";
            default -> "UNKNOWN";
        };
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class AllureResult {
        private String uuid;
        private String historyId;
        private String fullName;
        private String name;
        private String status;
        private Long start;
        private Long stop;
        private StatusDetails statusDetails;

        public String getUuid() {
            return uuid;
        }

        @SuppressWarnings("unused")
        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getHistoryId() {
            return historyId;
        }

        @SuppressWarnings("unused")
        public void setHistoryId(String historyId) {
            this.historyId = historyId;
        }

        public String getFullName() {
            return fullName;
        }

        @SuppressWarnings("unused")
        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getName() {
            return name;
        }

        @SuppressWarnings("unused")
        public void setName(String name) {
            this.name = name;
        }

        public String getStatus() {
            return status;
        }

        @SuppressWarnings("unused")
        public void setStatus(String status) {
            this.status = status;
        }

        public Long getStart() {
            return start;
        }

        @SuppressWarnings("unused")
        public void setStart(Long start) {
            this.start = start;
        }

        public Long getStop() {
            return stop;
        }

        @SuppressWarnings("unused")
        public void setStop(Long stop) {
            this.stop = stop;
        }

        public StatusDetails getStatusDetails() {
            return statusDetails;
        }

        @SuppressWarnings("unused")
        public void setStatusDetails(StatusDetails statusDetails) {
            this.statusDetails = statusDetails;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class StatusDetails {
        private String message;
        private String trace;

        public String getMessage() {
            return message;
        }

        @SuppressWarnings("unused")
        public void setMessage(String message) {
            this.message = message;
        }

        public String getTrace() {
            return trace;
        }

        @SuppressWarnings("unused")
        public void setTrace(String trace) {
            this.trace = trace;
        }
    }
}
