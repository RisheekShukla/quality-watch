package com.qualitywatch.agent.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TelemetryPayload {

    private String projectName;
    private String buildNumber;
    private String branch;
    private String commitHash;
    private Long timestamp;

    private CoverageData coverage;
    private TestExecutionData testExecution;
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

    public CoverageData getCoverage() {
        return coverage;
    }

    public void setCoverage(CoverageData coverage) {
        this.coverage = coverage;
    }

    public TestExecutionData getTestExecution() {
        return testExecution;
    }

    public void setTestExecution(TestExecutionData testExecution) {
        this.testExecution = testExecution;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public static class CoverageData {
        private Double lineCoveragePercent;
        private Double branchCoveragePercent;
        private Double instructionCoveragePercent;
        private Integer linesCovered;
        private Integer linesTotal;
        private Integer branchesCovered;
        private Integer branchesTotal;
        private String moduleName;
        private Map<String, PackageCoverage> packages = new HashMap<>();

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

        public Map<String, PackageCoverage> getPackages() {
            return packages;
        }

        public void setPackages(Map<String, PackageCoverage> packages) {
            this.packages = packages;
        }
    }

    public static class PackageCoverage {
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

    public static class TestExecutionData {
        private Integer totalTests;
        private Integer passedTests;
        private Integer failedTests;
        private Integer skippedTests;
        private Long totalDurationMs;
        private List<TestResult> tests = new ArrayList<>();

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

        public List<TestResult> getTests() {
            return tests;
        }

        public void setTests(List<TestResult> tests) {
            this.tests = tests;
        }
    }

    public static class TestResult {
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
