package com.qualitywatch.service.analytics;

import com.qualitywatch.domain.analytics.FlakyTest;
import com.qualitywatch.domain.common.Project;
import com.qualitywatch.repository.FlakyTestRepository;
import com.qualitywatch.repository.ProjectRepository;
import com.qualitywatch.repository.TestExecutionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class FlakyTestDetectionService {

    private static final Logger log = LoggerFactory.getLogger(FlakyTestDetectionService.class);

    private final TestExecutionRepository testExecutionRepository;
    private final FlakyTestRepository flakyTestRepository;
    private final ProjectRepository projectRepository;

    public FlakyTestDetectionService(
            TestExecutionRepository testExecutionRepository,
            FlakyTestRepository flakyTestRepository,
            ProjectRepository projectRepository) {
        this.testExecutionRepository = testExecutionRepository;
        this.flakyTestRepository = flakyTestRepository;
        this.projectRepository = projectRepository;
    }

    @Transactional
    public void detectFlakyTests(UUID projectId) {
        Project project = projectRepository.findById(projectId).orElse(null);
        if (project == null) {
            return;
        }

        List<Object[]> rows = testExecutionRepository.aggregateOutcomesByTest(projectId);
        Instant now = Instant.now();

        for (Object[] row : rows) {
            String testClass = (String) row[0];
            String testMethod = (String) row[1];
            long total = ((Number) row[2]).longValue();
            long failed = ((Number) row[3]).longValue();
            long passed = ((Number) row[4]).longValue();

            if (total < 5 || passed == 0 || failed == 0) {
                continue;
            }

            double failureRate = (failed * 100.0) / total;
            if (failureRate <= 10 || failureRate >= 90) {
                continue;
            }

            FlakyTest flaky = flakyTestRepository
                    .findByProject_IdAndTestClassAndTestMethod(projectId, testClass, testMethod)
                    .orElseGet(FlakyTest::new);

            flaky.setProject(project);
            flaky.setTestClass(testClass);
            flaky.setTestMethod(testMethod);
            flaky.setFailureRate(BigDecimal.valueOf(failureRate).setScale(2, RoundingMode.HALF_UP));
            flaky.setTotalExecutions((int) total);
            flaky.setFailedExecutions((int) failed);
            flaky.setLastFailedAt(now);
            flaky.setDetectionConfidence(failureRate > 30 && failureRate < 70 ? "HIGH" : "MEDIUM");
            if (flaky.getCreatedAt() == null) {
                flaky.setCreatedAt(now);
            }
            flaky.setUpdatedAt(now);

            flakyTestRepository.save(flaky);
        }

        log.debug("Flaky test detection finished for project {}", projectId);
    }
}
