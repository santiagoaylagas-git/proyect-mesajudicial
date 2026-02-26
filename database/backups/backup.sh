#!/bin/bash
# ============================================================
# SOJUS — Backup Automatizado de PostgreSQL
# Uso: ./backup.sh
# ============================================================

set -euo pipefail

# --- Configuración ---
DB_NAME="sojus_db"
DB_USER="sojus_user"
DB_HOST="localhost"
DB_PORT="5432"
BACKUP_DIR="/var/backups/sojus"
MONTHLY_DIR="${BACKUP_DIR}/monthly"
LOG_FILE="/var/log/sojus_backup.log"
RETENTION_DAYS=30
MONTHLY_RETENTION_DAYS=365
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="${BACKUP_DIR}/sojus_db_${TIMESTAMP}.dump"

# --- Funciones ---
log() {
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] $1" | tee -a "${LOG_FILE}"
}

# --- Crear directorios ---
mkdir -p "${BACKUP_DIR}" "${MONTHLY_DIR}"

log "=== INICIO BACKUP ==="
log "Base de datos: ${DB_NAME}"
log "Archivo destino: ${BACKUP_FILE}"

# --- Ejecutar pg_dump ---
if pg_dump \
    -h "${DB_HOST}" \
    -p "${DB_PORT}" \
    -U "${DB_USER}" \
    -d "${DB_NAME}" \
    -Fc \
    -Z 9 \
    -f "${BACKUP_FILE}"; then

    FILESIZE=$(du -h "${BACKUP_FILE}" | cut -f1)
    log "Backup completado exitosamente. Tamaño: ${FILESIZE}"
else
    log "ERROR: Falló el backup de ${DB_NAME}"
    exit 1
fi

# --- Copia mensual (día 1 de cada mes) ---
DAY_OF_MONTH=$(date +%d)
if [ "${DAY_OF_MONTH}" = "01" ]; then
    cp "${BACKUP_FILE}" "${MONTHLY_DIR}/sojus_db_monthly_${TIMESTAMP}.dump"
    log "Copia mensual creada en ${MONTHLY_DIR}"
fi

# --- Limpieza de backups antiguos ---
DELETED_COUNT=$(find "${BACKUP_DIR}" -maxdepth 1 -name "sojus_db_*.dump" -mtime +${RETENTION_DAYS} -delete -print | wc -l)
log "Backups diarios eliminados (>${RETENTION_DAYS} días): ${DELETED_COUNT}"

DELETED_MONTHLY=$(find "${MONTHLY_DIR}" -name "sojus_db_monthly_*.dump" -mtime +${MONTHLY_RETENTION_DAYS} -delete -print | wc -l)
log "Backups mensuales eliminados (>${MONTHLY_RETENTION_DAYS} días): ${DELETED_MONTHLY}"

# --- Verificar integridad ---
if pg_restore --list "${BACKUP_FILE}" > /dev/null 2>&1; then
    log "Verificación de integridad: OK"
else
    log "ADVERTENCIA: El backup puede estar corrupto"
fi

log "=== FIN BACKUP ==="
