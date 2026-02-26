#!/bin/bash
# ============================================================
# SOJUS — Restauración de PostgreSQL
# Uso: ./restore.sh <ruta_al_backup.dump>
# ============================================================

set -euo pipefail

# --- Configuración ---
DB_NAME="sojus_db"
DB_USER="sojus_user"
DB_HOST="localhost"
DB_PORT="5432"
LOG_FILE="/var/log/sojus_backup.log"

# --- Validar argumentos ---
if [ $# -lt 1 ]; then
    echo "Uso: $0 <ruta_al_archivo_backup.dump>"
    echo "Ejemplo: $0 /var/backups/sojus/sojus_db_20260225_020000.dump"
    exit 1
fi

BACKUP_FILE="$1"

# --- Funciones ---
log() {
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] $1" | tee -a "${LOG_FILE}"
}

# --- Verificar que el archivo existe ---
if [ ! -f "${BACKUP_FILE}" ]; then
    log "ERROR: Archivo no encontrado: ${BACKUP_FILE}"
    exit 1
fi

# --- Verificar integridad del backup ---
log "=== INICIO RESTORE ==="
log "Archivo origen: ${BACKUP_FILE}"
log "Base de datos destino: ${DB_NAME}"

log "Verificando integridad del backup..."
if ! pg_restore --list "${BACKUP_FILE}" > /dev/null 2>&1; then
    log "ERROR: El archivo de backup parece estar corrupto"
    exit 1
fi
log "Integridad verificada: OK"

# --- Confirmación ---
echo ""
echo "=========================================="
echo "  ADVERTENCIA: RESTAURACIÓN DE BASE DE DATOS"
echo "=========================================="
echo "  Base de datos: ${DB_NAME}"
echo "  Archivo: ${BACKUP_FILE}"
echo "  Host: ${DB_HOST}:${DB_PORT}"
echo ""
echo "  ESTO REEMPLAZARÁ TODOS LOS DATOS ACTUALES"
echo "=========================================="
echo ""
read -p "¿Desea continuar? (s/N): " CONFIRM

if [ "${CONFIRM}" != "s" ] && [ "${CONFIRM}" != "S" ]; then
    log "Restore cancelado por el usuario"
    exit 0
fi

# --- Crear backup previo a la restauración ---
PRE_RESTORE_BACKUP="/var/backups/sojus/pre_restore_$(date +%Y%m%d_%H%M%S).dump"
log "Creando backup de seguridad previo: ${PRE_RESTORE_BACKUP}"
pg_dump -h "${DB_HOST}" -p "${DB_PORT}" -U "${DB_USER}" -d "${DB_NAME}" -Fc -f "${PRE_RESTORE_BACKUP}" 2>/dev/null || true

# --- Ejecutar restauración ---
log "Ejecutando pg_restore..."
if pg_restore \
    -h "${DB_HOST}" \
    -p "${DB_PORT}" \
    -U "${DB_USER}" \
    -d "${DB_NAME}" \
    --clean \
    --if-exists \
    --no-owner \
    --no-privileges \
    "${BACKUP_FILE}"; then

    log "Restauración completada exitosamente"
else
    log "ADVERTENCIA: pg_restore terminó con errores (puede ser normal si algunas tablas no existían)"
fi

# --- Verificación post-restore ---
log "Verificación post-restore:"
psql -h "${DB_HOST}" -p "${DB_PORT}" -U "${DB_USER}" -d "${DB_NAME}" -c "
    SELECT 'circunscripciones' AS tabla, count(*) AS registros FROM circunscripciones
    UNION ALL SELECT 'distritos', count(*) FROM distritos
    UNION ALL SELECT 'edificios', count(*) FROM edificios
    UNION ALL SELECT 'juzgados', count(*) FROM juzgados
    UNION ALL SELECT 'users', count(*) FROM users
    UNION ALL SELECT 'hardware', count(*) FROM hardware
    UNION ALL SELECT 'software', count(*) FROM software
    UNION ALL SELECT 'tickets', count(*) FROM tickets
    UNION ALL SELECT 'contracts', count(*) FROM contracts
    UNION ALL SELECT 'audit_log', count(*) FROM audit_log
    ORDER BY tabla;
" 2>&1 | tee -a "${LOG_FILE}"

log "=== FIN RESTORE ==="
