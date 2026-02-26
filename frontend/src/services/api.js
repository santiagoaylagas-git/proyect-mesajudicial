import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { API_BASE_URL } from '../constants/theme';

const api = axios.create({
    baseURL: API_BASE_URL,
    timeout: 15000,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Interceptor: agregar JWT token a cada request
api.interceptors.request.use(
    async (config) => {
        const token = await AsyncStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

// Interceptor: manejar errores de autenticación
api.interceptors.response.use(
    (response) => response,
    async (error) => {
        if (error.response?.status === 401) {
            await AsyncStorage.removeItem('token');
            await AsyncStorage.removeItem('user');
        }
        return Promise.reject(error);
    }
);

// ---- Auth ----
export const authAPI = {
    login: (username, password) =>
        api.post('/api/auth/login', { username, password }),
    me: () => api.get('/api/auth/me'),
};

// ---- Tickets ----
export const ticketsAPI = {
    getAll: () => api.get('/api/tickets'),
    getMyTickets: () => api.get('/api/tickets/my'),
    getById: (id) => api.get(`/api/tickets/${id}`),
    create: (data) => api.post('/api/tickets', data),
    changeStatus: (id, data) => api.patch(`/api/tickets/${id}/status`, data),
};

// ---- Inventory ----
export const inventoryAPI = {
    getAllHardware: () => api.get('/api/inventory/hardware'),
    getHardwareById: (id) => api.get(`/api/inventory/hardware/${id}`),
    createHardware: (data) => api.post('/api/inventory/hardware', data),
    getAllSoftware: () => api.get('/api/inventory/software'),
    getSoftwareById: (id) => api.get(`/api/inventory/software/${id}`),
    createSoftware: (data) => api.post('/api/inventory/software', data),
};

// ---- Contracts ----
export const contractsAPI = {
    getAll: () => api.get('/api/contracts'),
    getById: (id) => api.get(`/api/contracts/${id}`),
    create: (data) => api.post('/api/contracts', data),
    getExpiring: (days = 30) => api.get(`/api/contracts/expiring?days=${days}`),
};

// ---- Locations ----
export const locationsAPI = {
    getCircunscripciones: () => api.get('/api/locations/circunscripciones'),
    getJuzgados: () => api.get('/api/locations/juzgados'),
};

// ---- Users ----
export const usersAPI = {
    getAll: () => api.get('/api/users'),
    getById: (id) => api.get(`/api/users/${id}`),
    getByRole: (role) => api.get(`/api/users/role/${role}`),
};

// ---- Dashboard ----
export const dashboardAPI = {
    getStats: () => api.get('/api/dashboard/stats'),
};

// ---- Audit ----
export const auditAPI = {
    getRecent: () => api.get('/api/audit'),
    getByEntity: (entityName, entityId) =>
        api.get(`/api/audit/entity/${entityName}/${entityId}`),
};

export default api;
