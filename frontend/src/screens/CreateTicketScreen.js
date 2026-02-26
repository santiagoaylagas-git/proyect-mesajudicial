import React, { useState } from 'react';
import {
    View,
    Text,
    TextInput,
    StyleSheet,
    TouchableOpacity,
    ScrollView,
    Alert,
    ActivityIndicator,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { ticketsAPI } from '../services/api';
import { COLORS } from '../constants/theme';

export default function CreateTicketScreen({ navigation }) {
    const [asunto, setAsunto] = useState('');
    const [descripcion, setDescripcion] = useState('');
    const [prioridad, setPrioridad] = useState('MEDIA');
    const [loading, setLoading] = useState(false);

    const priorities = [
        { value: 'BAJA', label: 'Baja', color: COLORS.success },
        { value: 'MEDIA', label: 'Media', color: COLORS.warning },
        { value: 'ALTA', label: 'Alta', color: COLORS.danger },
    ];

    const handleCreate = async () => {
        if (!asunto.trim()) {
            Alert.alert('Error', 'El asunto es obligatorio');
            return;
        }

        setLoading(true);
        try {
            await ticketsAPI.create({
                asunto: asunto.trim(),
                descripcion: descripcion.trim(),
                prioridad,
                canal: 'APP_MOVIL',
            });
            Alert.alert('Éxito', 'Ticket creado correctamente', [
                { text: 'OK', onPress: () => navigation.goBack() },
            ]);
        } catch (error) {
            Alert.alert('Error', 'No se pudo crear el ticket');
        } finally {
            setLoading(false);
        }
    };

    return (
        <ScrollView style={styles.container}>
            <View style={styles.form}>
                {/* Asunto */}
                <View style={styles.field}>
                    <Text style={styles.label}>Asunto *</Text>
                    <TextInput
                        style={styles.input}
                        placeholder="Ej: Impresora no funciona"
                        placeholderTextColor={COLORS.textLight}
                        value={asunto}
                        onChangeText={setAsunto}
                    />
                </View>

                {/* Descripción */}
                <View style={styles.field}>
                    <Text style={styles.label}>Descripción</Text>
                    <TextInput
                        style={[styles.input, styles.textArea]}
                        placeholder="Describa el problema en detalle..."
                        placeholderTextColor={COLORS.textLight}
                        value={descripcion}
                        onChangeText={setDescripcion}
                        multiline
                        numberOfLines={5}
                        textAlignVertical="top"
                    />
                </View>

                {/* Prioridad */}
                <View style={styles.field}>
                    <Text style={styles.label}>Prioridad</Text>
                    <View style={styles.priorityButtons}>
                        {priorities.map((p) => (
                            <TouchableOpacity
                                key={p.value}
                                style={[
                                    styles.priorityBtn,
                                    prioridad === p.value && {
                                        backgroundColor: p.color + '15',
                                        borderColor: p.color,
                                    },
                                ]}
                                onPress={() => setPrioridad(p.value)}
                            >
                                <View style={[styles.priorityDot, { backgroundColor: p.color }]} />
                                <Text
                                    style={[
                                        styles.priorityText,
                                        prioridad === p.value && { color: p.color, fontWeight: '600' },
                                    ]}
                                >
                                    {p.label}
                                </Text>
                            </TouchableOpacity>
                        ))}
                    </View>
                </View>

                {/* Submit */}
                <TouchableOpacity
                    style={[styles.submitBtn, loading && { opacity: 0.7 }]}
                    onPress={handleCreate}
                    disabled={loading}
                >
                    {loading ? (
                        <ActivityIndicator color="#fff" />
                    ) : (
                        <>
                            <Ionicons name="send" size={18} color="#fff" />
                            <Text style={styles.submitBtnText}>Crear Ticket</Text>
                        </>
                    )}
                </TouchableOpacity>
            </View>
        </ScrollView>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: COLORS.bgBody,
    },
    form: {
        padding: 20,
        gap: 20,
    },
    field: {
        gap: 6,
    },
    label: {
        fontSize: 13,
        fontWeight: '600',
        color: COLORS.textMuted,
        textTransform: 'uppercase',
        letterSpacing: 0.5,
    },
    input: {
        backgroundColor: COLORS.bgCard,
        borderRadius: 10,
        borderWidth: 1,
        borderColor: COLORS.border,
        padding: 14,
        fontSize: 15,
        color: COLORS.textMain,
    },
    textArea: {
        minHeight: 120,
    },
    priorityButtons: {
        flexDirection: 'row',
        gap: 10,
    },
    priorityBtn: {
        flex: 1,
        flexDirection: 'row',
        alignItems: 'center',
        justifyContent: 'center',
        gap: 6,
        paddingVertical: 12,
        borderRadius: 10,
        backgroundColor: COLORS.bgCard,
        borderWidth: 1,
        borderColor: COLORS.border,
    },
    priorityDot: {
        width: 8,
        height: 8,
        borderRadius: 4,
    },
    priorityText: {
        fontSize: 13,
        color: COLORS.textMuted,
    },
    submitBtn: {
        flexDirection: 'row',
        alignItems: 'center',
        justifyContent: 'center',
        gap: 8,
        backgroundColor: COLORS.accent,
        paddingVertical: 14,
        borderRadius: 10,
        marginTop: 10,
    },
    submitBtnText: {
        color: '#fff',
        fontSize: 16,
        fontWeight: '600',
    },
});
