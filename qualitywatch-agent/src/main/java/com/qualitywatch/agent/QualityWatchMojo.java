package com.qualitywatch.agent;

import com.qualitywatch.agent.collector.AllureCollector;
import com.qualitywatch.agent.collector.JaCoCoCollector;
import com.qualitywatch.agent.collector.ReportCollector;
import com.qualitywatch.agent.model.TelemetryPayload;
import com.qualitywatch.agent.uploader.TelemetryUploader;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Collects JaCoCo / Allure artifacts and uploads them to QualityWatch.
 */
@Mojo(name = "upload", defaultPhase = LifecyclePhase.VERIFY)
public class QualityWatchMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(property = "qualitywatch.serverUrl", defaultValue = "http://localhost:8080")
    private String serverUrl;

    @Parameter(property = "qualitywatch.projectName")
    private String projectName;

    @Parameter(property = "qualitywatch.apiKey")
    private String apiKey;

    @Parameter(property = "qualitywatch.buildNumber", defaultValue = "${env.BUILD_NUMBER}")
    private String buildNumber;

    @Parameter(property = "qualitywatch.branch", defaultValue = "main")
    private String branch;

    @Parameter(property = "qualitywatch.commitHash", defaultValue = "${env.GIT_COMMIT}")
    private String commitHash;

    @Parameter(property = "qualitywatch.skip", defaultValue = "false")
    private boolean skip;

    @Parameter(property = "qualitywatch.jacocoReportPath", defaultValue = "${project.build.directory}/site/jacoco/jacoco.xml")
    private String jacocoReportPath;

    @Parameter(property = "qualitywatch.allureResultsPath", defaultValue = "${project.build.directory}/allure-results")
    private String allureResultsPath;

    private final List<ReportCollector> collectors = new ArrayList<>();

    @Override
    public void execute() throws MojoExecutionException {
        if (skip) {
            getLog().info("QualityWatch upload skipped");
            return;
        }

        getLog().info("QualityWatch Agent starting...");

        collectors.clear();
        collectors.add(new JaCoCoCollector(new File(jacocoReportPath), getLog()));
        collectors.add(new AllureCollector(new File(allureResultsPath), getLog()));

        TelemetryPayload payload = buildPayload();

        TelemetryUploader uploader = new TelemetryUploader(serverUrl, apiKey, getLog());
        try {
            uploader.upload(payload);
            getLog().info("Successfully uploaded telemetry to QualityWatch");
        } catch (Exception e) {
            getLog().error("Failed to upload telemetry: " + e.getMessage(), e);
        }
    }

    private TelemetryPayload buildPayload() {
        TelemetryPayload payload = new TelemetryPayload();

        payload.setProjectName(projectName != null ? projectName : project.getArtifactId());
        payload.setBuildNumber(buildNumber != null && !buildNumber.isBlank()
                ? buildNumber
                : String.valueOf(System.currentTimeMillis()));
        payload.setBranch(branch);
        payload.setCommitHash(commitHash);
        payload.setTimestamp(System.currentTimeMillis());

        for (ReportCollector collector : collectors) {
            if (collector.isAvailable()) {
                try {
                    collector.collect(payload);
                } catch (Exception e) {
                    getLog().warn("Failed to collect from " + collector.getClass().getSimpleName() + ": " + e.getMessage());
                }
            }
        }

        return payload;
    }
}
