import React, { useState, useCallback } from 'react';
import {
    View,
    Text,
    StyleSheet,
    ScrollView,
    TouchableOpacity,
    RefreshControl,
    ActivityIndicator,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { useFocusEffect } from '@react-navigation/native';
import { useAuth } from '../context/AuthContext';
import { dashboardAPI } from '../services/api';
import { COLORS } from '../constants/theme';

export default function DashboardScreen({ navigation }) {
    const { user, logout } = useAuth();
    const [stats, setStats] = useState(null);
    const [loading, setLoading] = useState(true);
    const [refreshing, setRefreshing] = useState(false);

    const loadStats = async () => {
        try {
            const response = await dashboardAPI.getStats();
            setStats(response.data);
        } catch (error) {
            console.error('Error loading stats:', error);
        } finally {
            setLoading(false);
            setRefreshing(false);
        }
    };

    useFocusEffect(
        useCallback(() => {
            loadStats();
        }, [])
    );

    const onRefresh = () => {
        setRefreshing(true);
        loadStats();
    };

    if (loading) {
        return (
            <View style={styles.center}>
                <ActivityIndicator size="large" color={COLORS.accent} />
            </View>
        );
    }

    const statCards = [
        { label: 'Tickets Abiertos', value: stats?.ticketsAbiertos ?? 0, icon: 'ticket-outline', color: COLORS.info },
        { label: 'Prioridad Alta', value: stats?.ticketsPrioridadAlta ?? 0, icon: 'alert-circle-outline', color: COLORS.danger },
        { label: 'Cerrados (Mes)', value: stats?.ticketsCerradosMes ?? 0, icon: 'checkmark-circle-outline', color: COLORS.success },
        { label: 'Hardware', value: stats?.totalHardware ?? 0, icon: 'hardware-chip-outline', color: COLORS.accent },
        { label: 'Software', value: stats?.totalSoftware ?? 0, icon: 'apps-outline', color: COLORS.warning },
        { label: 'Contratos', value: stats?.contratosVigentes ?? 0, icon: 'document-text-outline', color: COLORS.success },
    ];

    return (
        <ScrollView
            style={styles.container}
            refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} />}
        >
            {/* Welcome header */}
            <View style={styles.welcomeBar}>
                <View>
                    <Text style={styles.welcomeText}>Bienvenido,</Text>
                    <Text style={styles.userName}>{user?.fullName || user?.username}</Text>
                    <Text style={styles.userRole}>{user?.role}</Text>
                </View>
                <TouchableOpacity style={styles.logoutBtn} onPress={logout}>
                    <Ionicons name="log-out-outline" size={22} color={COLORS.danger} />
                </TouchableOpacity>
            </View>

            {/* Stats grid */}
            <View style={styles.statsGrid}>
                {statCards.map((card, index) => (
                    <TouchableOpacity
                        key={index}
                        style={styles.statCard}
                        activeOpacity={0.7}
                    >
                        <View style={[styles.statIconBg, { backgroundColor: card.color + '15' }]}>
                            <Ionicons name={card.icon} size={22} color={card.color} />
                        </View>
                        <Text style={styles.statValue}>{card.value}</Text>
                        <Text style={styles.statLabel}>{card.label}</Text>
                    </TouchableOpacity>
                ))}
            </View>

            {/* Quick Actions */}
            <Text style={styles.sectionTitle}>Acciones Rápidas</Text>
            <View style={styles.quickActions}>
                <TouchableOpacity
                    style={styles.actionBtn}
                    onPress={() => navigation.navigate('CreateTicket')}
                >
                    <Ionicons name="add-circle-outline" size={24} color={COLORS.accent} />
                    <Text style={styles.actionBtnText}>Nuevo Ticket</Text>
                </TouchableOpacity>
                <TouchableOpacity
                    style={styles.actionBtn}
                    onPress={() => navigation.navigate('Tickets')}
                >
                    <Ionicons name="list-outline" size={24} color={COLORS.accent} />
                    <Text style={styles.actionBtnText}>Ver Tickets</Text>
                </TouchableOpacity>
                <TouchableOpacity
                    style={styles.actionBtn}
                    onPress={() => navigation.navigate('Inventory')}
                >
                    <Ionicons name="hardware-chip-outline" size={24} color={COLORS.accent} />
                    <Text style={styles.actionBtnText}>Inventario</Text>
                </TouchableOpacity>
            </View>

            {/* Alert: contracts expiring */}
            {stats?.contratosProximosVencer > 0 && (
                <TouchableOpacity
                    style={styles.alertCard}
                    onPress={() => navigation.navigate('Contracts')}
                >
                    <Ionicons name="warning-outline" size={22} color={COLORS.warning} />
                    <View style={styles.alertContent}>
                        <Text style={styles.alertTitle}>Contratos por Vencer</Text>
                        <Text style={styles.alertText}>
                            {stats.contratosProximosVencer} contrato(s) vencen en los próximos 30 días
                        </Text>
                    </View>
                    <Ionicons name="chevron-forward" size={18} color={COLORS.textMuted} />
                </TouchableOpacity>
            )}

            <View style={{ height: 32 }} />
        </ScrollView>
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
    welcomeBar: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
        paddingHorizontal: 20,
        paddingVertical: 20,
        backgroundColor: COLORS.bgCard,
        borderBottomWidth: 1,
        borderBottomColor: COLORS.border,
    },
    welcomeText: {
        fontSize: 13,
        color: COLORS.textMuted,
    },
    userName: {
        fontSize: 20,
        fontWeight: '700',
        color: COLORS.textMain,
    },
    userRole: {
        fontSize: 12,
        color: COLORS.accent,
        marginTop: 2,
        fontWeight: '500',
        textTransform: 'uppercase',
        letterSpacing: 0.5,
    },
    logoutBtn: {
        padding: 8,
        borderRadius: 8,
        backgroundColor: COLORS.dangerBg,
    },
    statsGrid: {
        flexDirection: 'row',
        flexWrap: 'wrap',
        paddingHorizontal: 14,
        paddingTop: 16,
        gap: 10,
    },
    statCard: {
        backgroundColor: COLORS.bgCard,
        borderRadius: 14,
        padding: 16,
        width: '48%',
        flexGrow: 1,
        borderWidth: 1,
        borderColor: COLORS.border,
        shadowColor: '#000',
        shadowOffset: { width: 0, height: 1 },
        shadowOpacity: 0.04,
        shadowRadius: 3,
        elevation: 2,
    },
    statIconBg: {
        width: 40,
        height: 40,
        borderRadius: 10,
        justifyContent: 'center',
        alignItems: 'center',
        marginBottom: 10,
    },
    statValue: {
        fontSize: 28,
        fontWeight: '800',
        color: COLORS.textMain,
    },
    statLabel: {
        fontSize: 12,
        color: COLORS.textMuted,
        marginTop: 2,
    },
    sectionTitle: {
        fontSize: 14,
        fontWeight: '600',
        color: COLORS.textMuted,
        paddingHorizontal: 20,
        paddingTop: 24,
        paddingBottom: 10,
        textTransform: 'uppercase',
        letterSpacing: 0.5,
    },
    quickActions: {
        flexDirection: 'row',
        paddingHorizontal: 14,
        gap: 10,
    },
    actionBtn: {
        flex: 1,
        backgroundColor: COLORS.bgCard,
        borderRadius: 12,
        padding: 16,
        alignItems: 'center',
        gap: 8,
        borderWidth: 1,
        borderColor: COLORS.border,
    },
    actionBtnText: {
        fontSize: 12,
        color: COLORS.textMain,
        fontWeight: '500',
    },
    alertCard: {
        flexDirection: 'row',
        alignItems: 'center',
        backgroundColor: COLORS.warningBg,
        marginHorizontal: 14,
        marginTop: 20,
        padding: 16,
        borderRadius: 12,
        borderWidth: 1,
        borderColor: 'rgba(217,119,6,0.15)',
        gap: 12,
    },
    alertContent: {
        flex: 1,
    },
    alertTitle: {
        fontSize: 14,
        fontWeight: '600',
        color: COLORS.warning,
    },
    alertText: {
        fontSize: 12,
        color: COLORS.textMuted,
        marginTop: 2,
    },
});
