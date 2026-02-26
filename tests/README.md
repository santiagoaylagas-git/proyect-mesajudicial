# Testing — SOJUS HelpDesk Judicial

## Estrategia de Testing

```
tests/
├── e2e/                          # Tests end-to-end
│   ├── playwright/               # Tests con Playwright
│   │   ├── login.spec.ts
│   │   ├── tickets.spec.ts
│   │   ├── inventory.spec.ts
│   │   └── playwright.config.ts
│   └── fixtures/                 # Datos de prueba
├── load/                         # Tests de carga
│   ├── k6/                       # Scripts k6
│   │   ├── stress-test.js
│   │   └── spike-test.js
│   └── results/                  # Resultados
├── security/                     # Tests de seguridad
│   ├── owasp-zap/
│   └── penetration/
└── qa/                           # QA manual
    ├── test-plans/               # Planes de testing
    ├── test-cases/               # Casos de prueba
    └── bug-reports/              # Reportes de bugs
```

## Niveles de Testing
| Nivel | Tool | Ubicación |
|-------|------|-----------|
| Unit | xUnit (.NET) / Jest (React) | `backend/tests/` y `frontend/src/**/*.test.ts` |
| Integration | xUnit + TestContainers | `backend/tests/integration/` |
| E2E | Playwright | `tests/e2e/` |
| Load | k6 | `tests/load/` |
| Security | OWASP ZAP | `tests/security/` |
