package com.qualitywatch.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Maps platform-provided {@code DATABASE_URL} (postgres://…) to Spring datasource properties.
 */
public class CloudDatabaseEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final String PROPERTY_SOURCE = "cloudDatabase";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (StringUtils.hasText(environment.getProperty("SPRING_DATASOURCE_URL"))) {
            return;
        }

        String databaseUrl = environment.getProperty("DATABASE_URL");
        if (!StringUtils.hasText(databaseUrl)) {
            return;
        }

        try {
            URI uri = URI.create(databaseUrl.replace("postgres://", "postgresql://"));
            Map<String, Object> props = new HashMap<>();
            props.put("SPRING_DATASOURCE_URL", toJdbcUrl(uri));
            if (StringUtils.hasText(uri.getUserInfo())) {
                String[] userInfo = uri.getUserInfo().split(":", 2);
                props.put("SPRING_DATASOURCE_USERNAME", decode(userInfo[0]));
                if (userInfo.length > 1) {
                    props.put("SPRING_DATASOURCE_PASSWORD", decode(userInfo[1]));
                }
            }
            environment.getPropertySources().addFirst(new MapPropertySource(PROPERTY_SOURCE, props));
        } catch (RuntimeException ignored) {
            // Fall back to explicit SPRING_DATASOURCE_* / PG* env vars.
        }
    }

    private static String toJdbcUrl(URI uri) {
        String database = uri.getPath();
        if (database.startsWith("/")) {
            database = database.substring(1);
        }
        return "jdbc:postgresql://" + uri.getHost()
                + (uri.getPort() > 0 ? ":" + uri.getPort() : "")
                + "/" + database;
    }

    private static String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}
