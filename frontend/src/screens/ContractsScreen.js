import React, { useState, useCallback } from 'react';
import { View, Text, StyleSheet, FlatList, RefreshControl, ActivityIndicator } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { useFocusEffect } from '@react-navigation/native';
import { contractsAPI } from '../services/api';
import { COLORS } from '../constants/theme';

export default function ContractsScreen() {
    const [contracts, setContracts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [refreshing, setRefreshing] = useState(false);

    const load = async () => {
        try {
            const res = await contractsAPI.getAll();
            setContracts(res.data);
        } catch (e) { console.error(e); }
        finally { setLoading(false); setRefreshing(false); }
    };

    useFocusEffect(useCallback(() => { load(); }, []));

    const renderItem = ({ item }) => (
        <View style={s.card}>
            <View style={s.row}>
                <Ionicons name="document-text" size={20} color={COLORS.accent} />
                <View style={{ flex: 1 }}>
                    <Text style={s.name}>{item.nombre}</Text>
                    <Text style={s.prov}>{item.proveedor}</Text>
                </View>
            </View>
            <View style={s.details}>
                {item.numeroContrato && <Detail l="N°" v={item.numeroContrato} />}
                <Detail l="Inicio" v={item.fechaInicio || '—'} />
                <Detail l="Fin" v={item.fechaFin || '—'} />
                {item.coberturaHw && <Detail l="HW" v={item.coberturaHw} />}
                {item.coberturaSw && <Detail l="SW" v={item.coberturaSw} />}
            </View>
        </View>
    );

    if (loading) return <View style={s.center}><ActivityIndicator size="large" color={COLORS.accent} /></View>;

    return (
        <FlatList data={contracts} keyExtractor={i => i.id.toString()} renderItem={renderItem}
            contentContainerStyle={{ padding: 14, gap: 10 }}
            refreshControl={<RefreshControl refreshing={refreshing} onRefresh={() => { setRefreshing(true); load(); }} />}
            ListEmptyComponent={<View style={s.center}><Ionicons name="document-outline" size={48} color={COLORS.border} /><Text style={s.empty}>Sin contratos</Text></View>}
        />
    );
}

function Detail({ l, v }) {
    return <View style={s.dRow}><Text style={s.dLabel}>{l}:</Text><Text style={s.dVal}>{v}</Text></View>;
}

const s = StyleSheet.create({
    center: { flex: 1, justifyContent: 'center', alignItems: 'center', paddingVertical: 60 },
    card: { backgroundColor: COLORS.bgCard, borderRadius: 12, padding: 16, borderWidth: 1, borderColor: COLORS.border },
    row: { flexDirection: 'row', alignItems: 'center', gap: 12, marginBottom: 10 },
    name: { fontSize: 15, fontWeight: '600', color: COLORS.textMain },
    prov: { fontSize: 12, color: COLORS.textMuted },
    details: { gap: 4, borderTopWidth: 1, borderTopColor: COLORS.border, paddingTop: 10 },
    dRow: { flexDirection: 'row', gap: 6 },
    dLabel: { fontSize: 12, color: COLORS.textLight, width: 45 },
    dVal: { fontSize: 12, color: COLORS.textMain, fontWeight: '500', flex: 1 },
    empty: { fontSize: 14, color: COLORS.textMuted, marginTop: 12 },
});
