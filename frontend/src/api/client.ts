import axios from 'axios';

const client = axios.create({
    baseURL: '/api/v1',
    headers: {
        'Content-Type': 'application/json',
    },
});

// Request interceptor to add token
client.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('accessToken');
        const isAuthRequest = config.url?.includes('/auth/') || config.url?.includes('/users/signup');

        if (token && !isAuthRequest) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Response interceptor to handle auth errors
client.interceptors.response.use(
    (response) => {
        console.log('✅ API Response:', {
            url: response.config.url,
            status: response.status,
            data: response.data
        });
        return response;
    },
    (error) => {
        console.error('❌ API Error:', {
            url: error.config?.url,
            status: error.response?.status,
            data: error.response?.data,
            message: error.message
        });

        if (error.response && error.response.status === 401) {
            console.warn("Unauthorized error. Token cleared.");
            localStorage.removeItem('accessToken');
        }
        return Promise.reject(error);
    }
);

export interface RsData<T> {
    resultCode: string;
    msg: string;
    data: T;
}

export default client;
