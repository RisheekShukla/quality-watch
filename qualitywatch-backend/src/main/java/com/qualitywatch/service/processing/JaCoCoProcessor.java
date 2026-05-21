package com.qualitywatch.service.processing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qualitywatch.domain.telemetry.CoveragePackage;
import com.qualitywatch.domain.telemetry.CoverageReport;
import com.qualitywatch.domain.telemetry.TelemetryEvent;
import com.qualitywatch.repository.CoveragePackageRepository;
import com.qualitywatch.repository.CoverageReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Iterator;
import java.util.Map;

@Service
public class JaCoCoProcessor {

    private static final Logger log = LoggerFactory.getLogger(JaCoCoProcessor.class);

    private final CoverageReportRepository coverageReportRepository;
    private final CoveragePackageRepository coveragePackageRepository;
    private final ObjectMapper objectMapper;

    public JaCoCoProcessor(
            CoverageReportRepository coverageReportRepository,
            CoveragePackageRepository coveragePackageRepository,
            ObjectMapper objectMapper) {
        this.coverageReportRepository = coverageReportRepository;
        this.coveragePackageRepository = coveragePackageRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void process(TelemetryEvent event) {
        Map<String, Object> payload = event.getPayload();
        if (payload == null || payload.get("coverage") == null) {
            return;
        }

        coverageReportRepository.deleteAllByTelemetryEvent_Id(event.getId());

        JsonNode root = objectMapper.valueToTree(payload.get("coverage"));

        CoverageReport report = new CoverageReport();
        report.setBuild(event.getBuild());
        report.setTelemetryEvent(event);
        report.setModuleName(text(root, "moduleName"));
        report.setLineCoveragePercent(decimal(root, "lineCoveragePercent"));
        report.setBranchCoveragePercent(decimal(root, "branchCoveragePercent"));
        report.setInstructionCoveragePercent(decimal(root, "instructionCoveragePercent"));
        report.setLinesCovered(intVal(root, "linesCovered"));
        report.setLinesTotal(intVal(root, "linesTotal"));
        report.setBranchesCovered(intVal(root, "branchesCovered"));
        report.setBranchesTotal(intVal(root, "branchesTotal"));
        report.setCreatedAt(Instant.now());

        report = coverageReportRepository.save(report);

        JsonNode packages = root.get("packages");
        if (packages != null && packages.isObject()) {
            for (Iterator<Map.Entry<String, JsonNode>> it = packages.fields(); it.hasNext(); ) {
                Map.Entry<String, JsonNode> entry = it.next();
                JsonNode pkg = entry.getValue();
                CoveragePackage cp = new CoveragePackage();
                cp.setCoverageReport(report);
                cp.setPackageName(firstNonBlank(text(pkg, "packageName"), entry.getKey()));
                cp.setLineCoveragePercent(decimal(pkg, "lineCoveragePercent"));
                cp.setBranchCoveragePercent(decimal(pkg, "branchCoveragePercent"));
                cp.setComplexity(intVal(pkg, "complexity"));
                cp.setCreatedAt(Instant.now());
                coveragePackageRepository.save(cp);
            }
        }

        log.debug("Persisted coverage report {} for build {}", report.getId(), event.getBuild().getId());
    }

    private static String text(JsonNode node, String field) {
        JsonNode v = node.get(field);
        return v == null || v.isNull() ? null : v.asText(null);
    }

    private static Integer intVal(JsonNode node, String field) {
        JsonNode v = node.get(field);
        if (v == null || v.isNull() || !v.isNumber()) {
            return null;
        }
        return v.intValue();
    }

    private static BigDecimal decimal(JsonNode node, String field) {
        JsonNode v = node.get(field);
        if (v == null || v.isNull() || !v.isNumber()) {
            return null;
        }
        return BigDecimal.valueOf(v.doubleValue()).setScale(2, RoundingMode.HALF_UP);
    }

    private static String firstNonBlank(String a, String b) {
        if (a != null && !a.isBlank()) {
            return a;
        }
        return b != null ? b : "";
    }
}
