package com.qualitywatch.messaging.consumer;

import com.qualitywatch.domain.telemetry.TelemetryEvent;
import com.qualitywatch.repository.TelemetryEventRepository;
import com.qualitywatch.messaging.producer.TelemetryProducer;
import com.qualitywatch.service.processing.AllureProcessor;
import com.qualitywatch.service.processing.JaCoCoProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Component
public class TelemetryConsumer {

    private static final Logger log = LoggerFactory.getLogger(TelemetryConsumer.class);

    private final TelemetryEventRepository telemetryEventRepository;
    private final JaCoCoProcessor jaCoCoProcessor;
    private final AllureProcessor allureProcessor;
    private final TelemetryProducer telemetryProducer;
    private final TransactionTemplate transactionTemplate;

    public TelemetryConsumer(
            TelemetryEventRepository telemetryEventRepository,
            JaCoCoProcessor jaCoCoProcessor,
            AllureProcessor allureProcessor,
            TelemetryProducer telemetryProducer,
            TransactionTemplate transactionTemplate) {
        this.telemetryEventRepository = telemetryEventRepository;
        this.jaCoCoProcessor = jaCoCoProcessor;
        this.allureProcessor = allureProcessor;
        this.telemetryProducer = telemetryProducer;
        this.transactionTemplate = transactionTemplate;
    }

    @RabbitListener(queues = "${qualitywatch.rabbitmq.queue.telemetry}")
    public void processTelemetryEvent(Map<String, Object> message) {
        UUID eventId = UUID.fromString(String.valueOf(message.get("eventId")));

        final UUID[] projectIdHolder = new UUID[1];

        try {
            transactionTemplate.executeWithoutResult(status -> {
                TelemetryEvent event = telemetryEventRepository.findWithAssociations(eventId)
                        .orElseThrow(() -> new IllegalStateException("Telemetry event not found: " + eventId));

                log.info("Processing telemetry event {}", eventId);

                event.setProcessingStatus("PROCESSING");
                telemetryEventRepository.save(event);

                Map<String, Object> payload = event.getPayload();
                if (payload != null && payload.get("coverage") != null) {
                    jaCoCoProcessor.process(event);
                }
                if (payload != null && payload.get("testExecution") != null) {
                    allureProcessor.process(event);
                }

                event.setProcessingStatus("COMPLETED");
                event.setProcessedAt(Instant.now());
                telemetryEventRepository.save(event);

                projectIdHolder[0] = event.getBuild().getProject().getId();
            });

            if (projectIdHolder[0] != null) {
                telemetryProducer.sendAggregationProcessing(projectIdHolder[0], eventId);
            }

            log.info("Completed telemetry event {}", eventId);
        } catch (Exception e) {
            log.error("Failed telemetry event {}", eventId, e);
            transactionTemplate.executeWithoutResult(status ->
                    telemetryEventRepository.findById(eventId).ifPresent(ev -> {
                        ev.setProcessingStatus("FAILED");
                        telemetryEventRepository.save(ev);
                    }));
        }
    }
}
