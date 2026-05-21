package com.qualitywatch.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Canonical upload body aligned with the Maven agent payload.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TelemetryUploadRequest {

    @NotBlank
    private String projectName;

    @NotBlank
    private String buildNumber;

    @NotBlank
    private String branch;

    private String commitHash;

    @NotNull
    private Long timestamp;

    private CoveragePayload coverage;

    private TestExecutionPayload testExecution;

    private Map<String, Object> metadata = new HashMap<>();

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
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

    public String getCommitHash() {
        return commitHash;
    }

    public void setCommitHash(String commitHash) {
        this.commitHash = commitHash;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public CoveragePayload getCoverage() {
        return coverage;
    }

    public void setCoverage(CoveragePayload coverage) {
        this.coverage = coverage;
    }

    public TestExecutionPayload getTestExecution() {
        return testExecution;
    }

    public void setTestExecution(TestExecutionPayload testExecution) {
        this.testExecution = testExecution;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CoveragePayload {
        private Double lineCoveragePercent;
        private Double branchCoveragePercent;
        private Double instructionCoveragePercent;
        private Integer linesCovered;
        private Integer linesTotal;
        private Integer branchesCovered;
        private Integer branchesTotal;
        private String moduleName;
        private Map<String, PackagePayload> packages = new HashMap<>();

        public Double getLineCoveragePercent() {
            return lineCoveragePercent;
        }

        public void setLineCoveragePercent(Double lineCoveragePercent) {
            this.lineCoveragePercent = lineCoveragePercent;
        }

        public Double getBranchCoveragePercent() {
            return branchCoveragePercent;
        }

        public void setBranchCoveragePercent(Double branchCoveragePercent) {
            this.branchCoveragePercent = branchCoveragePercent;
        }

        public Double getInstructionCoveragePercent() {
            return instructionCoveragePercent;
        }

        public void setInstructionCoveragePercent(Double instructionCoveragePercent) {
            this.instructionCoveragePercent = instructionCoveragePercent;
        }

        public Integer getLinesCovered() {
            return linesCovered;
        }

        public void setLinesCovered(Integer linesCovered) {
            this.linesCovered = linesCovered;
        }

        public Integer getLinesTotal() {
            return linesTotal;
        }

        public void setLinesTotal(Integer linesTotal) {
            this.linesTotal = linesTotal;
        }

        public Integer getBranchesCovered() {
            return branchesCovered;
        }

        public void setBranchesCovered(Integer branchesCovered) {
            this.branchesCovered = branchesCovered;
        }

        public Integer getBranchesTotal() {
            return branchesTotal;
        }

        public void setBranchesTotal(Integer branchesTotal) {
            this.branchesTotal = branchesTotal;
        }

        public String getModuleName() {
            return moduleName;
        }

        public void setModuleName(String moduleName) {
            this.moduleName = moduleName;
        }

        public Map<String, PackagePayload> getPackages() {
            return packages;
        }

        public void setPackages(Map<String, PackagePayload> packages) {
            this.packages = packages;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PackagePayload {
        private String packageName;
        private Double lineCoveragePercent;
        private Double branchCoveragePercent;
        private Integer complexity;

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public Double getLineCoveragePercent() {
            return lineCoveragePercent;
        }

        public void setLineCoveragePercent(Double lineCoveragePercent) {
            this.lineCoveragePercent = lineCoveragePercent;
        }

        public Double getBranchCoveragePercent() {
            return branchCoveragePercent;
        }

        public void setBranchCoveragePercent(Double branchCoveragePercent) {
            this.branchCoveragePercent = branchCoveragePercent;
        }

        public Integer getComplexity() {
            return complexity;
        }

        public void setComplexity(Integer complexity) {
            this.complexity = complexity;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TestExecutionPayload {
        private Integer totalTests;
        private Integer passedTests;
        private Integer failedTests;
        private Integer skippedTests;
        private Long totalDurationMs;
        private List<TestResultPayload> tests = new ArrayList<>();

        public Integer getTotalTests() {
            return totalTests;
        }

        public void setTotalTests(Integer totalTests) {
            this.totalTests = totalTests;
        }

        public Integer getPassedTests() {
            return passedTests;
        }

        public void setPassedTests(Integer passedTests) {
            this.passedTests = passedTests;
        }

        public Integer getFailedTests() {
            return failedTests;
        }

        public void setFailedTests(Integer failedTests) {
            this.failedTests = failedTests;
        }

        public Integer getSkippedTests() {
            return skippedTests;
        }

        public void setSkippedTests(Integer skippedTests) {
            this.skippedTests = skippedTests;
        }

        public Long getTotalDurationMs() {
            return totalDurationMs;
        }

        public void setTotalDurationMs(Long totalDurationMs) {
            this.totalDurationMs = totalDurationMs;
        }

        public List<TestResultPayload> getTests() {
            return tests;
        }

        public void setTests(List<TestResultPayload> tests) {
            this.tests = tests;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TestResultPayload {
        private String suiteName;
        private String className;
        private String methodName;
        private String status;
        private Long durationMs;
        private String errorMessage;
        private String stackTrace;

        public String getSuiteName() {
            return suiteName;
        }

        public void setSuiteName(String suiteName) {
            this.suiteName = suiteName;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Long getDurationMs() {
            return durationMs;
        }

        public void setDurationMs(Long durationMs) {
            this.durationMs = durationMs;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getStackTrace() {
            return stackTrace;
        }

        public void setStackTrace(String stackTrace) {
            this.stackTrace = stackTrace;
        }
    }
}
