# Shared / Común — SOJUS HelpDesk Judicial

## Descripción
Código, tipos y contratos compartidos entre frontend, backend y agentes.

## Estructura

```
shared/
├── types/                  # Interfaces/tipos compartidos
│   ├── ticket.ts           # ITicket, TicketStatus, Priority
│   ├── user.ts             # IUser, Role, Permission
│   ├── hardware.ts         # IHardware, AssetStatus
│   ├── software.ts         # ISoftware, LicenseType
│   ├── contract.ts         # IContract, ContractStatus
│   ├── location.ts         # ILocation, LocationType
│   └── audit.ts            # IAuditLog, AuditAction
├── constants/              # Constantes compartidas
│   ├── roles.ts            # ROLES, PERMISSIONS
│   ├── status.ts           # TICKET_STATUSES, ASSET_STATUSES
│   └── config.ts           # APP_CONFIG
├── validators/             # Reglas de validación compartidas
│   ├── ticket.validator.ts
│   └── inventory.validator.ts
└── utils/                  # Utilidades compartidas
    ├── date-formatter.ts
    ├── id-generator.ts     # Formato INV-XXX-0000, CAS-YYYY-NNN
    └── sanitizer.ts
```
