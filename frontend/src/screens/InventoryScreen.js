import React, { useState, useCallback } from 'react';
import {
    View,
    Text,
    StyleSheet,
    FlatList,
    TouchableOpacity,
    RefreshControl,
    ActivityIndicator,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { useFocusEffect } from '@react-navigation/native';
import { inventoryAPI } from '../services/api';
import { COLORS } from '../constants/theme';

export default function InventoryScreen() {
    const [tab, setTab] = useState('hardware');
    const [hardware, setHardware] = useState([]);
    const [software, setSoftware] = useState([]);
    const [loading, setLoading] = useState(true);
    const [refreshing, setRefreshing] = useState(false);

    const load = async () => {
        try {
            const [hwRes, swRes] = await Promise.all([
                inventoryAPI.getAllHardware(),
                inventoryAPI.getAllSoftware(),
            ]);
            setHardware(hwRes.data);
            setSoftware(swRes.data);
        } catch (e) {
            console.error('Error loading inventory:', e);
        } finally {
            setLoading(false);
            setRefreshing(false);
        }
    };

    useFocusEffect(useCallback(() => { load(); }, []));

    const onRefresh = () => { setRefreshing(true); load(); };

    const renderHardwareItem = ({ item }) => (
        <View style={s.card}>
            <View style={s.cardHeader}>
                <Ionicons name="hardware-chip" size={20} color={COLORS.accent} />
                <View style={{ flex: 1 }}>
                    <Text style={s.cardTitle}>{item.clase} — {item.marca} {item.modelo}</Text>
                    <Text style={s.cardSubtitle}>N° Inv.: {item.inventarioPatrimonial}</Text>
                </View>
                <StatusBadge status={item.estado} />
            </View>
            <View style={s.details}>
                {item.tipo && <Detail l="Tipo" v={item.tipo} />}
                {item.numeroSerie && <Detail l="Serie" v={item.numeroSerie} />}
                {item.ubicacionFisica && <Detail l="Ubicación" v={item.ubicacionFisica} />}
            </View>
        </View>
    );

    const renderSoftwareItem = ({ item }) => (
        <View style={s.card}>
            <View style={s.cardHeader}>
                <Ionicons name="apps" size={20} color={COLORS.warning} />
                <View style={{ flex: 1 }}>
                    <Text style={s.cardTitle}>{item.nombre}</Text>
                    <Text style={s.cardSubtitle}>{item.fabricante} — v{item.version}</Text>
                </View>
                <StatusBadge status={item.estado} />
            </View>
            <View style={s.details}>
                {item.tipoLicencia && <Detail l="Licencia" v={item.tipoLicencia} />}
                {item.cantidadLicencias != null && <Detail l="Cantidad" v={String(item.cantidadLicencias)} />}
                {item.fechaVencimiento && <Detail l="Vence" v={item.fechaVencimiento} />}
            </View>
        </View>
    );

    if (loading) {
        return (
            <View style={s.center}>
                <ActivityIndicator size="large" color={COLORS.accent} />
            </View>
        );
    }

    const data = tab === 'hardware' ? hardware : software;
    const renderItem = tab === 'hardware' ? renderHardwareItem : renderSoftwareItem;
    const emptyIcon = tab === 'hardware' ? 'hardware-chip-outline' : 'apps-outline';
    const emptyLabel = tab === 'hardware' ? 'Sin hardware registrado' : 'Sin software registrado';

    return (
        <View style={s.container}>
            {/* Tabs */}
            <View style={s.tabs}>
                <TouchableOpacity
                    style={[s.tab, tab === 'hardware' && s.tabActive]}
                    onPress={() => setTab('hardware')}
                >
                    <Ionicons
                        name="hardware-chip-outline"
                        size={16}
                        color={tab === 'hardware' ? COLORS.accent : COLORS.textMuted}
                    />
                    <Text style={[s.tabText, tab === 'hardware' && s.tabTextActive]}>
                        Hardware ({hardware.length})
                    </Text>
                </TouchableOpacity>
                <TouchableOpacity
                    style={[s.tab, tab === 'software' && s.tabActive]}
                    onPress={() => setTab('software')}
                >
                    <Ionicons
                        name="apps-outline"
                        size={16}
                        color={tab === 'software' ? COLORS.accent : COLORS.textMuted}
                    />
                    <Text style={[s.tabText, tab === 'software' && s.tabTextActive]}>
                        Software ({software.length})
                    </Text>
                </TouchableOpacity>
            </View>

            <FlatList
                data={data}
                keyExtractor={(item) => item.id.toString()}
                renderItem={renderItem}
                contentContainerStyle={{ padding: 14, gap: 10 }}
                refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} />}
                ListEmptyComponent={
                    <View style={s.center}>
                        <Ionicons name={emptyIcon} size={48} color={COLORS.border} />
                        <Text style={s.emptyText}>{emptyLabel}</Text>
                    </View>
                }
            />
        </View>
    );
}

function StatusBadge({ status }) {
    const colors = {
        ACTIVO: COLORS.success,
        EN_REPARACION: COLORS.warning,
        DE_BAJA: COLORS.danger,
        EN_DEPOSITO: COLORS.textMuted,
    };
    const labels = {
        ACTIVO: 'Activo',
        EN_REPARACION: 'En Reparación',
        DE_BAJA: 'De Baja',
        EN_DEPOSITO: 'En Depósito',
    };
    const color = colors[status] || COLORS.textMuted;
    const label = labels[status] || status;

    return (
        <View style={[s.badge, { backgroundColor: color + '15', borderColor: color + '25' }]}>
            <Text style={[s.badgeText, { color }]}>{label}</Text>
        </View>
    );
}

function Detail({ l, v }) {
    return (
        <View style={s.dRow}>
            <Text style={s.dLabel}>{l}:</Text>
            <Text style={s.dVal}>{v}</Text>
        </View>
    );
}

const s = StyleSheet.create({
    container: { flex: 1, backgroundColor: COLORS.bgBody },
    center: { flex: 1, justifyContent: 'center', alignItems: 'center', paddingVertical: 60 },
    tabs: {
        flexDirection: 'row',
        backgroundColor: COLORS.bgCard,
        borderBottomWidth: 1,
        borderBottomColor: COLORS.border,
    },
    tab: {
        flex: 1,
        flexDirection: 'row',
        alignItems: 'center',
        justifyContent: 'center',
        gap: 6,
        paddingVertical: 14,
    },
    tabActive: {
        borderBottomWidth: 2,
        borderBottomColor: COLORS.accent,
    },
    tabText: { fontSize: 13, color: COLORS.textMuted, fontWeight: '500' },
    tabTextActive: { color: COLORS.accent, fontWeight: '600' },
    card: {
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
    cardHeader: { flexDirection: 'row', alignItems: 'center', gap: 12, marginBottom: 10 },
    cardTitle: { fontSize: 14, fontWeight: '600', color: COLORS.textMain },
    cardSubtitle: { fontSize: 12, color: COLORS.textMuted, marginTop: 2 },
    badge: { paddingHorizontal: 8, paddingVertical: 3, borderRadius: 6, borderWidth: 1 },
    badgeText: { fontSize: 10, fontWeight: '600' },
    details: { gap: 4, borderTopWidth: 1, borderTopColor: COLORS.border, paddingTop: 10 },
    dRow: { flexDirection: 'row', gap: 6 },
    dLabel: { fontSize: 12, color: COLORS.textLight, width: 70 },
    dVal: { fontSize: 12, color: COLORS.textMain, fontWeight: '500', flex: 1 },
    emptyText: { fontSize: 14, color: COLORS.textMuted, marginTop: 12 },
});
