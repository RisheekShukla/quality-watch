package com.qualitywatch.agent.collector;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.qualitywatch.agent.model.TelemetryPayload;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JaCoCoCollector implements ReportCollector {

    private final File reportFile;
    private final Log log;
    private final XmlMapper xmlMapper = new XmlMapper();

    public JaCoCoCollector(File reportFile, Log log) {
        this.reportFile = reportFile;
        this.log = log;
    }

    @Override
    public boolean isAvailable() {
        boolean exists = reportFile.exists() && reportFile.isFile();
        if (exists) {
            log.info("Found JaCoCo report at: " + reportFile.getAbsolutePath());
        }
        return exists;
    }

    @Override
    public void collect(TelemetryPayload payload) throws Exception {
        log.info("Parsing JaCoCo report...");

        JacocoReport report = xmlMapper.readValue(reportFile, JacocoReport.class);

        TelemetryPayload.CoverageData coverage = new TelemetryPayload.CoverageData();

        int totalLines = 0;
        int coveredLines = 0;
        int totalBranches = 0;
        int coveredBranches = 0;
        int totalInstructions = 0;
        int coveredInstructions = 0;

        List<JacocoPackage> packages = report.getPackages() != null ? report.getPackages() : List.of();
        for (JacocoPackage pkg : packages) {
            int pkgLinesMissed = 0;
            int pkgLinesCovered = 0;
            int pkgBranchesMissed = 0;
            int pkgBranchesCovered = 0;
            Integer complexity = null;

            List<JacocoCounter> counters = pkg.getCounters() != null ? pkg.getCounters() : List.of();
            for (JacocoCounter counter : counters) {
                switch (counter.getType()) {
                    case "LINE" -> {
                        pkgLinesMissed += counter.getMissed();
                        pkgLinesCovered += counter.getCovered();
                        totalLines += counter.getMissed() + counter.getCovered();
                        coveredLines += counter.getCovered();
                    }
                    case "BRANCH" -> {
                        pkgBranchesMissed += counter.getMissed();
                        pkgBranchesCovered += counter.getCovered();
                        totalBranches += counter.getMissed() + counter.getCovered();
                        coveredBranches += counter.getCovered();
                    }
                    case "INSTRUCTION" -> {
                        totalInstructions += counter.getMissed() + counter.getCovered();
                        coveredInstructions += counter.getCovered();
                    }
                    case "COMPLEXITY" -> complexity = counter.getMissed() + counter.getCovered();
                    default -> {
                    }
                }
            }

            TelemetryPayload.PackageCoverage pkgCoverage = new TelemetryPayload.PackageCoverage();
            pkgCoverage.setPackageName(pkg.getName());
            int lineTotal = pkgLinesMissed + pkgLinesCovered;
            pkgCoverage.setLineCoveragePercent(lineTotal > 0 ? (pkgLinesCovered * 100.0 / lineTotal) : 0.0);
            int branchTotal = pkgBranchesMissed + pkgBranchesCovered;
            pkgCoverage.setBranchCoveragePercent(branchTotal > 0 ? (pkgBranchesCovered * 100.0 / branchTotal) : 0.0);
            pkgCoverage.setComplexity(complexity);

            coverage.getPackages().put(pkg.getName(), pkgCoverage);
        }

        coverage.setLinesCovered(coveredLines);
        coverage.setLinesTotal(totalLines);
        coverage.setBranchesCovered(coveredBranches);
        coverage.setBranchesTotal(totalBranches);
        coverage.setLineCoveragePercent(totalLines > 0 ? (coveredLines * 100.0 / totalLines) : 0.0);
        coverage.setBranchCoveragePercent(totalBranches > 0 ? (coveredBranches * 100.0 / totalBranches) : 0.0);
        coverage.setInstructionCoveragePercent(
                totalInstructions > 0 ? (coveredInstructions * 100.0 / totalInstructions) : 0.0);

        payload.setCoverage(coverage);
        log.info("Collected coverage: " + coverage.getLineCoveragePercent() + "% line coverage");
    }

    @JacksonXmlRootElement(localName = "report")
    private static class JacocoReport {

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "package")
        private List<JacocoPackage> packages = new ArrayList<>();

        public List<JacocoPackage> getPackages() {
            return packages;
        }

        @SuppressWarnings("unused")
        public void setPackages(List<JacocoPackage> packages) {
            this.packages = packages;
        }
    }

    private static class JacocoPackage {

        @JacksonXmlProperty(isAttribute = true)
        private String name;

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "counter")
        private List<JacocoCounter> counters = new ArrayList<>();

        public String getName() {
            return name;
        }

        @SuppressWarnings("unused")
        public void setName(String name) {
            this.name = name;
        }

        public List<JacocoCounter> getCounters() {
            return counters;
        }

        @SuppressWarnings("unused")
        public void setCounters(List<JacocoCounter> counters) {
            this.counters = counters;
        }
    }

    private static class JacocoCounter {

        @JacksonXmlProperty(isAttribute = true)
        private String type;

        @JacksonXmlProperty(isAttribute = true)
        private int missed;

        @JacksonXmlProperty(isAttribute = true)
        private int covered;

        public String getType() {
            return type;
        }

        @SuppressWarnings("unused")
        public void setType(String type) {
            this.type = type;
        }

        public int getMissed() {
            return missed;
        }

        @SuppressWarnings("unused")
        public void setMissed(int missed) {
            this.missed = missed;
        }

        public int getCovered() {
            return covered;
        }

        @SuppressWarnings("unused")
        public void setCovered(int covered) {
            this.covered = covered;
        }
    }
}
