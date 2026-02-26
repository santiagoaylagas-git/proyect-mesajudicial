import React, { useState } from 'react';
import {
    View,
    Text,
    TextInput,
    TouchableOpacity,
    StyleSheet,
    Alert,
    KeyboardAvoidingView,
    Platform,
    ActivityIndicator,
} from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { useAuth } from '../context/AuthContext';
import { COLORS } from '../constants/theme';

export default function LoginScreen() {
    const { login } = useAuth();
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [loading, setLoading] = useState(false);
    const [showPassword, setShowPassword] = useState(false);

    const handleLogin = async () => {
        if (!username.trim() || !password.trim()) {
            Alert.alert('Error', 'Ingresá usuario y contraseña');
            return;
        }

        setLoading(true);
        const result = await login(username.trim(), password);
        setLoading(false);

        if (!result.success) {
            Alert.alert('Error de Autenticación', result.error);
        }
    };

    // Acceso rápido demo
    const quickLogin = async (user, pass) => {
        setUsername(user);
        setPassword(pass);
        setLoading(true);
        const result = await login(user, pass);
        setLoading(false);
        if (!result.success) {
            Alert.alert('Error', result.error);
        }
    };

    return (
        <KeyboardAvoidingView
            style={styles.container}
            behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
        >
            {/* Background gradient effect */}
            <View style={styles.bgAccent} />

            <View style={styles.content}>
                {/* Brand */}
                <View style={styles.brandSection}>
                    <Ionicons name="shield-checkmark" size={56} color={COLORS.accentLight} />
                    <Text style={styles.brandName}>SOJUS</Text>
                    <Text style={styles.brandSub}>Sistema de Gestión Judicial</Text>
                    <Text style={styles.brandOrg}>Poder Judicial · Provincia de Santa Fe</Text>
                </View>

                {/* Login Form */}
                <View style={styles.formCard}>
                    <View style={styles.inputGroup}>
                        <Ionicons name="person-outline" size={20} color={COLORS.textMuted} style={styles.inputIcon} />
                        <TextInput
                            style={styles.input}
                            placeholder="Usuario"
                            placeholderTextColor={COLORS.textMuted}
                            value={username}
                            onChangeText={setUsername}
                            autoCapitalize="none"
                            autoCorrect={false}
                        />
                    </View>

                    <View style={styles.inputGroup}>
                        <Ionicons name="lock-closed-outline" size={20} color={COLORS.textMuted} style={styles.inputIcon} />
                        <TextInput
                            style={styles.input}
                            placeholder="Contraseña"
                            placeholderTextColor={COLORS.textMuted}
                            value={password}
                            onChangeText={setPassword}
                            secureTextEntry={!showPassword}
                        />
                        <TouchableOpacity onPress={() => setShowPassword(!showPassword)}>
                            <Ionicons
                                name={showPassword ? 'eye-off-outline' : 'eye-outline'}
                                size={20}
                                color={COLORS.textMuted}
                            />
                        </TouchableOpacity>
                    </View>

                    <TouchableOpacity
                        style={[styles.loginButton, loading && styles.loginButtonDisabled]}
                        onPress={handleLogin}
                        disabled={loading}
                    >
                        {loading ? (
                            <ActivityIndicator color="#fff" />
                        ) : (
                            <Text style={styles.loginButtonText}>Iniciar Sesión</Text>
                        )}
                    </TouchableOpacity>
                </View>

                {/* Quick login demo */}
                <View style={styles.demoSection}>
                    <Text style={styles.demoTitle}>Acceso Rápido (Demo)</Text>
                    <View style={styles.demoButtons}>
                        {[
                            { label: 'Admin', user: 'admin', pass: 'admin123', icon: 'shield' },
                            { label: 'Operador', user: 'operador', pass: 'oper123', icon: 'headset' },
                            { label: 'Técnico', user: 'tecnico', pass: 'tec123', icon: 'construct' },
                        ].map((item) => (
                            <TouchableOpacity
                                key={item.user}
                                style={styles.demoBtn}
                                onPress={() => quickLogin(item.user, item.pass)}
                            >
                                <Ionicons name={item.icon} size={18} color={COLORS.accentLight} />
                                <Text style={styles.demoBtnText}>{item.label}</Text>
                            </TouchableOpacity>
                        ))}
                    </View>
                </View>
            </View>
        </KeyboardAvoidingView>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: COLORS.primary,
    },
    bgAccent: {
        position: 'absolute',
        top: -100,
        right: -100,
        width: 300,
        height: 300,
        borderRadius: 150,
        backgroundColor: 'rgba(3,105,161,0.06)',
    },
    content: {
        flex: 1,
        justifyContent: 'center',
        paddingHorizontal: 32,
    },
    brandSection: {
        alignItems: 'center',
        marginBottom: 40,
    },
    brandName: {
        fontSize: 36,
        fontWeight: '800',
        color: '#fff',
        letterSpacing: 4,
        marginTop: 12,
    },
    brandSub: {
        fontSize: 14,
        color: COLORS.accentLight,
        marginTop: 4,
        letterSpacing: 1,
    },
    brandOrg: {
        fontSize: 11,
        color: COLORS.textLight,
        marginTop: 6,
    },
    formCard: {
        backgroundColor: 'rgba(255,255,255,0.04)',
        borderRadius: 16,
        padding: 24,
        borderWidth: 1,
        borderColor: 'rgba(255,255,255,0.06)',
    },
    inputGroup: {
        flexDirection: 'row',
        alignItems: 'center',
        backgroundColor: 'rgba(255,255,255,0.06)',
        borderRadius: 10,
        paddingHorizontal: 14,
        marginBottom: 14,
        borderWidth: 1,
        borderColor: 'rgba(255,255,255,0.08)',
    },
    inputIcon: {
        marginRight: 10,
    },
    input: {
        flex: 1,
        height: 48,
        color: '#fff',
        fontSize: 15,
    },
    loginButton: {
        backgroundColor: COLORS.accent,
        borderRadius: 10,
        height: 48,
        justifyContent: 'center',
        alignItems: 'center',
        marginTop: 8,
    },
    loginButtonDisabled: {
        opacity: 0.7,
    },
    loginButtonText: {
        color: '#fff',
        fontSize: 16,
        fontWeight: '600',
    },
    demoSection: {
        marginTop: 32,
        alignItems: 'center',
    },
    demoTitle: {
        color: COLORS.textLight,
        fontSize: 11,
        textTransform: 'uppercase',
        letterSpacing: 1.5,
        marginBottom: 12,
    },
    demoButtons: {
        flexDirection: 'row',
        gap: 12,
    },
    demoBtn: {
        flexDirection: 'row',
        alignItems: 'center',
        gap: 6,
        backgroundColor: 'rgba(255,255,255,0.04)',
        paddingHorizontal: 14,
        paddingVertical: 8,
        borderRadius: 8,
        borderWidth: 1,
        borderColor: 'rgba(255,255,255,0.08)',
    },
    demoBtnText: {
        color: 'rgba(255,255,255,0.7)',
        fontSize: 12,
        fontWeight: '500',
    },
});
