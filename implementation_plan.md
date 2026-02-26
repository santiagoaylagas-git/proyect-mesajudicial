# Plan de Implementación - Asistente AI en Mesa de Ayuda

El objetivo es implementar la vista funcional de "Mesa de Ayuda" en el POC y agregar una función para crear tickets interactuando con un Asistente Virtual.

## Revisión del Usuario Requerida
> [!NOTE]
> Se reemplazará el placeholder de "Mesa de Ayuda" por una tabla real y se agregará un modal de chat simulado.

## Cambios Propuestos

### Código Fuente

#### [MODIFICAR] [poc-helpdesk-judicial.html](file:///c:/Users/satia/.gemini/antigravity/brain/a9dfda03-2d4c-4ea8-9fc2-34e73051fcbb/poc-helpdesk-judicial.html)
- **Implementar `renderTickets()`**:
    - Mostrar tabla completa de tickets desde `MOCK_DB`.
    - Cabecera con filtros y el botón **"✨ Solicitar con Asistente"**.
- **Agregar Modal de Asistente**:
    - HTML/CSS para un modal flotante tipo chat.
    - Lógica simple para simular una conversación (Usuario escribe -> Asistente responde "Ticket creado...").
- **Actualizar `navTo('tickets')`**:
    - Llamar a la nueva función `renderTickets()`.

## Plan de Verificación

### Verificación Manual
1. Abrir POC.
2. Ir a "Mesa de Ayuda".
3. Clic en "Solicitar con Asistente".
4. Verificar que se abra el cuadro de diálogo.
