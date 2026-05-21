CREATE TABLE coverage_reports (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    build_id UUID NOT NULL REFERENCES builds(id),
    telemetry_event_id UUID REFERENCES telemetry_events(id),
    module_name VARCHAR(255),
    line_coverage_percent DECIMAL(5,2),
    branch_coverage_percent DECIMAL(5,2),
    instruction_coverage_percent DECIMAL(5,2),
    lines_covered INTEGER,
    lines_total INTEGER,
    branches_covered INTEGER,
    branches_total INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE coverage_packages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    coverage_report_id UUID NOT NULL REFERENCES coverage_reports(id) ON DELETE CASCADE,
    package_name VARCHAR(500) NOT NULL,
    line_coverage_percent DECIMAL(5,2),
    branch_coverage_percent DECIMAL(5,2),
    complexity INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_coverage_reports_build ON coverage_reports(build_id);
