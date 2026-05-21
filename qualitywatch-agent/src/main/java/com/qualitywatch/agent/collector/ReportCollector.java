package com.qualitywatch.agent.collector;

import com.qualitywatch.agent.model.TelemetryPayload;

public interface ReportCollector {

    boolean isAvailable();

    void collect(TelemetryPayload payload) throws Exception;
}
