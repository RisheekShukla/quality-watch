CREATE TABLE flaky_tests (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID NOT NULL REFERENCES projects(id),
    test_class VARCHAR(500) NOT NULL,
    test_method VARCHAR(500) NOT NULL,
    failure_rate DECIMAL(5,2),
    total_executions INTEGER,
    failed_executions INTEGER,
    last_failed_at TIMESTAMP,
    detection_confidence VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(project_id, test_class, test_method)
);

CREATE INDEX idx_flaky_tests_project ON flaky_tests(project_id);
