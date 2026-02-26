# Análisis de Brechas: Sistema Judicial vs. Estándar HelpDesk

Este documento analiza el cumplimiento de la especificación actual (`SPEC.md`) frente a las características estándar solicitadas para un HelpDesk moderno.

## Matriz de Cumplimiento

| Característica | Estado Actual en SPEC/POC | Veredicto |
| :--- | :--- | :--- |
| **Gestión de Tickets** | ✅ Completo. Ciclo de vida definido, asignación, priorización y bitácora. | **CUMPLE** |
| **Automatización** | ⚠️ Parcial. Existen reglas de prioridad (Juez=Alta) y alertas de contratos. Falta escalamiento automático por SLA. | **PARCIAL** |
| **Autoservicio** | ❌ No existe. Actualmente el sistema es para Operadores/Técnicos. No hay portal para que el usuario final (juez/empleado) cree sus tickets. | **FALTA** |
| **Multicanal** | ❌ No especificado. El ingreso es manual por Operador. No se menciona integración con email, chat o teléfono. | **FALTA** |
| **Informes y Métricas** | ✅ Presente. Dashboard con KPIs de casos abiertos, eficacia y contratos. | **CUMPLE** |

## Recomendaciones de Mejora para SPEC.md

1.  **Incorporar Módulo de Autoservicio (Portal de Usuario):**
    *   Permitir a empleados judiciales crear tickets simples (ej: "No anda impresora") desde una vista simplificada.
    *   Consultar estado de sus propios tickets.

2.  **Definir Estrategia Multicanal:**
    *   Especificar *Email-to-Ticket*: Creación automática al recibir correo en `soporte@justicia...`.
    *   Especificar integración básica con Chatbot (mencionado en PROMPTS.md pero no detallado en flujo).

3.  **Refinar Automatización:**
    *   Agregar reglas de escalamiento: "Si un ticket Alta Prioridad no se toma en 4hs -> Alerta a Supervisor".

## Próximos Pasos
Si el usuario aprueba, actualizar `SPEC.md` con estas nuevas capacidades para robustecer la solución.
