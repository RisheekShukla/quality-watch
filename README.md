# QualityWatch

Engineering quality observability: ingest JaCoCo and Allure telemetry, persist to PostgreSQL, process asynchronously via RabbitMQ, and visualize coverage, flaky tests, and build health.

**Live demo (after deploy):** set your Railway/VPS URLs here — see [Deploy](#deploy-public-resume-demo).

| Link | URL |
|------|-----|
| Dashboard | `https://YOUR-FRONTEND-URL` |
| API | `https://YOUR-BACKEND-URL` |

## Prerequisites

- Java 21, Maven 3.9+
- Node 20+ (for the dashboard)
- Docker (recommended for local and production Compose)

## Quick start (local)

### 1. Infrastructure

```bash
cd docker && docker compose up -d postgres rabbitmq
```

### 2. Backend

```bash
cd qualitywatch-backend && mvn spring-boot:run
```

- API: http://localhost:8080
- Swagger (dev only): http://localhost:8080/swagger-ui.html

### 3. Frontend dashboard

```bash
cd qualitywatch-frontend && npm install && npm run dev
```

Open **http://localhost:5173** — routes: `/dashboard`, `/coverage`, `/tests`, `/builds`.

### 4. Seed demo data

```bash
export QUALITYWATCH_API_KEY=dev-secret   # optional locally if unset
chmod +x scripts/seed-demo-telemetry.sh
./scripts/seed-demo-telemetry.sh http://localhost:8080
```

## Production stack (Docker Compose)

Full stack with secured prod profile + frontend on port 3000:

```bash
cd docker
cp .env.example .env   # edit secrets and URLs
docker compose -f docker-compose.prod.yml --env-file .env up --build -d
```

| Service | URL |
|---------|-----|
| Dashboard | http://localhost:3000 |
| API | http://localhost:8080 |

Then seed: `./scripts/seed-demo-telemetry.sh http://localhost:8080`

## Deploy (public resume demo)

Two options for a **public HTTPS link**:

### Option A — Render (recommended, free tier)

One-click Blueprint from GitHub — see **[docs/DEPLOY-RENDER.md](docs/DEPLOY-RENDER.md)**.

1. Push repo to GitHub
2. Render → **New Blueprint** → connect repo ([`render.yaml`](render.yaml))
3. Add free [CloudAMQP](https://www.cloudamqp.com/) RabbitMQ credentials to backend env
4. Seed demo data and copy frontend URL to your resume

### Option B — Railway (all-in-one Docker)

Full guide: **[docs/DEPLOY-RAILWAY.md](docs/DEPLOY-RAILWAY.md)** · helper script: `./scripts/deploy-railway.sh`

1. `railway login` then `./scripts/deploy-railway.sh`
2. Add Postgres + RabbitMQ services in Railway dashboard
3. Set env vars from [docker/.env.example](docker/.env.example)
4. Seed and share frontend URL

Config templates: [`railway.backend.toml`](railway.backend.toml), [`railway.frontend.toml`](railway.frontend.toml).

### Environment variables (production)

| Variable | Purpose |
|----------|---------|
| `SPRING_PROFILES_ACTIVE` | Set to `prod` |
| `SPRING_DATASOURCE_URL` | JDBC URL to Postgres |
| `SPRING_DATASOURCE_USERNAME` / `PASSWORD` | DB credentials |
| `SPRING_RABBITMQ_HOST` / `PORT` / `USERNAME` / `PASSWORD` | RabbitMQ |
| `QUALITYWATCH_API_KEY` | Required — protects telemetry upload |
| `QUALITYWATCH_DASHBOARD_USER` / `PASSWORD` | HTTP Basic auth for dashboard API reads |
| `QUALITYWATCH_CORS_ALLOWED_ORIGINS` | Public frontend URL (exact origin) |
| `VITE_API_BASE_URL` | Frontend build — backend public URL |
| `VITE_DASHBOARD_USER` / `PASSWORD` | Frontend build — same as dashboard credentials |

## Upload telemetry (curl)

```bash
curl -X POST http://localhost:8080/api/v1/telemetry/upload \
  -H "Content-Type: application/json" \
  -H "X-API-Key: ${QUALITYWATCH_API_KEY}" \
  -d '{
    "projectName": "my-service",
    "buildNumber": "42",
    "branch": "main",
    "commitHash": "deadbeef",
    "timestamp": 1715000000000,
    "coverage": {
      "lineCoveragePercent": 80.0,
      "branchCoveragePercent": 70.0,
      "linesCovered": 800,
      "linesTotal": 1000
    },
    "testExecution": {
      "tests": [{
        "suiteName": "unit",
        "className": "com.example.MyTest",
        "methodName": "shouldWork",
        "status": "PASSED",
        "durationMs": 100
      }]
    }
  }'
```

Or run `./scripts/seed-demo-telemetry.sh`.

## Maven agent (CI integration)

Install locally:

```bash
cd qualitywatch-agent && mvn install
```

Add to your project `pom.xml`:

```xml
<plugin>
  <groupId>com.qualitywatch</groupId>
  <artifactId>qualitywatch-agent</artifactId>
  <version>1.0.0-SNAPSHOT</version>
  <configuration>
    <serverUrl>https://YOUR-BACKEND-URL</serverUrl>
    <projectName>my-service</projectName>
    <apiKey>${env.QUALITYWATCH_API_KEY}</apiKey>
  </configuration>
  <executions>
    <execution>
      <goals>
        <goal>upload</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

Runs on `mvn verify` (default phase). Upload is non-blocking — build won't fail if QualityWatch is down.

## Testing

```bash
mvn verify   # from repo root
```

Integration tests use Testcontainers when Docker is available; skipped otherwise.

## Architecture

- **Ingest:** `POST /api/v1/telemetry/upload` → Postgres event → RabbitMQ
- **Process:** consumer parses JaCoCo/Allure JSON → normalized tables
- **Aggregate:** aggregation queue refreshes coverage trends + flaky test detection
- **Dashboard:** React SPA reads `/api/v1/analytics/*`

Messaging contracts and DLQ behavior are documented in the previous README section on RabbitMQ.
