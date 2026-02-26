# Estrategia de Backup y Recovery — SOJUS HelpDesk Judicial

## 1. Resumen

| Parámetro              | Valor                                                  |
|------------------------|--------------------------------------------------------|
| **Motor**              | PostgreSQL 16                                          |
| **Base de datos**      | `sojus_db`                                             |
| **Herramientas**       | `pg_dump` / `pg_restore`                               |
| **Formato de backup**  | Custom (`-Fc`) — comprimido y restaurable selectivamente|
| **Frecuencia**         | Diario completo a las 02:00 AM                         |
| **Retención**          | 30 días (backups diarios), 12 meses (backups mensuales)|
| **Ubicación backups**  | `/var/backups/sojus/` + copia remota recomendada       |

---

## 2. Tipos de Backup

### 2.1 Backup Lógico Completo (Diario)
Utiliza `pg_dump` en formato custom para exportar toda la base de datos.

```bash
# Ejecutar manualmente:
./backup.sh
```

### 2.2 Backup Mensual de Retención Larga
El primer backup de cada mes se copia automáticamente a un directorio de archivo mensual con retención de 12 meses.

### 2.3 WAL Archiving (Opcional — Producción Avanzada)
Para entornos con RPO < 1 hora, habilitar WAL archiving en `postgresql.conf`:
```
wal_level = replica
archive_mode = on
archive_command = 'cp %p /var/backups/sojus/wal/%f'
```

---

## 3. Procedimiento de Backup

### Comando manual:
```bash
pg_dump -h localhost -U sojus_user -d sojus_db -Fc -f /var/backups/sojus/sojus_db_$(date +%Y%m%d_%H%M%S).dump
```

### Script automatizado:
```bash
./backup.sh
```
El script `backup.sh` realiza:
1. Crea el directorio de backups si no existe
2. Ejecuta `pg_dump` en formato custom comprimido
3. Limpia backups anteriores a 30 días
4. Registra la operación en un log

### Automatización con Cron:
```cron
# Backup diario a las 02:00 AM
0 2 * * * /ruta/al/proyecto/database/backups/backup.sh >> /var/log/sojus_backup.log 2>&1
```

---

## 4. Procedimiento de Restore

### 4.1 Restauración completa:
```bash
./restore.sh /var/backups/sojus/sojus_db_20260225_020000.dump
```

### 4.2 Restauración selectiva (una tabla específica):
```bash
pg_restore -h localhost -U sojus_user -d sojus_db -t tickets --clean /ruta/al/backup.dump
```

### 4.3 Listar contenido del backup (verificación):
```bash
pg_restore --list /ruta/al/backup.dump
```

---

## 5. Disaster Recovery — Paso a Paso

### Escenario: Pérdida total de la base de datos

| Paso | Acción | Comando |
|------|--------|---------|
| 1 | Detener la aplicación | `systemctl stop sojus-backend` |
| 2 | Crear base vacía | `createdb -U postgres sojus_db` |
| 3 | Restaurar último backup | `./restore.sh /var/backups/sojus/ULTIMO_BACKUP.dump` |
| 4 | Verificar integridad | `psql -U sojus_user -d sojus_db -c "SELECT count(*) FROM users;"` |
| 5 | Validar aplicación | `curl http://localhost:8080/api/dashboard/stats` |
| 6 | Reiniciar servicio | `systemctl start sojus-backend` |

### Escenario: Corrupción parcial (una tabla)

1. Identificar la tabla afectada
2. Restaurar solo esa tabla:
   ```bash
   pg_restore -h localhost -U sojus_user -d sojus_db -t nombre_tabla --clean /ruta/backup.dump
   ```
3. Verificar con `SELECT count(*) FROM nombre_tabla;`

---

## 6. Verificación de Integridad

### Test de backup (ejecutar semanalmente):
```bash
# 1. Crear base de prueba
createdb -U postgres sojus_db_test

# 2. Restaurar en base de prueba
pg_restore -h localhost -U postgres -d sojus_db_test /var/backups/sojus/ultimo_backup.dump

# 3. Verificar conteos
psql -U postgres -d sojus_db_test -c "
    SELECT 'users' AS tabla, count(*) FROM users
    UNION ALL
    SELECT 'tickets', count(*) FROM tickets
    UNION ALL
    SELECT 'hardware', count(*) FROM hardware
    UNION ALL
    SELECT 'contracts', count(*) FROM contracts;
"

# 4. Eliminar base de prueba
dropdb -U postgres sojus_db_test
```

---

## 7. Política de Retención

| Tipo | Frecuencia | Retención | Ubicación |
|------|-----------|-----------|-----------|
| Diario | Todos los días 02:00 | 30 días | `/var/backups/sojus/` |
| Mensual | Día 1 de cada mes | 12 meses | `/var/backups/sojus/monthly/` |
| Ante cambios DDL | Manual pre-migración | Permanente | `/var/backups/sojus/pre-migration/` |

> [!IMPORTANT]
> Se recomienda copiar los backups mensuales a almacenamiento externo (S3, NFS, unidad remota) para protección ante desastres físicos.
