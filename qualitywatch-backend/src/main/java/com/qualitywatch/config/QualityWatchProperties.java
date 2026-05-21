package com.qualitywatch.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "qualitywatch")
public class QualityWatchProperties {

    private String apiKey = "";
    private Cors cors = new Cors();
    private RabbitMq rabbitmq = new RabbitMq();
    private Schedule schedule = new Schedule();
    private Dashboard dashboard = new Dashboard();

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public Cors getCors() {
        return cors;
    }

    public void setCors(Cors cors) {
        this.cors = cors;
    }

    public RabbitMq getRabbitmq() {
        return rabbitmq;
    }

    public void setRabbitmq(RabbitMq rabbitmq) {
        this.rabbitmq = rabbitmq;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public Dashboard getDashboard() {
        return dashboard;
    }

    public void setDashboard(Dashboard dashboard) {
        this.dashboard = dashboard;
    }

    public static class Dashboard {
        private String username = "";
        private String password = "";

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class Cors {
        private String allowedOrigins = "http://localhost:5173";

        public String getAllowedOrigins() {
            return allowedOrigins;
        }

        public void setAllowedOrigins(String allowedOrigins) {
            this.allowedOrigins = allowedOrigins;
        }
    }

    public static class RabbitMq {
        private String exchange = "qualitywatch.telemetry";
        private QueueNames queue = new QueueNames();
        private RoutingKeys routingKey = new RoutingKeys();

        public String getExchange() {
            return exchange;
        }

        public void setExchange(String exchange) {
            this.exchange = exchange;
        }

        public QueueNames getQueue() {
            return queue;
        }

        public void setQueue(QueueNames queue) {
            this.queue = queue;
        }

        public RoutingKeys getRoutingKey() {
            return routingKey;
        }

        public void setRoutingKey(RoutingKeys routingKey) {
            this.routingKey = routingKey;
        }
    }

    public static class QueueNames {
        private String telemetry = "qualitywatch.telemetry.queue";
        private String aggregation = "qualitywatch.aggregation.queue";

        public String getTelemetry() {
            return telemetry;
        }

        public void setTelemetry(String telemetry) {
            this.telemetry = telemetry;
        }

        public String getAggregation() {
            return aggregation;
        }

        public void setAggregation(String aggregation) {
            this.aggregation = aggregation;
        }
    }

    public static class RoutingKeys {
        private String telemetry = "telemetry.upload";
        private String aggregation = "aggregation.process";

        public String getTelemetry() {
            return telemetry;
        }

        public void setTelemetry(String telemetry) {
            this.telemetry = telemetry;
        }

        public String getAggregation() {
            return aggregation;
        }

        public void setAggregation(String aggregation) {
            this.aggregation = aggregation;
        }
    }

    /**
     * Periodic tasks (milliseconds).
     */
    public static class Schedule {

        /** Fallback refresh for {@code coverage_trends} materialized view. */
        private long coverageTrendsMs = 300_000L;

        public long getCoverageTrendsMs() {
            return coverageTrendsMs;
        }

        public void setCoverageTrendsMs(long coverageTrendsMs) {
            this.coverageTrendsMs = coverageTrendsMs;
        }
    }
}
