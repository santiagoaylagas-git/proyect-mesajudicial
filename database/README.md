# Base de Datos — SOJUS HelpDesk Judicial

## Motor
- **RDBMS:** PostgreSQL 16+
- **Extensiones:** pgcrypto, pg_trgm, pgvector (búsqueda semántica)

## Estructura

```
database/
├── migrations/          # Scripts de migración versionados
│   ├── V001_create_schema.sql
│   ├── V002_seed_locations.sql
│   ├── V003_seed_roles.sql
│   └── ...
├── seeds/               # Datos iniciales
│   ├── circunscripciones.sql
│   ├── distritos.sql
│   ├── roles_permisos.sql
│   └── demo_data.sql
├── schemas/             # DDL por módulo
│   ├── 01_auth.sql      # users, roles, permissions, sessions
│   ├── 02_locations.sql  # circunscriptions, districts, buildings, courts
│   ├── 03_inventory.sql  # hardware, software, asset_history
│   ├── 04_tickets.sql    # tickets, ticket_log, assignments
│   ├── 05_contracts.sql  # contracts, alerts, sla
│   └── 06_audit.sql      # audit_log
├── functions/           # Funciones y stored procedures
│   ├── fn_escalate_ticket.sql
│   ├── fn_audit_trigger.sql
│   └── fn_contract_alerts.sql
├── views/               # Vistas SQL
│   ├── vw_dashboard_stats.sql
│   └── vw_ticket_summary.sql
├── backups/             # Scripts de backup/restore
│   ├── backup.sh
│   └── restore.sh
├── erd.md               # Diagrama Entidad-Relación
└── README.md
```

## Entidades Principales
| Entidad | Descripción |
|---------|-------------|
| `users` | Usuarios del sistema con RBAC |
| `tickets` | Casos de mesa de ayuda |
| `hardware` | Inventario de equipos físicos |
| `software` | Inventario de licencias/programas |
| `contracts` | Contratos con proveedores |
| `locations` | Estructura territorial jerárquica |
| `audit_log` | Log inmutable de auditoría |
