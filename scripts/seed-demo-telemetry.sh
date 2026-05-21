#!/usr/bin/env bash
set -euo pipefail

API_URL="${1:-http://localhost:8080}"
API_KEY="${QUALITYWATCH_API_KEY:-}"

if [[ -z "$API_KEY" ]]; then
  echo "Set QUALITYWATCH_API_KEY (required for prod uploads)."
  exit 1
fi

echo "Seeding demo telemetry to ${API_URL} ..."

curl -sf -X POST "${API_URL}/api/v1/telemetry/upload" \
  -H "Content-Type: application/json" \
  -H "X-API-Key: ${API_KEY}" \
  -d '{
    "projectName": "demo-service",
    "buildNumber": "demo-build-1",
    "branch": "main",
    "commitHash": "abc1234",
    "timestamp": '"$(date +%s000)"',
    "coverage": {
      "lineCoveragePercent": 78.5,
      "branchCoveragePercent": 65.2,
      "instructionCoveragePercent": 72.0,
      "linesCovered": 785,
      "linesTotal": 1000,
      "branchesCovered": 130,
      "branchesTotal": 200,
      "moduleName": "demo-service",
      "packages": {
        "com.example.core": {
          "packageName": "com.example.core",
          "lineCoveragePercent": 85.0,
          "branchCoveragePercent": 70.0,
          "complexity": 42
        }
      }
    },
    "testExecution": {
      "totalTests": 4,
      "passedTests": 3,
      "failedTests": 1,
      "skippedTests": 0,
      "totalDurationMs": 420,
      "tests": [
        {
          "suiteName": "unit",
          "className": "com.example.DemoTest",
          "methodName": "shouldPass",
          "status": "PASSED",
          "durationMs": 50
        },
        {
          "suiteName": "unit",
          "className": "com.example.DemoTest",
          "methodName": "shouldAlsoPass",
          "status": "PASSED",
          "durationMs": 30
        },
        {
          "suiteName": "unit",
          "className": "com.example.FlakyTest",
          "methodName": "sometimesFails",
          "status": "FAILED",
          "durationMs": 120,
          "errorMessage": "AssertionError: expected true"
        },
        {
          "suiteName": "integration",
          "className": "com.example.ApiTest",
          "methodName": "healthCheck",
          "status": "PASSED",
          "durationMs": 220
        }
      ]
    }
  }'

echo ""
echo "Done. Open the dashboard and select project 'demo-service'."
echo "Processing is async — wait a few seconds for charts to populate."
