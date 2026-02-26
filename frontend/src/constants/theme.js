// Colores y constantes de diseño para SOJUS
export const COLORS = {
    primary: '#0a1628',
    primaryLight: '#162236',
    primaryMid: '#1c2d45',
    accent: '#0369A1',
    accentLight: '#0284C7',
    bgBody: '#F8FAFC',
    bgCard: '#FFFFFF',
    textMain: '#020617',
    textMuted: '#475569',
    textLight: '#94A3B8',
    border: '#DFE3EA',
    success: '#059669',
    successBg: 'rgba(5,150,105,0.08)',
    warning: '#D97706',
    warningBg: 'rgba(217,119,6,0.08)',
    danger: '#DC2626',
    dangerBg: 'rgba(220,38,38,0.08)',
    info: '#2563EB',
    infoBg: 'rgba(37,99,235,0.08)',
    white: '#FFFFFF',
};

export const API_BASE_URL = 'http://10.0.2.2:8080'; // Android emulator → localhost
// Para dispositivo físico, usar la IP de tu máquina: 'http://192.168.x.x:8080'
// Para iOS simulator: 'http://localhost:8080'

export const ROLES = {
    ADMIN: 'ADMINISTRADOR',
    OPERADOR: 'OPERADOR',
    TECNICO: 'TECNICO',
};

export const TICKET_STATUS = {
    SOLICITADO: { label: 'Solicitado', color: COLORS.info },
    ASIGNADO: { label: 'Asignado', color: COLORS.warning },
    EN_CURSO: { label: 'En Curso', color: COLORS.accent },
    CERRADO: { label: 'Cerrado', color: COLORS.success },
};

export const PRIORITY = {
    ALTA: { label: 'Alta', color: COLORS.danger },
    MEDIA: { label: 'Media', color: COLORS.warning },
    BAJA: { label: 'Baja', color: COLORS.success },
};
