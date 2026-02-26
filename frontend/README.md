# Frontend — SOJUS HelpDesk Judicial (React Native)

## Stack
- **Framework:** React Native (Expo ~50)
- **Navegación:** React Navigation 6 (Stack + Drawer)
- **HTTP Client:** Axios con interceptores JWT
- **Estado:** Context API + AsyncStorage
- **Íconos:** @expo/vector-icons (Ionicons)

## Estructura

```
frontend/
├── App.js                          # Entry point
├── package.json
├── app.json                        # Expo config
├── babel.config.js
└── src/
    ├── constants/theme.js          # Colores, API URL, mappings
    ├── context/AuthContext.js      # Autenticación JWT
    ├── navigation/AppNavigator.js  # Stack + Drawer
    ├── screens/
    │   ├── LoginScreen.js          # Login + acceso rápido demo
    │   ├── DashboardScreen.js      # Panel con métricas
    │   ├── TicketsScreen.js        # Lista de tickets
    │   ├── TicketDetailScreen.js   # Detalle + cambio de estado
    │   ├── CreateTicketScreen.js   # Crear nuevo ticket
    │   ├── InventoryScreen.js      # Hardware/Software tabs
    │   └── ContractsScreen.js      # Lista de contratos
    └── services/api.js             # Axios client + endpoints
```

## Setup

```bash
# Requisitos: Node.js 18+, npm, Expo CLI
cd frontend
npm install
npx expo start

# Escanear QR con Expo Go (Android/iOS)
# O presionar 'a' para Android emulator, 'i' para iOS simulator
```

## Configuración API
Editar `src/constants/theme.js`:
```js
// Android emulator:
export const API_BASE_URL = 'http://10.0.2.2:8080';
// iOS simulator:
export const API_BASE_URL = 'http://localhost:8080';
// Dispositivo físico (usar IP de tu PC):
export const API_BASE_URL = 'http://192.168.x.x:8080';
```
