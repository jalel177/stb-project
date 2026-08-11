import axios from 'axios';

const API_BASE_URL = window.__ENV__?.API_URL || 'http://localhost:8080';


const api = axios.create({
  baseURL: API_BASE_URL,
  headers: { 'Content-Type': 'application/json' },
});

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) config.headers.Authorization = `Bearer ${token}`;
    return config;
  },
  (error) => Promise.reject(error)
);

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/';
    }
    return Promise.reject(error);
  }
);

// ========== AUTH SERVICES ==========
export const authService = {
  login: (username, password) => api.post('api/auth/login', { username, password }),
  signup: (username, password) =>
    api.post('api/auth/signup', { username, password }),
  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  }
};

// ========== TRANSACTION SERVICES ==========
export const transactionService = {
  process: (data) => api.post('api/transactions/process', data),
  validate: (data) => api.post('api/transactions/validate', data),
  getHistory: () => api.get('api/transactions/history'),
  getByReference: (reference) => api.get(`api/transactions/${reference}`),
  getByAccount: (accountNumber) => api.get(`api/transactions/account/${accountNumber}`),
  getAll: () => api.get('api/transactions/all'),
  getPending: () => api.get('api/transactions/pending'),
  approve: (id) => api.put(`api/transactions/${id}/approve`),
  reject: (id, reason) => api.put(`api/transactions/${id}/reject`, { reason }),
};

// ========== ADMIN SERVICES ==========
export const adminService = {
  getUsers: () => api.get('api/admin/users'),
  updateUserRole: (id, role) => api.put(`api/admin/users/${id}/role`, { role }),
  getAllAccounts: () => api.get('api/admin/accounts'),
  updateAccountStatus: (accountNumber, status) =>
    api.put(`api/admin/accounts/${accountNumber}/status`, { status }),
};

// ========== USER SELF-SERVICE ==========
export const userService = {
  getMyProfile: () => api.get('api/auth/me'),
};

export default api;