import React from 'react';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { createDrawerNavigator } from '@react-navigation/drawer';
import { ActivityIndicator, View } from 'react-native';
import { Ionicons } from '@expo/vector-icons';
import { useAuth } from '../context/AuthContext';
import { COLORS } from '../constants/theme';

// Screens
import LoginScreen from '../screens/LoginScreen';
import DashboardScreen from '../screens/DashboardScreen';
import TicketsScreen from '../screens/TicketsScreen';
import TicketDetailScreen from '../screens/TicketDetailScreen';
import CreateTicketScreen from '../screens/CreateTicketScreen';
import InventoryScreen from '../screens/InventoryScreen';
import ContractsScreen from '../screens/ContractsScreen';

const Stack = createNativeStackNavigator();
const Drawer = createDrawerNavigator();

// Drawer Navigator (menú lateral filtrado por rol)
function DrawerNavigator() {
    const { user, logout } = useAuth();
    const role = user?.role;

    // Determinar qué pantallas son visibles para cada rol
    const canSeeDashboard = role === 'ADMINISTRADOR' || role === 'OPERADOR';
    const canSeeInventory = role === 'ADMINISTRADOR' || role === 'TECNICO';
    const canSeeContracts = role === 'ADMINISTRADOR' || role === 'OPERADOR';

    return (
        <Drawer.Navigator
            screenOptions={{
                headerStyle: { backgroundColor: COLORS.primary },
                headerTintColor: COLORS.white,
                headerTitleStyle: { fontWeight: '600' },
                drawerStyle: { backgroundColor: COLORS.primary, width: 280 },
                drawerActiveTintColor: COLORS.accentLight,
                drawerInactiveTintColor: COLORS.textLight,
                drawerLabelStyle: { fontSize: 15, marginLeft: -10 },
            }}
        >
            {canSeeDashboard && (
                <Drawer.Screen
                    name="Dashboard"
                    component={DashboardScreen}
                    options={{
                        title: 'Panel Principal',
                        drawerIcon: ({ color, size }) => (
                            <Ionicons name="grid-outline" size={size} color={color} />
                        ),
                    }}
                />
            )}
            <Drawer.Screen
                name="Tickets"
                component={TicketsScreen}
                options={{
                    title: role === 'TECNICO' ? 'Mis Tickets' : 'Mesa de Ayuda',
                    drawerIcon: ({ color, size }) => (
                        <Ionicons name="ticket-outline" size={size} color={color} />
                    ),
                }}
            />
            {canSeeInventory && (
                <Drawer.Screen
                    name="Inventory"
                    component={InventoryScreen}
                    options={{
                        title: 'Inventario',
                        drawerIcon: ({ color, size }) => (
                            <Ionicons name="hardware-chip-outline" size={size} color={color} />
                        ),
                    }}
                />
            )}
            {canSeeContracts && (
                <Drawer.Screen
                    name="Contracts"
                    component={ContractsScreen}
                    options={{
                        title: 'Contratos',
                        drawerIcon: ({ color, size }) => (
                            <Ionicons name="document-text-outline" size={size} color={color} />
                        ),
                    }}
                />
            )}
        </Drawer.Navigator>
    );
}

// Main Navigator
export default function AppNavigator() {
    const { isAuthenticated, loading } = useAuth();

    if (loading) {
        return (
            <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center', backgroundColor: COLORS.primary }}>
                <ActivityIndicator size="large" color={COLORS.accentLight} />
            </View>
        );
    }

    return (
        <Stack.Navigator screenOptions={{ headerShown: false }}>
            {!isAuthenticated ? (
                <Stack.Screen name="Login" component={LoginScreen} />
            ) : (
                <>
                    <Stack.Screen name="Main" component={DrawerNavigator} />
                    <Stack.Screen
                        name="TicketDetail"
                        component={TicketDetailScreen}
                        options={{
                            headerShown: true,
                            title: 'Detalle del Ticket',
                            headerStyle: { backgroundColor: COLORS.primary },
                            headerTintColor: COLORS.white,
                        }}
                    />
                    <Stack.Screen
                        name="CreateTicket"
                        component={CreateTicketScreen}
                        options={{
                            headerShown: true,
                            title: 'Nuevo Ticket',
                            headerStyle: { backgroundColor: COLORS.primary },
                            headerTintColor: COLORS.white,
                        }}
                    />
                </>
            )}
        </Stack.Navigator>
    );
}
