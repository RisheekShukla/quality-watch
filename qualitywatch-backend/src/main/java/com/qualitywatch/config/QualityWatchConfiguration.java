package com.qualitywatch.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(QualityWatchProperties.class)
public class QualityWatchConfiguration {
}
