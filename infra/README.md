# Infraestructura — SOJUS HelpDesk Judicial

## Stack
- **Contenedores:** Docker + Docker Compose
- **Orquestación:** Kubernetes (producción)
- **CI/CD:** GitHub Actions
- **Reverse Proxy:** Nginx / Traefik

## Estructura

```
infra/
├── docker/
│   ├── Dockerfile.frontend      # Build React app
│   ├── Dockerfile.backend       # Build .NET API
│   ├── Dockerfile.agents        # Build Python agents
│   └── docker-compose.yml       # Stack completo local
├── kubernetes/
│   ├── namespaces/
│   ├── deployments/
│   │   ├── frontend.yaml
│   │   ├── backend.yaml
│   │   ├── postgres.yaml
│   │   └── agents.yaml
│   ├── services/
│   ├── configmaps/
│   ├── secrets/
│   └── ingress.yaml
├── nginx/
│   └── nginx.conf               # Reverse proxy config
├── scripts/
│   ├── setup-dev.sh             # Setup entorno development
│   ├── deploy-staging.sh        # Deploy a staging
│   └── deploy-prod.sh           # Deploy a producción
└── monitoring/
    ├── prometheus/
    └── grafana/
```

## Ambientes
| Ambiente | URL | Descripción |
|----------|-----|-------------|
| Local | localhost:5173 / :5000 | Desarrollo individual |
| Staging | staging.sojus.gob.ar | Testing y QA |
| Producción | sojus.gob.ar | Ambiente productivo |
