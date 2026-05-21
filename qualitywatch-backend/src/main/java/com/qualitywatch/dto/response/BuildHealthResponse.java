package com.qualitywatch.dto.response;

import java.time.Instant;
import java.util.UUID;

public class BuildHealthResponse {

    private UUID buildId;
    private String buildNumber;
    private String branch;
    private String status;
    private Instant buildTimestamp;
    private Long totalTests;
    private Long failedTests;

    public UUID getBuildId() {
        return buildId;
    }

    public void setBuildId(UUID buildId) {
        this.buildId = buildId;
    }

    public String getBuildNumber() {
        return buildNumber;
    }

    public void setBuildNumber(String buildNumber) {
        this.buildNumber = buildNumber;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getBuildTimestamp() {
        return buildTimestamp;
    }

    public void setBuildTimestamp(Instant buildTimestamp) {
        this.buildTimestamp = buildTimestamp;
    }

    public Long getTotalTests() {
        return totalTests;
    }

    public void setTotalTests(Long totalTests) {
        this.totalTests = totalTests;
    }

    public Long getFailedTests() {
        return failedTests;
    }

    public void setFailedTests(Long failedTests) {
        this.failedTests = failedTests;
    }
}
