package com.qualitywatch.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CoverageTrendResponse {

    private LocalDate date;
    private BigDecimal avgLineCoverage;
    private BigDecimal avgBranchCoverage;
    private Long buildCount;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getAvgLineCoverage() {
        return avgLineCoverage;
    }

    public void setAvgLineCoverage(BigDecimal avgLineCoverage) {
        this.avgLineCoverage = avgLineCoverage;
    }

    public BigDecimal getAvgBranchCoverage() {
        return avgBranchCoverage;
    }

    public void setAvgBranchCoverage(BigDecimal avgBranchCoverage) {
        this.avgBranchCoverage = avgBranchCoverage;
    }

    public Long getBuildCount() {
        return buildCount;
    }

    public void setBuildCount(Long buildCount) {
        this.buildCount = buildCount;
    }
}
