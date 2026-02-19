import React, { createContext, useContext, useEffect, useMemo, useState } from 'react';

type UserRole = 'USER' | 'ADMIN';

export interface AuthState {
  token: string | null;
  userId: number | null;
  username: string | null;
  userRole: UserRole | null;
}

interface AuthContextValue extends AuthState {
  login: (state: AuthState) => void;
  logout: () => void;
  isAuthenticated: boolean;
}

const STORAGE_KEY = 'constructflow_auth';

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [state, setState] = useState<AuthState>(() => {
    const raw = window.localStorage.getItem(STORAGE_KEY);
    if (!raw) return { token: null, userId: null, username: null, userRole: null };
    try {
      const parsed = JSON.parse(raw) as AuthState;
      return parsed;
    } catch {
      return { token: null, userId: null, username: null, userRole: null };
    }
  });

  useEffect(() => {
    window.localStorage.setItem(STORAGE_KEY, JSON.stringify(state));
  }, [state]);

  const value = useMemo<AuthContextValue>(
    () => ({
      ...state,
      isAuthenticated: Boolean(state.token),
      login(next) {
        setState(next);
      },
      logout() {
        setState({ token: null, userId: null, username: null, userRole: null });
      }
    }),
    [state]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = (): AuthContextValue => {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
};

