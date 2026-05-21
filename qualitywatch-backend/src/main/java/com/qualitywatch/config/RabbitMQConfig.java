package com.qualitywatch.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {

    /** Dead-letter exchange for failed telemetry processing after retries. */
    public static final String DLX_EXCHANGE_NAME = "qualitywatch.dlx";

    public static final String TELEMETRY_DLQ_QUEUE_NAME = "qualitywatch.telemetry.dlq";

    public static final String TELEMETRY_DLQ_ROUTING_KEY = "telemetry.failed";

    private final QualityWatchProperties properties;

    public RabbitMQConfig(QualityWatchProperties properties) {
        this.properties = properties;
    }

    @Bean
    public TopicExchange qualityWatchExchange() {
        return new TopicExchange(properties.getRabbitmq().getExchange());
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX_EXCHANGE_NAME);
    }

    @Bean
    public Queue telemetryDeadLetterQueue() {
        return QueueBuilder.durable(TELEMETRY_DLQ_QUEUE_NAME).build();
    }

    @Bean
    public Binding telemetryDeadLetterBinding() {
        return BindingBuilder.bind(telemetryDeadLetterQueue())
                .to(deadLetterExchange())
                .with(TELEMETRY_DLQ_ROUTING_KEY);
    }

    @Bean
    public Queue telemetryQueue() {
        return QueueBuilder.durable(properties.getRabbitmq().getQueue().getTelemetry())
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE_NAME)
                .withArgument("x-dead-letter-routing-key", TELEMETRY_DLQ_ROUTING_KEY)
                .withArgument("x-message-ttl", 86400000)
                .build();
    }

    @Bean
    public Queue aggregationQueue() {
        return QueueBuilder.durable(properties.getRabbitmq().getQueue().getAggregation()).build();
    }

    @Bean
    public Binding telemetryBinding() {
        return BindingBuilder.bind(telemetryQueue())
                .to(qualityWatchExchange())
                .with(properties.getRabbitmq().getRoutingKey().getTelemetry());
    }

    @Bean
    public Binding aggregationBinding() {
        return BindingBuilder.bind(aggregationQueue())
                .to(qualityWatchExchange())
                .with(properties.getRabbitmq().getRoutingKey().getAggregation());
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jackson2JsonMessageConverter());
        return template;
    }
}
