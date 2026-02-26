import React, { useState, useEffect } from 'react';
import {
    View,
    Text,
    StyleSheet,
    ScrollView,
    ActivityIndicator,
    TouchableOpacity,
    Alert,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { ticketsAPI } from '../services/api';
import { COLORS, TICKET_STATUS, PRIORITY } from '../constants/theme';

export default function TicketDetailScreen({ route, navigation }) {
    const { ticketId } = route.params;
    const [ticket, setTicket] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        loadTicket();
    }, [ticketId]);

    const loadTicket = async () => {
        try {
            const response = await ticketsAPI.getById(ticketId);
            setTicket(response.data);
        } catch (error) {
            Alert.alert('Error', 'No se pudo cargar el ticket');
            navigation.goBack();
        } finally {
            setLoading(false);
        }
    };

    const changeStatus = async (newStatus) => {
        try {
            await ticketsAPI.changeStatus(ticketId, {
                status: newStatus,
                comentario: `Estado cambiado a ${newStatus}`,
            });
            loadTicket();
            Alert.alert('Éxito', `Estado actualizado a ${newStatus}`);
        } catch (error) {
            Alert.alert('Error', 'No se pudo actualizar el estado');
        }
    };

    if (loading) {
        return (
            <View style={styles.center}>
                <ActivityIndicator size="large" color={COLORS.accent} />
            </View>
        );
    }

    if (!ticket) return null;

    const statusInfo = TICKET_STATUS[ticket.status] || {};
    const priorityInfo = PRIORITY[ticket.prioridad] || {};

    return (
        <ScrollView style={styles.container}>
            {/* Header */}
            <View style={styles.headerSection}>
                <View style={styles.headerRow}>
                    <Text style={styles.ticketIdLabel}>Ticket #{ticket.id}</Text>
                    <View style={[styles.badge, { backgroundColor: statusInfo.color + '15' }]}>
                        <Text style={[styles.badgeText, { color: statusInfo.color }]}>{statusInfo.label}</Text>
                    </View>
                </View>
                <Text style={styles.subject}>{ticket.asunto}</Text>
            </View>

            {/* Info Cards */}
            <View style={styles.infoGrid}>
                <InfoItem icon="alert-circle" label="Prioridad" value={priorityInfo.label} color={priorityInfo.color} />
                <InfoItem icon="location" label="Juzgado" value={ticket.juzgadoNombre || '—'} />
                <InfoItem icon="person" label="Solicitante" value={ticket.solicitanteNombre || '—'} />
                <InfoItem icon="construct" label="Técnico" value={ticket.tecnicoNombre || 'Sin asignar'} />
                <InfoItem icon="hardware-chip" label="Equipo" value={ticket.hardwareInventario || '—'} />
                <InfoItem icon="radio" label="Canal" value={ticket.canal || '—'} />
            </View>

            {/* Descripción */}
            {ticket.descripcion && (
                <View style={styles.section}>
                    <Text style={styles.sectionTitle}>Descripción</Text>
                    <Text style={styles.description}>{ticket.descripcion}</Text>
                </View>
            )}

            {/* Bitácora */}
            {ticket.bitacora && (
                <View style={styles.section}>
                    <Text style={styles.sectionTitle}>Bitácora</Text>
                    <View style={styles.logBox}>
                        <Text style={styles.logText}>{ticket.bitacora}</Text>
                    </View>
                </View>
            )}

            {/* Fechas */}
            <View style={styles.section}>
                <Text style={styles.sectionTitle}>Fechas</Text>
                <View style={styles.dateRow}>
                    <Text style={styles.dateLabel}>Creado:</Text>
                    <Text style={styles.dateValue}>{ticket.createdAt}</Text>
                </View>
                {ticket.updatedAt && (
                    <View style={styles.dateRow}>
                        <Text style={styles.dateLabel}>Actualizado:</Text>
                        <Text style={styles.dateValue}>{ticket.updatedAt}</Text>
                    </View>
                )}
                {ticket.closedAt && (
                    <View style={styles.dateRow}>
                        <Text style={styles.dateLabel}>Cerrado:</Text>
                        <Text style={styles.dateValue}>{ticket.closedAt}</Text>
                    </View>
                )}
            </View>

            {/* Actions */}
            {ticket.status !== 'CERRADO' && (
                <View style={styles.actionsSection}>
                    <Text style={styles.sectionTitle}>Acciones</Text>
                    <View style={styles.actionBtns}>
                        {ticket.status === 'SOLICITADO' && (
                            <TouchableOpacity
                                style={[styles.actionBtn, { backgroundColor: COLORS.warning }]}
                                onPress={() => changeStatus('ASIGNADO')}
                            >
                                <Text style={styles.actionBtnText}>Asignar</Text>
                            </TouchableOpacity>
                        )}
                        {ticket.status === 'ASIGNADO' && (
                            <TouchableOpacity
                                style={[styles.actionBtn, { backgroundColor: COLORS.accent }]}
                                onPress={() => changeStatus('EN_CURSO')}
                            >
                                <Text style={styles.actionBtnText}>Iniciar</Text>
                            </TouchableOpacity>
                        )}
                        {(ticket.status === 'EN_CURSO' || ticket.status === 'ASIGNADO') && (
                            <TouchableOpacity
                                style={[styles.actionBtn, { backgroundColor: COLORS.success }]}
                                onPress={() => changeStatus('CERRADO')}
                            >
                                <Text style={styles.actionBtnText}>Cerrar</Text>
                            </TouchableOpacity>
                        )}
                    </View>
                </View>
            )}

            <View style={{ height: 40 }} />
        </ScrollView>
    );
}

function InfoItem({ icon, label, value, color }) {
    return (
        <View style={styles.infoItem}>
            <Ionicons name={icon} size={16} color={color || COLORS.textMuted} />
            <View>
                <Text style={styles.infoLabel}>{label}</Text>
                <Text style={[styles.infoValue, color ? { color } : null]}>{value}</Text>
            </View>
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: COLORS.bgBody,
    },
    center: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
    },
    headerSection: {
        backgroundColor: COLORS.bgCard,
        padding: 20,
        borderBottomWidth: 1,
        borderBottomColor: COLORS.border,
    },
    headerRow: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
        marginBottom: 8,
    },
    ticketIdLabel: {
        fontSize: 13,
        color: COLORS.textMuted,
        fontWeight: '600',
    },
    badge: {
        paddingHorizontal: 10,
        paddingVertical: 4,
        borderRadius: 6,
    },
    badgeText: {
        fontSize: 12,
        fontWeight: '600',
    },
    subject: {
        fontSize: 18,
        fontWeight: '700',
        color: COLORS.textMain,
    },
    infoGrid: {
        flexDirection: 'row',
        flexWrap: 'wrap',
        padding: 14,
        gap: 8,
    },
    infoItem: {
        flexDirection: 'row',
        alignItems: 'center',
        gap: 8,
        backgroundColor: COLORS.bgCard,
        paddingHorizontal: 12,
        paddingVertical: 10,
        borderRadius: 10,
        borderWidth: 1,
        borderColor: COLORS.border,
        width: '48%',
        flexGrow: 1,
    },
    infoLabel: {
        fontSize: 10,
        color: COLORS.textLight,
        textTransform: 'uppercase',
    },
    infoValue: {
        fontSize: 13,
        fontWeight: '600',
        color: COLORS.textMain,
    },
    section: {
        paddingHorizontal: 14,
        paddingTop: 16,
    },
    sectionTitle: {
        fontSize: 13,
        fontWeight: '600',
        color: COLORS.textMuted,
        textTransform: 'uppercase',
        letterSpacing: 0.5,
        marginBottom: 8,
    },
    description: {
        fontSize: 14,
        color: COLORS.textMain,
        lineHeight: 22,
        backgroundColor: COLORS.bgCard,
        padding: 14,
        borderRadius: 10,
        borderWidth: 1,
        borderColor: COLORS.border,
    },
    logBox: {
        backgroundColor: '#0d1117',
        padding: 14,
        borderRadius: 10,
    },
    logText: {
        fontSize: 12,
        color: '#3fb950',
        fontFamily: 'monospace',
        lineHeight: 20,
    },
    dateRow: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        paddingVertical: 6,
        borderBottomWidth: 1,
        borderBottomColor: COLORS.border,
    },
    dateLabel: {
        fontSize: 13,
        color: COLORS.textMuted,
    },
    dateValue: {
        fontSize: 13,
        color: COLORS.textMain,
        fontWeight: '500',
    },
    actionsSection: {
        paddingHorizontal: 14,
        paddingTop: 20,
    },
    actionBtns: {
        flexDirection: 'row',
        gap: 10,
    },
    actionBtn: {
        flex: 1,
        paddingVertical: 12,
        borderRadius: 10,
        alignItems: 'center',
    },
    actionBtnText: {
        color: '#fff',
        fontWeight: '600',
        fontSize: 14,
    },
});
