package com.qualitywatch.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class FlakyTestResponse {

    private UUID id;
    private String testClass;
    private String testMethod;
    private BigDecimal failureRate;
    private Integer totalExecutions;
    private Integer failedExecutions;
    private Instant lastFailedAt;
    private String detectionConfidence;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTestClass() {
        return testClass;
    }

    public void setTestClass(String testClass) {
        this.testClass = testClass;
    }

    public String getTestMethod() {
        return testMethod;
    }

    public void setTestMethod(String testMethod) {
        this.testMethod = testMethod;
    }

    public BigDecimal getFailureRate() {
        return failureRate;
    }

    public void setFailureRate(BigDecimal failureRate) {
        this.failureRate = failureRate;
    }

    public Integer getTotalExecutions() {
        return totalExecutions;
    }

    public void setTotalExecutions(Integer totalExecutions) {
        this.totalExecutions = totalExecutions;
    }

    public Integer getFailedExecutions() {
        return failedExecutions;
    }

    public void setFailedExecutions(Integer failedExecutions) {
        this.failedExecutions = failedExecutions;
    }

    public Instant getLastFailedAt() {
        return lastFailedAt;
    }

    public void setLastFailedAt(Instant lastFailedAt) {
        this.lastFailedAt = lastFailedAt;
    }

    public String getDetectionConfidence() {
        return detectionConfidence;
    }

    public void setDetectionConfidence(String detectionConfidence) {
        this.detectionConfidence = detectionConfidence;
    }
}
