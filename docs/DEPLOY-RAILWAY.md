# Railway deployment guide for QualityWatch (resume / public demo)

Deploy QualityWatch on [Railway](https://railway.app) to get HTTPS URLs without managing a VPS.

## Architecture on Railway

| Service | Source | Notes |
|---------|--------|-------|
| Postgres | Railway plugin | Auto `DATABASE_URL` — map to Spring datasource env vars |
| RabbitMQ | Docker image service or [CloudAMQP](https://www.cloudamqp.com/) free tier | Railway has no native RabbitMQ plugin |
| Backend | [`docker/backend/Dockerfile`](../docker/backend/Dockerfile) | Set `SPRING_PROFILES_ACTIVE=prod` |
| Frontend | [`qualitywatch-frontend/Dockerfile`](../qualitywatch-frontend/Dockerfile) | Build args for API URL + dashboard auth |

## Step 1 — Create project

1. Sign in to Railway → **New Project** → **Deploy from GitHub repo** (this repository).
2. Add **PostgreSQL** from the template marketplace.

## Step 2 — Backend service

1. **New Service** → use repo root, Dockerfile path: `docker/backend/Dockerfile`.
2. Set environment variables:

| Variable | Value |
|----------|--------|
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://${{Postgres.PGHOST}}:${{Postgres.PGPORT}}/${{Postgres.PGDATABASE}}` |
| `SPRING_DATASOURCE_USERNAME` | `${{Postgres.PGUSER}}` |
| `SPRING_DATASOURCE_PASSWORD` | `${{Postgres.PGPASSWORD}}` |
| `SPRING_RABBITMQ_HOST` | RabbitMQ host |
| `SPRING_RABBITMQ_PORT` | `5672` |
| `SPRING_RABBITMQ_USERNAME` | RabbitMQ user |
| `SPRING_RABBITMQ_PASSWORD` | RabbitMQ password |
| `QUALITYWATCH_API_KEY` | Strong random string (uploads) |
| `QUALITYWATCH_DASHBOARD_USER` | e.g. `demo` |
| `QUALITYWATCH_DASHBOARD_PASSWORD` | Strong password (viewers of dashboard) |
| `QUALITYWATCH_CORS_ALLOWED_ORIGINS` | Frontend public URL (set after Step 3) |

3. **Settings → Networking → Generate Domain** → note URL e.g. `https://qualitywatch-api-production.up.railway.app`

## Step 3 — RabbitMQ

Option A — **Separate Railway service** from image `rabbitmq:3-management-alpine` with `RABBITMQ_DEFAULT_USER` / `RABBITMQ_DEFAULT_PASS`. Use the internal hostname for `SPRING_RABBITMQ_HOST`.

Option B — **CloudAMQP** free plan; paste AMQP credentials into backend env.

## Step 4 — Frontend service

1. **New Service** → Dockerfile path: `qualitywatch-frontend/Dockerfile`.
2. **Build arguments** (Railway → Variables → build-time):

| Build arg | Value |
|-----------|--------|
| `VITE_API_BASE_URL` | Backend public URL from Step 2 |
| `VITE_DASHBOARD_USER` | Same as `QUALITYWATCH_DASHBOARD_USER` |
| `VITE_DASHBOARD_PASSWORD` | Same as `QUALITYWATCH_DASHBOARD_PASSWORD` |

3. Generate public domain e.g. `https://qualitywatch-web-production.up.railway.app`
4. Update backend `QUALITYWATCH_CORS_ALLOWED_ORIGINS` to this frontend URL and redeploy backend.

## Step 5 — Seed demo data

```bash
export QUALITYWATCH_API_KEY=your-upload-key
./scripts/seed-demo-telemetry.sh https://your-backend.up.railway.app
```

Open the frontend URL → log in with dashboard credentials when prompted (browser HTTP Basic auth) → select **demo-service**.

## Resume links

After deploy, add to your resume/README:

- **Live demo:** `https://your-frontend.up.railway.app`
- **Credentials:** share dashboard user/password privately or in interview

## Troubleshooting

- **Empty dashboard:** run seed script; wait ~10s for RabbitMQ consumer.
- **CORS errors:** `QUALITYWATCH_CORS_ALLOWED_ORIGINS` must exactly match frontend origin (no trailing slash).
- **401 on API:** frontend build must include matching `VITE_DASHBOARD_*` credentials.
- **Backend crash on start:** prod profile requires all secrets — see [`application-prod.yml`](../qualitywatch-backend/src/main/resources/application-prod.yml).
