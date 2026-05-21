package com.qualitywatch.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

public class LatestCoverageResponse {

    private BigDecimal lineCoveragePercent;
    private BigDecimal branchCoveragePercent;
    private BigDecimal instructionCoveragePercent;
    private Instant buildTimestamp;
    private String buildNumber;
    private String branch;
    private String status;

    public BigDecimal getLineCoveragePercent() {
        return lineCoveragePercent;
    }

    public void setLineCoveragePercent(BigDecimal lineCoveragePercent) {
        this.lineCoveragePercent = lineCoveragePercent;
    }

    public BigDecimal getBranchCoveragePercent() {
        return branchCoveragePercent;
    }

    public void setBranchCoveragePercent(BigDecimal branchCoveragePercent) {
        this.branchCoveragePercent = branchCoveragePercent;
    }

    public BigDecimal getInstructionCoveragePercent() {
        return instructionCoveragePercent;
    }

    public void setInstructionCoveragePercent(BigDecimal instructionCoveragePercent) {
        this.instructionCoveragePercent = instructionCoveragePercent;
    }

    public Instant getBuildTimestamp() {
        return buildTimestamp;
    }

    public void setBuildTimestamp(Instant buildTimestamp) {
        this.buildTimestamp = buildTimestamp;
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
}
