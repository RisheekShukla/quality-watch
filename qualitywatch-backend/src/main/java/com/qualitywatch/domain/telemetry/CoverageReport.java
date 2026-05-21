package com.qualitywatch.domain.telemetry;

import com.qualitywatch.domain.common.Build;
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
@Table(name = "coverage_reports")
public class CoverageReport {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "build_id", nullable = false)
    private Build build;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "telemetry_event_id")
    private TelemetryEvent telemetryEvent;

    private String moduleName;

    @Column(precision = 5, scale = 2)
    private BigDecimal lineCoveragePercent;

    @Column(precision = 5, scale = 2)
    private BigDecimal branchCoveragePercent;

    @Column(precision = 5, scale = 2)
    private BigDecimal instructionCoveragePercent;

    private Integer linesCovered;
    private Integer linesTotal;
    private Integer branchesCovered;
    private Integer branchesTotal;

    @Column(nullable = false)
    private Instant createdAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Build getBuild() {
        return build;
    }

    public void setBuild(Build build) {
        this.build = build;
    }

    public TelemetryEvent getTelemetryEvent() {
        return telemetryEvent;
    }

    public void setTelemetryEvent(TelemetryEvent telemetryEvent) {
        this.telemetryEvent = telemetryEvent;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
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

    public BigDecimal getInstructionCoveragePercent() {
        return instructionCoveragePercent;
    }

    public void setInstructionCoveragePercent(BigDecimal instructionCoveragePercent) {
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
