package com.qualitywatch.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Profile("prod")
public class ProdEnvironmentValidator {

    private final QualityWatchProperties properties;

    public ProdEnvironmentValidator(QualityWatchProperties properties) {
        this.properties = properties;
    }

    @EventListener(ApplicationReadyEvent.class)
    void validateRequiredSecrets() {
        if (!StringUtils.hasText(properties.getApiKey())) {
            throw new IllegalStateException("QUALITYWATCH_API_KEY must be set when spring.profiles.active=prod");
        }
        if (!StringUtils.hasText(properties.getDashboard().getUsername())
                || !StringUtils.hasText(properties.getDashboard().getPassword())) {
            throw new IllegalStateException(
                    "QUALITYWATCH_DASHBOARD_USER and QUALITYWATCH_DASHBOARD_PASSWORD must be set in prod");
        }
        if (!StringUtils.hasText(properties.getCors().getAllowedOrigins())) {
            throw new IllegalStateException("QUALITYWATCH_CORS_ALLOWED_ORIGINS must be set in prod");
        }
    }
}
