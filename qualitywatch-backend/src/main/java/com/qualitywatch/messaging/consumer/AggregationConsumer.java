package com.qualitywatch.messaging.consumer;

import com.qualitywatch.service.CoverageTrendRefreshService;
import com.qualitywatch.service.analytics.FlakyTestDetectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

/**
 * Follow-up work after telemetry rows are persisted: refresh coverage MV and recompute flaky tests.
 */
@Component
public class AggregationConsumer {

    private static final Logger log = LoggerFactory.getLogger(AggregationConsumer.class);

    private final CoverageTrendRefreshService coverageTrendRefreshService;
    private final FlakyTestDetectionService flakyTestDetectionService;

    public AggregationConsumer(
            CoverageTrendRefreshService coverageTrendRefreshService,
            FlakyTestDetectionService flakyTestDetectionService) {
        this.coverageTrendRefreshService = coverageTrendRefreshService;
        this.flakyTestDetectionService = flakyTestDetectionService;
    }

    @RabbitListener(queues = "${qualitywatch.rabbitmq.queue.aggregation}")
    public void processAggregation(Map<String, Object> message) {
        UUID projectId = UUID.fromString(String.valueOf(message.get("projectId")));
        UUID eventId = UUID.fromString(String.valueOf(message.get("eventId")));

        log.info("Aggregation job for project {} (event {})", projectId, eventId);

        coverageTrendRefreshService.refreshCoverageTrends();
        flakyTestDetectionService.detectFlakyTests(projectId);

        log.info("Aggregation finished for event {}", eventId);
    }
}
