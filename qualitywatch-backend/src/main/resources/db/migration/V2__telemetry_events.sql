CREATE TABLE telemetry_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    build_id UUID NOT NULL REFERENCES builds(id),
    event_type VARCHAR(50) NOT NULL,
    payload JSONB NOT NULL,
    received_at TIMESTAMP NOT NULL DEFAULT NOW(),
    processed_at TIMESTAMP,
    processing_status VARCHAR(50) NOT NULL DEFAULT 'PENDING'
);

CREATE INDEX idx_telemetry_events_build ON telemetry_events(build_id);
CREATE INDEX idx_telemetry_events_status ON telemetry_events(processing_status) WHERE processing_status = 'PENDING';
