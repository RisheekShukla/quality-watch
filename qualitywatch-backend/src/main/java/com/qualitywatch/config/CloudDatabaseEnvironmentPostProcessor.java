package com.qualitywatch.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.StringUtils;

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
            ParsedPostgresUrl parsed = ParsedPostgresUrl.parse(databaseUrl);
            Map<String, Object> props = new HashMap<>();
            props.put("SPRING_DATASOURCE_URL", parsed.jdbcUrl());
            props.put("SPRING_DATASOURCE_USERNAME", parsed.username());
            props.put("SPRING_DATASOURCE_PASSWORD", parsed.password());
            environment.getPropertySources().addFirst(new MapPropertySource(PROPERTY_SOURCE, props));
        } catch (RuntimeException ignored) {
            // Fall back to explicit SPRING_DATASOURCE_* / PG* env vars.
        }
    }

    private record ParsedPostgresUrl(String username, String password, String host, int port, String database) {

        String jdbcUrl() {
            return "jdbc:postgresql://" + host + ":" + port + "/" + database;
        }

        static ParsedPostgresUrl parse(String databaseUrl) {
            String normalized = databaseUrl.trim();
            if (normalized.startsWith("postgres://")) {
                normalized = normalized.substring("postgres://".length());
            } else if (normalized.startsWith("postgresql://")) {
                normalized = normalized.substring("postgresql://".length());
            } else {
                throw new IllegalArgumentException("Unsupported DATABASE_URL scheme");
            }

            int at = normalized.lastIndexOf('@');
            if (at < 0) {
                throw new IllegalArgumentException("DATABASE_URL missing credentials");
            }

            String userInfo = normalized.substring(0, at);
            String hostAndPath = normalized.substring(at + 1);

            String username;
            String password = "";
            int colon = userInfo.indexOf(':');
            if (colon >= 0) {
                username = decode(userInfo.substring(0, colon));
                password = decode(userInfo.substring(colon + 1));
            } else {
                username = decode(userInfo);
            }

            int slash = hostAndPath.indexOf('/');
            if (slash < 0) {
                throw new IllegalArgumentException("DATABASE_URL missing database name");
            }

            String hostPort = hostAndPath.substring(0, slash);
            String database = hostAndPath.substring(slash + 1);
            int query = database.indexOf('?');
            if (query >= 0) {
                database = database.substring(0, query);
            }

            String host;
            int port = 5432;
            int portSep = hostPort.lastIndexOf(':');
            if (portSep >= 0) {
                host = hostPort.substring(0, portSep);
                port = Integer.parseInt(hostPort.substring(portSep + 1));
            } else {
                host = hostPort;
            }

            return new ParsedPostgresUrl(username, password, host, port, database);
        }

        private static String decode(String value) {
            return URLDecoder.decode(value, StandardCharsets.UTF_8);
        }
    }
}
