import axios from 'axios';
import { AuthState } from '../state/AuthContext';

/** API gateway URL (e.g. http://localhost:8680). */
const GATEWAY_URL = import.meta.env.VITE_API_GATEWAY_URL ?? 'http://localhost:8680';

/** Base for all v1 APIs (e.g. http://localhost:8680/api/v1). Gateway routes: /api/v1/auth/**, /api/v1/entity/**, /api/v1/timesheet/**. */
const API_BASE = import.meta.env.VITE_API_BASE_URL ?? `${GATEWAY_URL}/api/v1`;

let currentAuth: AuthState | null = null;

export const setAuthState = (auth: AuthState | null) => {
  currentAuth = auth;
};

const addAuthInterceptor = (instance: ReturnType<typeof axios.create>) => {
  instance.interceptors.request.use(config => {
    if (currentAuth?.token) {
      config.headers = {
        ...config.headers,
        Authorization: `Bearer ${currentAuth.token}`
      };
    }
    return config;
  });
};

/** Auth: POST /api/v1/auth/login (user-service, JWT relaxed). */
export const loginClient = axios.create({
  baseURL: API_BASE
});

/** Entity-service: customers, companies, projects, employees, tasks, cost-codes, project-budgets. Gateway path: /api/v1/entity/** → rewritten to /api/v1/**. */
export const api = axios.create({
  baseURL: `${API_BASE}/entity`
});
addAuthInterceptor(api);

/** Timesheet-service: timesheet CRUD. Gateway path: /api/v1/timesheet/** → rewritten to /api/v1/**. */
export const timesheetApi = axios.create({
  baseURL: `${API_BASE}/timesheet`
});
addAuthInterceptor(timesheetApi);

