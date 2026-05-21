#!/usr/bin/env bash
# Deploy QualityWatch to Railway (https://railway.app)
#
# Prerequisites:
#   npm install -g @railway/cli
#   railway login
#
# Usage: ./scripts/deploy-railway.sh

set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT"

if ! command -v railway >/dev/null 2>&1; then
  echo "Installing Railway CLI..."
  npm install -g @railway/cli
fi

if ! railway whoami >/dev/null 2>&1; then
  echo "Run 'railway login' first, then re-run this script."
  exit 1
fi

echo "==> Creating Railway project (if needed)"
if [[ ! -f .railway/project.json ]]; then
  railway init --name qualitywatch
fi

echo "==> Add PostgreSQL from Railway dashboard: Project → + New → Database → PostgreSQL"
echo "    Then link DATABASE_URL to the backend service."
echo ""
echo "==> Add RabbitMQ service: + New → Empty Service → Deploy rabbitmq:3-management-alpine"
echo "    Set RABBITMQ_DEFAULT_USER / RABBITMQ_DEFAULT_PASS and map to backend SPRING_RABBITMQ_*"
echo ""
echo "==> Deploy backend"
railway up --service backend --dockerfile docker/backend/Dockerfile "$ROOT" || \
  railway up --dockerfile docker/backend/Dockerfile "$ROOT"

echo ""
echo "==> Set backend env (Railway dashboard → backend service → Variables):"
cat <<'EOF'
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:postgresql://${{Postgres.PGHOST}}:${{Postgres.PGPORT}}/${{Postgres.PGDATABASE}}
SPRING_DATASOURCE_USERNAME=${{Postgres.PGUSER}}
SPRING_DATASOURCE_PASSWORD=${{Postgres.PGPASSWORD}}
SPRING_RABBITMQ_HOST=<rabbitmq internal host>
SPRING_RABBITMQ_PORT=5672
SPRING_RABBITMQ_USERNAME=qualitywatch
SPRING_RABBITMQ_PASSWORD=<rabbitmq password>
QUALITYWATCH_API_KEY=<random upload key>
QUALITYWATCH_DASHBOARD_USER=demo
QUALITYWATCH_DASHBOARD_PASSWORD=<dashboard password>
QUALITYWATCH_CORS_ALLOWED_ORIGINS=<frontend public https URL>
EOF

echo ""
echo "==> Generate public domains for backend + frontend in Railway → Networking"
echo "==> Deploy frontend with build args (frontend service → Variables):"
echo "    VITE_API_BASE_URL=<backend https URL>"
echo "    VITE_DASHBOARD_USER=demo"
echo "    VITE_DASHBOARD_PASSWORD=<same as backend>"
echo ""
echo "==> Seed demo data:"
echo "    export QUALITYWATCH_API_KEY=<your key>"
echo "    ./scripts/seed-demo-telemetry.sh https://<backend-domain>"
