# Backend — SOJUS HelpDesk Judicial

## Stack
- **Framework:** Spring Boot 3.2 (Java 17)
- **Build Tool:** Maven
- **ORM:** Spring Data JPA + Hibernate
- **Base de Datos:** PostgreSQL 16 (H2 para desarrollo)
- **Auth:** JWT (JJWT 0.12) + Spring Security + BCrypt
- **Docs:** SpringDoc OpenAPI (Swagger UI)
- **Testing:** Spring Boot Test + JUnit 5

## Estructura

```
backend/
├── pom.xml
└── src/main/java/com/sojus/
    ├── SojusApplication.java          # Punto de entrada
    ├── config/
    │   ├── DataInitializer.java       # Seed data demo
    │   └── OpenApiConfig.java         # Swagger config
    ├── controller/
    │   ├── AuthController.java        # POST /api/auth/login, GET /api/auth/me
    │   ├── TicketController.java      # CRUD tickets + cambio de estado
    │   ├── InventoryController.java   # CRUD hardware y software
    │   ├── ContractController.java    # CRUD contratos + alertas vencimiento
    │   ├── LocationController.java    # Estructura territorial
    │   ├── UserController.java        # ABM usuarios
    │   ├── AuditController.java       # Logs de auditoría
    │   └── DashboardController.java   # Estadísticas
    ├── domain/
    │   ├── entity/                    # JPA Entities
    │   └── enums/                     # RoleName, TicketStatus, Priority, AssetStatus
    ├── dto/                           # Request/Response DTOs
    ├── repository/                    # Spring Data JPA Repositories
    ├── security/
    │   ├── JwtTokenProvider.java      # Generación/validación JWT
    │   ├── JwtAuthenticationFilter.java
    │   └── SecurityConfig.java        # RBAC, CORS, stateless sessions
    └── service/                       # Business logic layer
```

## Endpoints principales

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/auth/login` | Login → JWT token |
| GET | `/api/auth/me` | Usuario actual |
| GET/POST | `/api/tickets` | Listar/Crear tickets |
| GET | `/api/tickets/{id}` | Detalle ticket |
| PATCH | `/api/tickets/{id}/status` | Cambiar estado |
| GET/POST | `/api/inventory/hardware` | Inventario Hardware |
| GET/POST | `/api/inventory/software` | Inventario Software |
| GET/POST | `/api/contracts` | Contratos |
| GET | `/api/contracts/expiring?days=30` | Alertas vencimiento |
| GET | `/api/locations/circunscripciones` | Estructura territorial |
| GET | `/api/locations/juzgados` | Juzgados |
| GET/POST | `/api/users` | ABM Usuarios |
| GET | `/api/audit` | Logs auditoría |
| GET | `/api/dashboard/stats` | Dashboard métricas |

## Setup

```bash
# Requisitos: JDK 17+, Maven 3.9+
cd backend
mvn clean compile
mvn spring-boot:run

# API:       http://localhost:8080
# Swagger:   http://localhost:8080/swagger-ui.html
# H2 Console: http://localhost:8080/h2-console
```

## Credenciales Demo

| Rol | Usuario | Contraseña |
|-----|---------|------------|
| Admin | `admin` | `admin123` |
| Operador | `operador` | `oper123` |
| Técnico | `tecnico` | `tec123` |
| Gestor Inventario | `gestor` | `gest123` |
| Auditor | `auditor` | `audit123` |
