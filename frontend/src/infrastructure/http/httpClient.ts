import axios from 'axios';
import keycloak from '../../plugins/keycloak';

export const httpClient = axios.create({
    baseURL: '/api',
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json'
    }
});

// Request Interceptor
httpClient.interceptors.request.use((config) => {
    if (keycloak && keycloak.token) {
        config.headers.Authorization = `Bearer ${keycloak.token}`;
    }
    return config;
}, (error) => {
    return Promise.reject(error);
});

// Response Interceptor
httpClient.interceptors.response.use((response) => {
    return response.data;
}, (error) => {
    // Map backend domain errors to UI messages
    const errorResponse = error.response?.data;
    const msg = errorResponse?.message || error.message || "Une erreur est survenue.";

    // if (error.response?.status === 401) {
    //     // Handle 401 globally, e.g. redirect to login
    // }

    return Promise.reject(new Error(msg));
});
