# QualityWatch

> Tracks test coverage and build health from CI in one dashboard.

**Live demo:** `https://YOUR-FRONTEND-URL` · **API:** `https://YOUR-BACKEND-URL`  
*(Update after deploy — see [Deploy](#deploy))*

---

## What is this?

QualityWatch collects quality signals from your Java builds and shows them in one place:

| You get | From |
|---------|------|
| Coverage trends | JaCoCo reports |
| Flaky test detection | Allure / test execution data |
| Build health history | CI build metadata + test outcomes |

**Flow:** CI uploads telemetry → API stores event → RabbitMQ processes async → Dashboard reads analytics.

---

## What's in the repo?

| Module | Purpose |
|--------|---------|
| `qualitywatch-backend` | Spring Boot API, Postgres, RabbitMQ workers |
| `qualitywatch-frontend` | React dashboard (Vite + Tailwind + Recharts) |
| `qualitywatch-agent` | Maven plugin — auto-upload on `mvn verify` |
| `docker/` | Local & production Docker Compose |
| `scripts/` | Demo seed script, deploy helpers |

**Tech stack:** Java 21 · Spring Boot 3 · PostgreSQL · Flyway · RabbitMQ · React · Docker · GitHub Actions · Testcontainers

---

## Quick start

### Option A — Full stack in Docker (recommended)

**Prerequisites:** Docker Desktop

```bash
cd docker
cp .env.example .env   # edit secrets if needed
docker compose -f docker-compose.prod.yml --env-file .env up --build -d
```

| Service | URL |
|---------|-----|
| Dashboard | http://localhost:3000 |
| API | http://localhost:8080 |

Seed demo data:

```bash
export QUALITYWATCH_API_KEY=local-upload-key   # match docker/.env
./scripts/seed-demo-telemetry.sh http://localhost:8080
```

Open **http://localhost:3000** → select project **demo-service**.

### Option B — Dev mode (backend + frontend separately)

**Prerequisites:** Java 21, Maven 3.9+, Node 20+, Docker

```bash
# 1. Infrastructure
cd docker && docker compose up -d postgres rabbitmq

# 2. Backend
cd qualitywatch-backend && mvn spring-boot:run
# API: http://localhost:8080 · Swagger: http://localhost:8080/swagger-ui.html

# 3. Frontend
cd qualitywatch-frontend && npm install && npm run dev
# Dashboard: http://localhost:5173
```

Routes: `/dashboard`, `/coverage`, `/tests`, `/builds`.

---

## Deploy

| Platform | Guide |
|----------|--------|
| **Render** (recommended, free tier) | [docs/DEPLOY-RENDER.md](docs/DEPLOY-RENDER.md) |
| **Railway** | [docs/DEPLOY-RAILWAY.md](docs/DEPLOY-RAILWAY.md) |

**Render quick steps:**

1. Push repo to GitHub
2. Render → **New Blueprint** → connect repo ([`render.yaml`](render.yaml))
3. Add [CloudAMQP](https://www.cloudamqp.com/) credentials to backend env (see deploy doc)
4. Set Postgres JDBC URL correctly (host only — username/password as separate vars)
5. Seed demo data → add frontend URL to README and resume

Production env reference: [docker/.env.example](docker/.env.example) · Railway configs: [`railway.backend.toml`](railway.backend.toml), [`railway.frontend.toml`](railway.frontend.toml)

---

## CI integration

Install the Maven plugin locally:

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

Runs on `mvn verify`. Upload is non-blocking — your build won't fail if QualityWatch is down.

---

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

Or run `./scripts/seed-demo-telemetry.sh http://localhost:8080`.

---

## Architecture

```
Maven CI ──► POST /api/v1/telemetry/upload ──► Postgres
                        │
                        └──► RabbitMQ ──► Process JaCoCo/Allure ──► Analytics
                                                      │
React Dashboard ◄── GET /api/v1/analytics/* ◄────────┘
```

- **Ingest:** API key–protected upload → event persisted → queued
- **Process:** JaCoCo/Allure JSON → normalized tables (idempotent)
- **Aggregate:** coverage materialized views + flaky test detection
- **Messaging:** retry policy + dead-letter queue for failed jobs

---

## Testing

```bash
mvn verify   # from repo root
```

Integration tests use Testcontainers when Docker is available; skipped otherwise. CI runs on every push via [`.github/workflows/ci.yml`](.github/workflows/ci.yml).
