# Agentes IA — SOJUS HelpDesk Judicial

## Descripción
Módulos de inteligencia artificial para asistencia, clasificación automática y análisis predictivo del sistema HelpDesk.

## Estructura

```
agents/
├── chatbot/                    # Asistente virtual (Fase 2)
│   ├── intents/                # Definición de intenciones
│   ├── responses/              # Templates de respuestas
│   ├── classifier.py           # Clasificador de intents
│   └── bot.py                  # Lógica principal del chatbot
├── ticket-classifier/          # Clasificación automática de tickets
│   ├── model/                  # Modelo entrenado
│   ├── training/               # Datos de entrenamiento
│   ├── classifier.py           # Clasificador de prioridad/categoría
│   └── escalation.py           # Lógica de escalamiento automático
├── email-parser/               # Email-to-Ticket (Fase 4)
│   ├── parser.py               # Parseo de emails entrantes
│   ├── templates/              # Templates de respuesta automática
│   └── config.yaml             # Configuración IMAP/SMTP
├── analytics/                  # Análisis predictivo
│   ├── reports.py              # Generación de reportes
│   ├── predictions.py          # Predicción de carga de trabajo
│   └── dashboards.py           # Métricas en tiempo real
├── knowledge-base/             # Base de conocimiento / FAQ
│   ├── articles/               # Artículos de soluciones
│   ├── embeddings/             # Vectores para búsqueda semántica
│   └── search.py               # Motor de búsqueda
├── requirements.txt
└── Dockerfile
```

## Stack
- **Lenguaje:** Python 3.11+
- **NLP:** OpenAI API / LangChain / Ollama (local)
- **ML:** scikit-learn / transformers
- **API:** FastAPI
- **Embeddings:** sentence-transformers / pgvector

## Agentes Planificados

| Agente | Fase | Función |
|--------|------|---------|
| Chatbot | 2 | Asistente virtual en intranet |
| Ticket Classifier | 2 | Clasificación automática de prioridad |
| Email Parser | 4 | Convertir emails a tickets |
| Knowledge Base | 3 | FAQ y búsqueda semántica |
| Analytics | 3 | Reportes predictivos |
