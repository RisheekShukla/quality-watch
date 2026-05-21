package com.qualitywatch.domain.telemetry;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "coverage_packages")
public class CoveragePackage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "coverage_report_id", nullable = false)
    private CoverageReport coverageReport;

    @Column(nullable = false, length = 500)
    private String packageName;

    @Column(precision = 5, scale = 2)
    private BigDecimal lineCoveragePercent;

    @Column(precision = 5, scale = 2)
    private BigDecimal branchCoveragePercent;

    private Integer complexity;

    @Column(nullable = false)
    private Instant createdAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public CoverageReport getCoverageReport() {
        return coverageReport;
    }

    public void setCoverageReport(CoverageReport coverageReport) {
        this.coverageReport = coverageReport;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

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

    public Integer getComplexity() {
        return complexity;
    }

    public void setComplexity(Integer complexity) {
        this.complexity = complexity;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
