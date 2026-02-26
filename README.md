# SOJUS — Sistema de Gestión Judicial
### Poder Judicial · Provincia de Santa Fe

> Sistema integral de Help Desk, Inventario y Gestión Territorial para la Dirección de Informática del Poder Judicial.

---

## 📁 Estructura del Proyecto

```
Proyecto_HelpDesk_Judicial/
│
├── 📂 backend/           → Spring Boot 3.2 (Java 17) — REST API
├── 📂 frontend/          → React Native (Expo) — App Móvil
├── 📂 database/          → PostgreSQL — Schemas, Migrations, Seeds
├── 📂 docs/api/postman/  → Colección Postman para testing de API
├── 📂 agents/            → Agentes IA (Python) — Chatbot, Clasificador
├── 📂 shared/            → Tipos, constantes y validadores compartidos
├── 📂 infra/             → Docker, Kubernetes, Nginx, CI/CD
├── 📂 tests/             → Tests E2E, Load, Security, QA
│
├── 📄 poc-helpdesk-judicial.html   → POC interactivo (self-contained)
├── 📄 SPEC.md                      → Especificación funcional
├── 📄 .gitignore
└── 📄 README.md                    → Este archivo
```

## 🏛️ Módulos Core

| Módulo | Descripción | Fase |
|--------|-------------|------|
| **Estructura Territorial** | Circunscripciones → Distritos → Edificios → Juzgados | 1 |
| **Seguridad (RBAC)** | Roles: Admin, Operador, Técnico, Gestor, Auditor | 1 |
| **Inventario HW/SW** | Rastreo de activos con ciclo de vida completo | 2 |
| **Contratos & Alertas** | Gestión de proveedores, SLA, vencimientos | 2 |
| **Mesa de Ayuda** | Tickets con escalamiento automático | 3 |
| **Portal de Autoservicio** | Catálogo de servicios + FAQ | 3 |
| **Integración Multicanal** | Email-to-Ticket, Chatbot IA | 4 |

## ⚙️ Stack Tecnológico

| Capa | Tecnología |
|------|------------|
| Frontend | React Native (Expo) + React Navigation |
| Backend | Spring Boot 3.2 (Java 17) + Spring Data JPA |
| Base de Datos | PostgreSQL 16 + JSONB (H2 para desarrollo) |
| Auth | JWT + Spring Security + BCrypt |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
| API Testing | Postman |
| Agentes IA | Python 3.11 + FastAPI + LangChain |
| Infraestructura | Docker + Kubernetes + Nginx |

## 🚀 Quick Start

### Backend (Spring Boot)
```bash
# Requisitos: JDK 17+, Maven 3.9+
cd backend
mvn clean compile
mvn spring-boot:run
# API: http://localhost:8080
# Swagger: http://localhost:8080/swagger-ui.html
```

### Frontend (React Native)
```bash
# Requisitos: Node.js 18+, npm
cd frontend
npm install
npx expo start
# Escanear QR con Expo Go
```

### Postman
1. Importar `docs/api/postman/SOJUS_API.postman_collection.json`
2. Ejecutar "Login (Admin)" → el token se guarda automáticamente
3. Probar los demás endpoints

## 📋 Credenciales Demo

| Rol | Usuario | Contraseña |
|-----|---------|------------|
| Administrador | `admin` | `admin123` |
| Operador | `operador` | `oper123` |
| Técnico | `tecnico` | `tec123` |
| Gestor Inventario | `gestor` | `gest123` |
| Auditor | `auditor` | `audit123` |

## 📜 Licencia

Proyecto del Poder Judicial — Provincia de Santa Fe. Uso interno.
