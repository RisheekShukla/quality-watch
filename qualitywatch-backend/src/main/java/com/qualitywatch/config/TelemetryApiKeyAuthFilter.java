package com.qualitywatch.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TelemetryApiKeyAuthFilter extends OncePerRequestFilter {

    private final QualityWatchProperties properties;

    public TelemetryApiKeyAuthFilter(QualityWatchProperties properties) {
        this.properties = properties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!HttpMethod.POST.matches(request.getMethod())
                || !"/api/v1/telemetry/upload".equals(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        String configured = properties.getApiKey();
        if (!StringUtils.hasText(configured)) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("X-API-Key");
        if (!configured.equals(header)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or missing API key");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
