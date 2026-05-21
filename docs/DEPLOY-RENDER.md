# Deploy QualityWatch on Render (free tier friendly)

Render runs **Postgres + backend + frontend**. RabbitMQ is external ([CloudAMQP](https://www.cloudamqp.com/) free plan).

## 1. Push this repo to GitHub

```bash
git init
git add .
git commit -m "QualityWatch MVP"
gh auth login   # if needed
gh repo create quality-watch --public --source=. --push
```

## 2. Create a Render Blueprint

1. Go to [Render Dashboard](https://dashboard.render.com/) → **New** → **Blueprint**
2. Connect your GitHub repo
3. Render reads [`render.yaml`](../render.yaml) and creates 3 resources

## 3. RabbitMQ (CloudAMQP — free)

1. Sign up at [cloudamqp.com](https://www.cloudamqp.com/) → create **Little Lemur** (free) instance
2. Copy AMQP details into **qualitywatch-backend** env on Render:

| Variable | Value |
|----------|--------|
| `SPRING_RABBITMQ_HOST` | e.g. `kangaroo.rmq.cloudamqp.com` |
| `SPRING_RABBITMQ_PORT` | `5672` |
| `SPRING_RABBITMQ_USERNAME` | from CloudAMQP |
| `SPRING_RABBITMQ_PASSWORD` | from CloudAMQP |

3. **Manual Deploy** backend after saving vars

## 4. Note generated secrets

From **qualitywatch-backend** → **Environment**:

- `QUALITYWATCH_API_KEY` — for CI uploads / seed script
- `QUALITYWATCH_DASHBOARD_PASSWORD` — dashboard login (user is `demo`)

Frontend rebuilds automatically with matching `VITE_DASHBOARD_PASSWORD`.

## 5. Seed demo data

```bash
export QUALITYWATCH_API_KEY=<from Render backend env>
./scripts/seed-demo-telemetry.sh https://qualitywatch-backend.onrender.com
```

Use your actual backend URL from Render → **Networking**.

## 6. Open the dashboard

Render → **qualitywatch-frontend** → public URL (e.g. `https://qualitywatch-frontend.onrender.com`)

- **Login:** `demo` / password from backend env
- **Project:** `demo-service`

## Resume links

- **Live demo:** frontend public URL
- **API health:** `https://<backend>/actuator/health`

## Troubleshooting

| Issue | Fix |
|-------|-----|
| Backend crash on start | Set all RabbitMQ vars; prod profile requires every secret |
| Empty charts | Run seed script; wait ~15s for async processing |
| CORS errors | `QUALITYWATCH_CORS_ALLOWED_ORIGINS` must match frontend `RENDER_EXTERNAL_URL` exactly |
| 401 on dashboard | Redeploy frontend after setting dashboard password on backend |

See also: [DEPLOY-RAILWAY.md](./DEPLOY-RAILWAY.md) for Railway (includes managed RabbitMQ container).
