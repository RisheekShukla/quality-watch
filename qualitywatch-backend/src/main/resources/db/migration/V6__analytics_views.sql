CREATE MATERIALIZED VIEW coverage_trends AS
SELECT
    p.id AS project_id,
    p.name AS project_name,
    b.branch,
    DATE(b.build_timestamp) AS date,
    AVG(cr.line_coverage_percent) AS avg_line_coverage,
    AVG(cr.branch_coverage_percent) AS avg_branch_coverage,
    COUNT(DISTINCT b.id) AS build_count
FROM projects p
JOIN builds b ON b.project_id = p.id
JOIN coverage_reports cr ON cr.build_id = b.id
GROUP BY p.id, p.name, b.branch, DATE(b.build_timestamp);

CREATE UNIQUE INDEX idx_coverage_trends_unique ON coverage_trends (project_id, branch, date);
