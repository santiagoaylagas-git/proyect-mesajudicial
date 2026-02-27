# Plan de Pruebas por Endpoint — SOJUS HelpDesk

**Proyecto:** Sistema de Gestión Judicial (SOJUS)  
**Fase:** 2 — Backend  
**Fecha:** 2026-02-27  
**Herramienta:** Postman

> **Requisito previo:** Iniciar la aplicación con `cd backend && .\mvnw.cmd spring-boot:run`  
> **Base URL:** `http://localhost:8080`

---

## Índice

1. [Autenticación](#1-autenticación-apiauth)
2. [Mesa de Ayuda — Tickets](#2-mesa-de-ayuda--tickets-apitickets)
3. [Inventario — Hardware](#3-inventario--hardware-apiinventoryhardware)
4. [Inventario — Software](#4-inventario--software-apiinventorysoftware)
5. [Contratos](#5-contratos-apicontracts)
6. [Estructura Territorial](#6-estructura-territorial-apilocations)
7. [Usuarios](#7-usuarios-apiusers)
8. [Auditoría](#8-auditoría-apiaudit)
9. [Dashboard](#9-dashboard-apidashboard)

---

## 1. Autenticación (`/api/auth`)

### EP-01 · `POST /api/auth/login` — Login ADMIN

| Campo | Valor |
|-------|-------|
| **Método** | POST |
| **URL** | `http://localhost:8080/api/auth/login` |
| **Headers** | `Content-Type: application/json` |
| **Body (raw JSON)** | `{ "username": "admin", "password": "admin123" }` |
| **Status esperado** | `200 OK` |
| **Verificar en response** | ✅ `token` no está vacío · ✅ `username` = "admin" · ✅ `role` = "ADMINISTRADOR" |
| **Acción post-test** | **Copiar el valor de `token`** y guardarlo como variable `token_admin` |

---

### EP-02 · `POST /api/auth/login` — Login OPERADOR

| Campo | Valor |
|-------|-------|
| **Método** | POST |
| **URL** | `http://localhost:8080/api/auth/login` |
| **Body (raw JSON)** | `{ "username": "operador", "password": "oper123" }` |
| **Status esperado** | `200 OK` |
| **Verificar en response** | ✅ `role` = "OPERADOR" |
| **Acción post-test** | Guardar `token` como `token_operador` |

---

### EP-03 · `POST /api/auth/login` — Login TECNICO

| Campo | Valor |
|-------|-------|
| **Método** | POST |
| **URL** | `http://localhost:8080/api/auth/login` |
| **Body (raw JSON)** | `{ "username": "tecnico", "password": "tec123" }` |
| **Status esperado** | `200 OK` |
| **Verificar en response** | ✅ `role` = "TECNICO" |
| **Acción post-test** | Guardar `token` como `token_tecnico` |

---

### EP-04 · `POST /api/auth/login` — Login con credenciales inválidas

| Campo | Valor |
|-------|-------|
| **Método** | POST |
| **URL** | `http://localhost:8080/api/auth/login` |
| **Body (raw JSON)** | `{ "username": "admin", "password": "passwordmal" }` |
| **Status esperado** | `409 Conflict` |
| **Verificar en response** | ✅ `message` contiene texto de error |

---

### EP-05 · `POST /api/auth/login` — Login con usuario inexistente

| Campo | Valor |
|-------|-------|
| **Método** | POST |
| **URL** | `http://localhost:8080/api/auth/login` |
| **Body (raw JSON)** | `{ "username": "noexiste", "password": "123456" }` |
| **Status esperado** | `409 Conflict` |

---

### EP-06 · `GET /api/auth/me` — Obtener usuario autenticado

| Campo | Valor |
|-------|-------|
| **Método** | GET |
| **URL** | `http://localhost:8080/api/auth/me` |
| **Headers** | `Authorization: Bearer {{token_admin}}` |
| **Status esperado** | `200 OK` |
| **Verificar en response** | ✅ `username` = "admin" · ✅ contiene `fullName`, `role`, `email` |

---

### EP-07 · `GET /api/auth/me` — Acceso sin token

| Campo | Valor |
|-------|-------|
| **Método** | GET |
| **URL** | `http://localhost:8080/api/auth/me` |
| **Headers** | Sin header `Authorization` |
| **Status esperado** | `403 Forbidden` |

---

## 2. Mesa de Ayuda — Tickets (`/api/tickets`)

> **Auth requerida:** Usar `token_admin`, `token_operador` o `token_tecnico` según el caso.

### EP-08 · `GET /api/tickets` — Listar todos (ADMIN)

| Campo | Valor |
|-------|-------|
| **Método** | GET |
| **URL** | `http://localhost:8080/api/tickets` |
| **Headers** | `Authorization: Bearer {{token_admin}}` |
| **Status esperado** | `200 OK` |
| **Verificar en response** | ✅ Response es un array · ✅ Cada elemento tiene `id`, `asunto`, `status`, `prioridad` |

---

### EP-09 · `GET /api/tickets` — Listar (TECNICO ve solo los suyos)

| Campo | Valor |
|-------|-------|
| **Método** | GET |
| **URL** | `http://localhost:8080/api/tickets` |
| **Headers** | `Authorization: Bearer {{token_tecnico}}` |
| **Status esperado** | `200 OK` |
| **Verificar en response** | ✅ Response es un array (puede estar vacío si no tiene tickets asignados) |

---

### EP-10 · `GET /api/tickets/my` — Mis tickets

| Campo | Valor |
|-------|-------|
| **Método** | GET |
| **URL** | `http://localhost:8080/api/tickets/my` |
| **Headers** | `Authorization: Bearer {{token_operador}}` |
| **Status esperado** | `200 OK` |
| **Verificar en response** | ✅ Response es un array |

---

### EP-11 · `GET /api/tickets/{id}` — Obtener ticket por ID

| Campo | Valor |
|-------|-------|
| **Método** | GET |
| **URL** | `http://localhost:8080/api/tickets/1` |
| **Headers** | `Authorization: Bearer {{token_admin}}` |
| **Status esperado** | `200 OK` |
| **Verificar en response** | ✅ `id` = 1 · ✅ contiene `asunto`, `status`, `prioridad`, `createdAt` |

---

### EP-12 · `GET /api/tickets/{id}` — Ticket inexistente

| Campo | Valor |
|-------|-------|
| **Método** | GET |
| **URL** | `http://localhost:8080/api/tickets/9999` |
| **Headers** | `Authorization: Bearer {{token_admin}}` |
| **Status esperado** | `404 Not Found` |

---

### EP-13 · `POST /api/tickets` — Crear ticket (OPERADOR)

| Campo | Valor |
|-------|-------|
| **Método** | POST |
| **URL** | `http://localhost:8080/api/tickets` |
| **Headers** | `Authorization: Bearer {{token_operador}}` · `Content-Type: application/json` |
| **Body (raw JSON)** | Ver abajo |
| **Status esperado** | `201 Created` |
| **Verificar en response** | ✅ `status` = "SOLICITADO" · ✅ `asunto` = el enviado · ✅ `id` es un número |

```json
{
  "asunto": "PC del puesto 3 no enciende",
  "descripcion": "La PC no enciende desde esta mañana. No hay luz en el monitor.",
  "prioridad": "MEDIA",
  "juzgadoId": 1,
  "canal": "WEB"
}
```

> **Anotar:** Guardar el `id` del ticket creado para usarlo en pruebas siguientes.

---

### EP-14 · `POST /api/tickets` — Prioridad ALTA automática (palabra clave "juez")

| Campo | Valor |
|-------|-------|
| **Método** | POST |
| **URL** | `http://localhost:8080/api/tickets` |
| **Headers** | `Authorization: Bearer {{token_operador}}` · `Content-Type: application/json` |
| **Body (raw JSON)** | `{ "asunto": "PC del Juez no funciona", "descripcion": "Urgente", "prioridad": "BAJA", "juzgadoId": 1 }` |
| **Status esperado** | `201 Created` |
| **Verificar en response** | ✅ **`prioridad` = "ALTA"** (ignora el valor "BAJA" enviado porque el asunto contiene "juez") |

---

### EP-15 · `POST /api/tickets` — TECNICO no puede crear tickets

| Campo | Valor |
|-------|-------|
| **Método** | POST |
| **URL** | `http://localhost:8080/api/tickets` |
| **Headers** | `Authorization: Bearer {{token_tecnico}}` · `Content-Type: application/json` |
| **Body (raw JSON)** | `{ "asunto": "Test sin permiso", "descripcion": "No debería funcionar", "juzgadoId": 1 }` |
| **Status esperado** | `403 Forbidden` |

---

### EP-16 · `PATCH /api/tickets/{id}/status` — Cambiar SOLICITADO → ASIGNADO

| Campo | Valor |
|-------|-------|
| **Método** | PATCH |
| **URL** | `http://localhost:8080/api/tickets/2/status` |
| **Headers** | `Authorization: Bearer {{token_admin}}` · `Content-Type: application/json` |
| **Body (raw JSON)** | `{ "status": "ASIGNADO", "tecnicoId": 3, "comentario": "Asignado al técnico" }` |
| **Status esperado** | `200 OK` |
| **Verificar en response** | ✅ `status` = "ASIGNADO" |

---

### EP-17 · `PATCH /api/tickets/{id}/status` — Cambiar ASIGNADO → EN_CURSO

| Campo | Valor |
|-------|-------|
| **Método** | PATCH |
| **URL** | `http://localhost:8080/api/tickets/2/status` (mismo ticket del EP-16) |
| **Headers** | `Authorization: Bearer {{token_tecnico}}` · `Content-Type: application/json` |
| **Body (raw JSON)** | `{ "status": "EN_CURSO", "comentario": "Comenzando diagnóstico" }` |
| **Status esperado** | `200 OK` |
| **Verificar en response** | ✅ `status` = "EN_CURSO" |

---

### EP-18 · `PATCH /api/tickets/{id}/status` — Cambiar EN_CURSO → CERRADO

| Campo | Valor |
|-------|-------|
| **Método** | PATCH |
| **URL** | `http://localhost:8080/api/tickets/2/status` |
| **Headers** | `Authorization: Bearer {{token_tecnico}}` · `Content-Type: application/json` |
| **Body (raw JSON)** | `{ "status": "CERRADO", "comentario": "Problema resuelto" }` |
| **Status esperado** | `200 OK` |
| **Verificar en response** | ✅ `status` = "CERRADO" · ✅ `closedAt` no es null |

---

### EP-19 · `PATCH /api/tickets/{id}/status` — Transición inválida (SOLICITADO → EN_CURSO)

| Campo | Valor |
|-------|-------|
| **Método** | PATCH |
| **URL** | `http://localhost:8080/api/tickets/1/status` (un ticket en SOLICITADO) |
| **Headers** | `Authorization: Bearer {{token_admin}}` · `Content-Type: application/json` |
| **Body (raw JSON)** | `{ "status": "EN_CURSO", "comentario": "Saltando pasos" }` |
| **Status esperado** | `409 Conflict` |
| **Verificar en response** | ✅ `message` contiene "Transición de estado inválida" |

---

### EP-20 · `PATCH /api/tickets/{id}/status` — OPERADOR no puede cambiar estado

| Campo | Valor |
|-------|-------|
| **Método** | PATCH |
| **URL** | `http://localhost:8080/api/tickets/1/status` |
| **Headers** | `Authorization: Bearer {{token_operador}}` · `Content-Type: application/json` |
| **Body (raw JSON)** | `{ "status": "ASIGNADO" }` |
| **Status esperado** | `403 Forbidden` |

---

### EP-21 · `DELETE /api/tickets/{id}` — Soft delete (ADMIN)

| Campo | Valor |
|-------|-------|
| **Método** | DELETE |
| **URL** | `http://localhost:8080/api/tickets/4` |
| **Headers** | `Authorization: Bearer {{token_admin}}` |
| **Status esperado** | `204 No Content` |
| **Verificación adicional** | Hacer GET al mismo ID → debería dar `404 Not Found` |

---

## 3. Inventario — Hardware (`/api/inventory/hardware`)

### EP-22 · `GET /api/inventory/hardware` — Listar hardware

| Campo | Valor |
|-------|-------|
| **Método** | GET |
| **URL** | `http://localhost:8080/api/inventory/hardware` |
| **Headers** | `Authorization: Bearer {{token_admin}}` |
| **Status esperado** | `200 OK` |
| **Verificar en response** | ✅ Array con objetos que tienen `inventarioPatrimonial`, `clase`, `marca` |

---

### EP-23 · `GET /api/inventory/hardware/{id}` — Obtener por ID

| Campo | Valor |
|-------|-------|
| **Método** | GET |
| **URL** | `http://localhost:8080/api/inventory/hardware/1` |
| **Headers** | `Authorization: Bearer {{token_admin}}` |
| **Status esperado** | `200 OK` |
| **Verificar en response** | ✅ `inventarioPatrimonial` es un string · ✅ `estado` es uno de: ACTIVO, EN_REPARACION, DE_BAJA, EN_DEPOSITO |

---

### EP-24 · `POST /api/inventory/hardware` — Crear hardware

| Campo | Valor |
|-------|-------|
| **Método** | POST |
| **URL** | `http://localhost:8080/api/inventory/hardware` |
| **Headers** | `Authorization: Bearer {{token_admin}}` · `Content-Type: application/json` |
| **Body (raw JSON)** | Ver abajo |
| **Status esperado** | `201 Created` |
| **Verificar en response** | ✅ `inventarioPatrimonial` = "INV-TEST-0001" |

```json
{
  "inventarioPatrimonial": "INV-TEST-0001",
  "numeroSerie": "SN-TEST-001",
  "clase": "PC",
  "tipo": "Notebook",
  "marca": "Lenovo",
  "modelo": "ThinkPad T14"
}
```

---

### EP-25 · `POST /api/inventory/hardware` — Inventario patrimonial duplicado

| Campo | Valor |
|-------|-------|
| **Método** | POST |
| **URL** | `http://localhost:8080/api/inventory/hardware` |
| **Headers** | `Authorization: Bearer {{token_admin}}` · `Content-Type: application/json` |
| **Body (raw JSON)** | `{ "inventarioPatrimonial": "INV-TEST-0001", "clase": "Impresora" }` |
| **Status esperado** | `409 Conflict` |
| **Verificar en response** | ✅ `message` contiene "Inventario Patrimonial" |

---

### EP-26 · `PUT /api/inventory/hardware/{id}` — Actualizar

| Campo | Valor |
|-------|-------|
| **Método** | PUT |
| **URL** | `http://localhost:8080/api/inventory/hardware/1` |
| **Headers** | `Authorization: Bearer {{token_admin}}` · `Content-Type: application/json` |
| **Body (raw JSON)** | `{ "clase": "PC", "tipo": "Desktop", "marca": "Dell", "modelo": "OptiPlex 7090 ACTUALIZADO", "estado": "ACTIVO", "ubicacionFisica": "Puesto 1 — Actualizado" }` |
| **Status esperado** | `200 OK` |
| **Verificar en response** | ✅ `modelo` contiene "ACTUALIZADO" |

---

### EP-27 · `DELETE /api/inventory/hardware/{id}` — Soft delete

| Campo | Valor |
|-------|-------|
| **Método** | DELETE |
| **URL** | `http://localhost:8080/api/inventory/hardware/4` |
| **Headers** | `Authorization: Bearer {{token_admin}}` |
| **Status esperado** | `204 No Content` |

---

## 4. Inventario — Software (`/api/inventory/software`)

### EP-28 · `GET /api/inventory/software` — Listar software

| Campo | Valor |
|-------|-------|
| **Método** | GET |
| **URL** | `http://localhost:8080/api/inventory/software` |
| **Headers** | `Authorization: Bearer {{token_admin}}` |
| **Status esperado** | `200 OK` |
| **Verificar en response** | ✅ Array con objetos que tienen `nombre`, `version`, `fabricante` |

---

### EP-29 · `GET /api/inventory/software/{id}` — Obtener por ID

| Campo | Valor |
|-------|-------|
| **Método** | GET |
| **URL** | `http://localhost:8080/api/inventory/software/1` |
| **Headers** | `Authorization: Bearer {{token_admin}}` |
| **Status esperado** | `200 OK` |

---

### EP-30 · `POST /api/inventory/software` — Crear software

| Campo | Valor |
|-------|-------|
| **Método** | POST |
| **URL** | `http://localhost:8080/api/inventory/software` |
| **Headers** | `Authorization: Bearer {{token_admin}}` · `Content-Type: application/json` |
| **Body (raw JSON)** | `{ "nombre": "Antivirus Kaspersky", "version": "2026", "fabricante": "Kaspersky", "tipoLicencia": "Anual", "cantidadLicencias": 100 }` |
| **Status esperado** | `201 Created` |
| **Verificar en response** | ✅ `nombre` = "Antivirus Kaspersky" |

---

### EP-31 · `PUT /api/inventory/software/{id}` — Actualizar

| Campo | Valor |
|-------|-------|
| **Método** | PUT |
| **URL** | `http://localhost:8080/api/inventory/software/1` |
| **Headers** | `Authorization: Bearer {{token_admin}}` · `Content-Type: application/json` |
| **Body (raw JSON)** | `{ "nombre": "Microsoft Office 365 — Renovado", "version": "2025", "fabricante": "Microsoft", "tipoLicencia": "Suscripción Anual", "cantidadLicencias": 600 }` |
| **Status esperado** | `200 OK` |
| **Verificar en response** | ✅ `cantidadLicencias` = 600 |

---

### EP-32 · `DELETE /api/inventory/software/{id}` — Soft delete

| Campo | Valor |
|-------|-------|
| **Método** | DELETE |
| **URL** | `http://localhost:8080/api/inventory/software/3` |
| **Headers** | `Authorization: Bearer {{token_admin}}` |
| **Status esperado** | `204 No Content` |

---

## 5. Contratos (`/api/contracts`)

### EP-33 · `GET /api/contracts` — Listar contratos activos

| Campo | Valor |
|-------|-------|
| **Método** | GET |
| **URL** | `http://localhost:8080/api/contracts` |
| **Headers** | `Authorization: Bearer {{token_admin}}` |
| **Status esperado** | `200 OK` |
| **Verificar en response** | ✅ Array de contratos · ✅ Cada uno tiene `nombre`, `proveedor`, `active` = true |

---

### EP-34 · `GET /api/contracts/{id}` — Obtener por ID

| Campo | Valor |
|-------|-------|
| **Método** | GET |
| **URL** | `http://localhost:8080/api/contracts/1` |
| **Headers** | `Authorization: Bearer {{token_admin}}` |
| **Status esperado** | `200 OK` |
| **Verificar en response** | ✅ `proveedor` es un string · ✅ `fechaInicio` y `fechaFin` están presentes |

---

### EP-35 · `POST /api/contracts` — Crear contrato

| Campo | Valor |
|-------|-------|
| **Método** | POST |
| **URL** | `http://localhost:8080/api/contracts` |
| **Headers** | `Authorization: Bearer {{token_admin}}` · `Content-Type: application/json` |
| **Body (raw JSON)** | Ver abajo |
| **Status esperado** | `201 Created` |
| **Verificar en response** | ✅ `nombre` contiene "Postman" |

```json
{
  "nombre": "Contrato Test Postman",
  "proveedor": "Proveedor Test SRL",
  "numeroContrato": "CNT-TEST-001",
  "fechaInicio": "2026-01-01",
  "fechaFin": "2027-12-31",
  "coberturaHw": "PCs de prueba",
  "slaDescripcion": "Respuesta 2hs hábiles"
}
```

---

### EP-36 · `PUT /api/contracts/{id}` — Actualizar contrato

| Campo | Valor |
|-------|-------|
| **Método** | PUT |
| **URL** | `http://localhost:8080/api/contracts/1` |
| **Headers** | `Authorization: Bearer {{token_admin}}` · `Content-Type: application/json` |
| **Body (raw JSON)** | `{ "nombre": "Soporte HW Dell — Renovado 2026", "proveedor": "Dell Argentina S.A.", "fechaFin": "2028-12-31", "slaDescripcion": "Respuesta 2hs. Resolución 12hs." }` |
| **Status esperado** | `200 OK` |

---

### EP-37 · `DELETE /api/contracts/{id}` — Desactivar contrato

| Campo | Valor |
|-------|-------|
| **Método** | DELETE |
| **URL** | `http://localhost:8080/api/contracts/2` |
| **Headers** | `Authorization: Bearer {{token_admin}}` |
| **Status esperado** | `204 No Content` |
| **Verificación adicional** | Hacer GET al mismo ID → debería dar `404 Not Found` |

---

### EP-38 · `GET /api/contracts/expiring` — Próximos a vencer

| Campo | Valor |
|-------|-------|
| **Método** | GET |
| **URL** | `http://localhost:8080/api/contracts/expiring?days=365` |
| **Headers** | `Authorization: Bearer {{token_admin}}` |
| **Status esperado** | `200 OK` |
| **Verificar en response** | ✅ Array (puede contener contratos con `fechaFin` cercana) |

---

## 6. Estructura Territorial (`/api/locations`)

### EP-39 · `GET /api/locations/circunscripciones` — Listar con jerarquía

| Campo | Valor |
|-------|-------|
| **Método** | GET |
| **URL** | `http://localhost:8080/api/locations/circunscripciones` |
| **Headers** | `Authorization: Bearer {{token_admin}}` |
| **Status esperado** | `200 OK` |
| **Verificar en response** | ✅ Array con objetos que incluyen `nombre` y sub-objetos `distritos` |

---

### EP-40 · `GET /api/locations/juzgados` — Listar juzgados

| Campo | Valor |
|-------|-------|
| **Método** | GET |
| **URL** | `http://localhost:8080/api/locations/juzgados` |
| **Headers** | `Authorization: Bearer {{token_admin}}` |
| **Status esperado** | `200 OK` |
| **Verificar en response** | ✅ Array con objetos que tienen `id`, `nombre`, `fuero` |

---

### EP-41 · `GET /api/locations/edificios/{id}/juzgados` — Juzgados de un edificio

| Campo | Valor |
|-------|-------|
| **Método** | GET |
| **URL** | `http://localhost:8080/api/locations/edificios/1/juzgados` |
| **Headers** | `Authorization: Bearer {{token_admin}}` |
| **Status esperado** | `200 OK` |

---

## 7. Usuarios (`/api/users`)

> **Importante:** Solo el rol ADMINISTRADOR tiene acceso a estos endpoints.

### EP-42 · `GET /api/users` — Listar usuarios (ADMIN)

| Campo | Valor |
|-------|-------|
| **Método** | GET |
| **URL** | `http://localhost:8080/api/users` |
| **Headers** | `Authorization: Bearer {{token_admin}}` |
| **Status esperado** | `200 OK` |
| **Verificar en response** | ✅ Array de usuarios · ✅ Cada uno tiene `username`, `role`, `active` |

---

### EP-43 · `GET /api/users` — TECNICO no tiene acceso

| Campo | Valor |
|-------|-------|
| **Método** | GET |
| **URL** | `http://localhost:8080/api/users` |
| **Headers** | `Authorization: Bearer {{token_tecnico}}` |
| **Status esperado** | `403 Forbidden` |

---

### EP-44 · `GET /api/users/{id}` — Obtener por ID

| Campo | Valor |
|-------|-------|
| **Método** | GET |
| **URL** | `http://localhost:8080/api/users/1` |
| **Headers** | `Authorization: Bearer {{token_admin}}` |
| **Status esperado** | `200 OK` |
| **Verificar en response** | ✅ `username` es un string · ✅ `role` es ADMINISTRADOR, TECNICO u OPERADOR |

---

### EP-45 · `GET /api/users/role/{role}` — Filtrar por rol

| Campo | Valor |
|-------|-------|
| **Método** | GET |
| **URL** | `http://localhost:8080/api/users/role/TECNICO` |
| **Headers** | `Authorization: Bearer {{token_admin}}` |
| **Status esperado** | `200 OK` |
| **Verificar en response** | ✅ Todos los usuarios del array tienen `role` = "TECNICO" |

---

### EP-46 · `POST /api/users` — Crear usuario

| Campo | Valor |
|-------|-------|
| **Método** | POST |
| **URL** | `http://localhost:8080/api/users` |
| **Headers** | `Authorization: Bearer {{token_admin}}` · `Content-Type: application/json` |
| **Body (raw JSON)** | `{ "username": "nuevo_user", "password": "pass123", "fullName": "Usuario Nuevo", "email": "nuevo@test.com", "role": "OPERADOR" }` |
| **Status esperado** | `201 Created` |
| **Verificar en response** | ✅ `username` = "nuevo_user" · ✅ No contiene campo `password` en el response |

---

### EP-47 · `POST /api/users` — Username duplicado

| Campo | Valor |
|-------|-------|
| **Método** | POST |
| **URL** | `http://localhost:8080/api/users` |
| **Headers** | `Authorization: Bearer {{token_admin}}` · `Content-Type: application/json` |
| **Body (raw JSON)** | `{ "username": "admin", "password": "pass", "fullName": "Duplicado", "role": "OPERADOR" }` |
| **Status esperado** | `409 Conflict` |
| **Verificar en response** | ✅ `message` contiene "ya existe" |

---

### EP-48 · `PUT /api/users/{id}` — Actualizar usuario

| Campo | Valor |
|-------|-------|
| **Método** | PUT |
| **URL** | `http://localhost:8080/api/users/1` |
| **Headers** | `Authorization: Bearer {{token_admin}}` · `Content-Type: application/json` |
| **Body (raw JSON)** | `{ "fullName": "Admin General — Actualizado", "email": "admin_updated@poderjudicial.gov.ar", "role": "ADMINISTRADOR", "active": true }` |
| **Status esperado** | `200 OK` |

---

### EP-49 · `DELETE /api/users/{id}` — Soft delete

| Campo | Valor |
|-------|-------|
| **Método** | DELETE |
| **URL** | `http://localhost:8080/api/users/2` |
| **Headers** | `Authorization: Bearer {{token_admin}}` |
| **Status esperado** | `204 No Content` |
| **Verificación adicional** | El usuario ya no aparecerá en `GET /api/users` |

---

## 8. Auditoría (`/api/audit`)

> **Importante:** Solo ADMINISTRADOR puede acceder.

### EP-50 · `GET /api/audit` — Últimos registros

| Campo | Valor |
|-------|-------|
| **Método** | GET |
| **URL** | `http://localhost:8080/api/audit` |
| **Headers** | `Authorization: Bearer {{token_admin}}` |
| **Status esperado** | `200 OK` |
| **Verificar en response** | ✅ Array de registros · ✅ Cada uno tiene `action`, `entityName`, `timestamp` |

---

### EP-51 · `GET /api/audit/entity/{name}/{id}` — Historial de una entidad

| Campo | Valor |
|-------|-------|
| **Método** | GET |
| **URL** | `http://localhost:8080/api/audit/entity/Ticket/1` |
| **Headers** | `Authorization: Bearer {{token_admin}}` |
| **Status esperado** | `200 OK` |
| **Verificar en response** | ✅ Array con registros de auditoría del Ticket 1 |

---

### EP-52 · `GET /api/audit` — TECNICO no tiene acceso

| Campo | Valor |
|-------|-------|
| **Método** | GET |
| **URL** | `http://localhost:8080/api/audit` |
| **Headers** | `Authorization: Bearer {{token_tecnico}}` |
| **Status esperado** | `403 Forbidden` |

---

## 9. Dashboard (`/api/dashboard`)

### EP-53 · `GET /api/dashboard/stats` — Estadísticas generales

| Campo | Valor |
|-------|-------|
| **Método** | GET |
| **URL** | `http://localhost:8080/api/dashboard/stats` |
| **Headers** | `Authorization: Bearer {{token_admin}}` |
| **Status esperado** | `200 OK` |
| **Verificar en response** | ✅ Contiene `ticketsAbiertos` · ✅ `ticketsCerrados` · ✅ `hwTotal` · ✅ Todos son números |

---

## Orden de ejecución recomendado

> ⚠️ **Ejecutar en este orden** para que los datos de pruebas anteriores estén disponibles.

| Paso | Endpoints | Descripción |
|------|-----------|-------------|
| 1 | EP-01, EP-02, EP-03 | **Obtener los 3 tokens** (ADMIN, OPERADOR, TECNICO) |
| 2 | EP-04, EP-05, EP-07 | Probar errores de login y acceso |
| 3 | EP-06 | Verificar token con `/me` |
| 4 | EP-08 a EP-12 | Consultas de tickets (GET) |
| 5 | EP-13, EP-14 | Crear tickets (happy path + regla de negocio) |
| 6 | EP-15 | Crear ticket sin permiso (TECNICO) |
| 7 | EP-16 → EP-17 → EP-18 | **Flujo completo:** SOLICITADO → ASIGNADO → EN_CURSO → CERRADO |
| 8 | EP-19, EP-20 | Transición inválida + sin permiso |
| 9 | EP-21 | Soft delete de ticket |
| 10 | EP-22 a EP-27 | CRUD Hardware |
| 11 | EP-28 a EP-32 | CRUD Software |
| 12 | EP-33 a EP-38 | CRUD Contratos |
| 13 | EP-39 a EP-41 | Estructura Territorial (solo lectura) |
| 14 | EP-42 a EP-49 | CRUD Usuarios |
| 15 | EP-50 a EP-52 | Auditoría |
| 16 | EP-53 | Dashboard |

**Total de pruebas: 53 endpoints testeados**
