import React, { useState, useCallback } from 'react';
import {
    View,
    Text,
    StyleSheet,
    FlatList,
    TouchableOpacity,
    RefreshControl,
    ActivityIndicator,
    Platform,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { useFocusEffect } from '@react-navigation/native';
import { ticketsAPI } from '../services/api';
import { COLORS, TICKET_STATUS, PRIORITY } from '../constants/theme';

export default function TicketsScreen({ navigation }) {
    const [tickets, setTickets] = useState([]);
    const [loading, setLoading] = useState(true);
    const [refreshing, setRefreshing] = useState(false);

    const loadTickets = async () => {
        try {
            const response = await ticketsAPI.getAll();
            setTickets(response.data);
        } catch (error) {
            console.error('Error loading tickets:', error);
        } finally {
            setLoading(false);
            setRefreshing(false);
        }
    };

    useFocusEffect(
        useCallback(() => {
            loadTickets();
        }, [])
    );

    const onRefresh = () => {
        setRefreshing(true);
        loadTickets();
    };

    const renderTicket = ({ item }) => {
        const statusInfo = TICKET_STATUS[item.status] || { label: item.status, color: COLORS.textMuted };
        const priorityInfo = PRIORITY[item.prioridad] || { label: item.prioridad, color: COLORS.textMuted };

        return (
            <TouchableOpacity
                style={styles.ticketCard}
                onPress={() => navigation.navigate('TicketDetail', { ticketId: item.id })}
                activeOpacity={0.7}
            >
                <View style={styles.ticketHeader}>
                    <Text style={styles.ticketId}>#{item.id}</Text>
                    <View style={[styles.badge, { backgroundColor: statusInfo.color + '15', borderColor: statusInfo.color + '25' }]}>
                        <Text style={[styles.badgeText, { color: statusInfo.color }]}>{statusInfo.label}</Text>
                    </View>
                </View>

                <Text style={styles.ticketSubject}>{item.asunto}</Text>

                <View style={styles.ticketMeta}>
                    <View style={[styles.priorityDot, { backgroundColor: priorityInfo.color }]} />
                    <Text style={styles.metaText}>{priorityInfo.label}</Text>
                    {item.juzgadoNombre && (
                        <>
                            <Text style={styles.metaSep}>·</Text>
                            <Ionicons name="location-outline" size={12} color={COLORS.textMuted} />
                            <Text style={styles.metaText} numberOfLines={1}>{item.juzgadoNombre}</Text>
                        </>
                    )}
                </View>

                <View style={styles.ticketFooter}>
                    {item.tecnicoNombre && (
                        <View style={styles.footerItem}>
                            <Ionicons name="person-outline" size={12} color={COLORS.textMuted} />
                            <Text style={styles.footerText}>{item.tecnicoNombre}</Text>
                        </View>
                    )}
                    <Text style={styles.footerDate}>{item.createdAt}</Text>
                </View>
            </TouchableOpacity>
        );
    };

    if (loading) {
        return (
            <View style={styles.center}>
                <ActivityIndicator size="large" color={COLORS.accent} />
            </View>
        );
    }

    return (
        <View style={styles.container}>
            {/* Header with create button */}
            <View style={styles.headerBar}>
                <Text style={styles.headerCount}>{tickets.length} ticket(s)</Text>
                <TouchableOpacity
                    style={styles.createBtn}
                    onPress={() => navigation.navigate('CreateTicket')}
                >
                    <Ionicons name="add" size={18} color="#fff" />
                    <Text style={styles.createBtnText}>Nuevo</Text>
                </TouchableOpacity>
            </View>

            <FlatList
                data={tickets}
                keyExtractor={(item) => item.id.toString()}
                renderItem={renderTicket}
                contentContainerStyle={styles.listContent}
                refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} />}
                ListEmptyComponent={
                    <View style={styles.emptyState}>
                        <Ionicons name="ticket-outline" size={48} color={COLORS.border} />
                        <Text style={styles.emptyText}>No hay tickets registrados</Text>
                    </View>
                }
            />
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
    headerBar: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
        paddingHorizontal: 16,
        paddingVertical: 12,
        backgroundColor: COLORS.bgCard,
        borderBottomWidth: 1,
        borderBottomColor: COLORS.border,
    },
    headerCount: {
        fontSize: 13,
        color: COLORS.textMuted,
    },
    createBtn: {
        flexDirection: 'row',
        alignItems: 'center',
        backgroundColor: COLORS.accent,
        paddingHorizontal: 14,
        paddingVertical: 8,
        borderRadius: 8,
        gap: 4,
    },
    createBtnText: {
        color: '#fff',
        fontSize: 13,
        fontWeight: '600',
    },
    listContent: {
        padding: 14,
        gap: 10,
    },
    ticketCard: {
        backgroundColor: COLORS.bgCard,
        borderRadius: 12,
        padding: 16,
        borderWidth: 1,
        borderColor: COLORS.border,
        shadowColor: '#000',
        shadowOffset: { width: 0, height: 1 },
        shadowOpacity: 0.04,
        shadowRadius: 3,
        elevation: 2,
    },
    ticketHeader: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
        marginBottom: 8,
    },
    ticketId: {
        fontSize: 12,
        color: COLORS.textMuted,
        fontWeight: '600',
        fontFamily: Platform?.OS === 'ios' ? 'Menlo' : 'monospace',
    },
    badge: {
        paddingHorizontal: 8,
        paddingVertical: 3,
        borderRadius: 6,
        borderWidth: 1,
    },
    badgeText: {
        fontSize: 11,
        fontWeight: '600',
    },
    ticketSubject: {
        fontSize: 15,
        fontWeight: '600',
        color: COLORS.textMain,
        marginBottom: 8,
    },
    ticketMeta: {
        flexDirection: 'row',
        alignItems: 'center',
        gap: 6,
        marginBottom: 10,
    },
    priorityDot: {
        width: 8,
        height: 8,
        borderRadius: 4,
    },
    metaText: {
        fontSize: 12,
        color: COLORS.textMuted,
    },
    metaSep: {
        color: COLORS.border,
    },
    ticketFooter: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
        borderTopWidth: 1,
        borderTopColor: COLORS.border,
        paddingTop: 8,
    },
    footerItem: {
        flexDirection: 'row',
        alignItems: 'center',
        gap: 4,
    },
    footerText: {
        fontSize: 11,
        color: COLORS.textMuted,
    },
    footerDate: {
        fontSize: 11,
        color: COLORS.textLight,
    },
    emptyState: {
        alignItems: 'center',
        paddingVertical: 60,
        gap: 12,
    },
    emptyText: {
        fontSize: 14,
        color: COLORS.textMuted,
    },
});
