package com.qualitywatch.messaging.producer;

import com.qualitywatch.config.QualityWatchProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class TelemetryProducer {

    private static final Logger log = LoggerFactory.getLogger(TelemetryProducer.class);

    private final RabbitTemplate rabbitTemplate;
    private final QualityWatchProperties properties;

    public TelemetryProducer(RabbitTemplate rabbitTemplate, QualityWatchProperties properties) {
        this.rabbitTemplate = rabbitTemplate;
        this.properties = properties;
    }

    public void sendTelemetryEvent(UUID eventId) {
        Map<String, Object> message = Map.of("eventId", eventId.toString());
        rabbitTemplate.convertAndSend(
                properties.getRabbitmq().getExchange(),
                properties.getRabbitmq().getRoutingKey().getTelemetry(),
                message);
        log.info("Queued telemetry event {}", eventId);
    }

    public void sendAggregationProcessing(UUID projectId, UUID eventId) {
        Map<String, Object> message = Map.of(
                "projectId", projectId.toString(),
                "eventId", eventId.toString());
        rabbitTemplate.convertAndSend(
                properties.getRabbitmq().getExchange(),
                properties.getRabbitmq().getRoutingKey().getAggregation(),
                message);
        log.info("Queued aggregation for project {} event {}", projectId, eventId);
    }
}
