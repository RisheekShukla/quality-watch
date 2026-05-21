CREATE TABLE test_executions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    build_id UUID NOT NULL REFERENCES builds(id),
    telemetry_event_id UUID REFERENCES telemetry_events(id),
    test_suite VARCHAR(500) NOT NULL,
    test_class VARCHAR(500) NOT NULL,
    test_method VARCHAR(500) NOT NULL,
    status VARCHAR(50) NOT NULL,
    duration_ms BIGINT,
    error_message TEXT,
    stack_trace TEXT,
    executed_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_test_executions_build ON test_executions(build_id);
CREATE INDEX idx_test_executions_status ON test_executions(status);
CREATE INDEX idx_test_executions_executed_at ON test_executions(executed_at DESC);
