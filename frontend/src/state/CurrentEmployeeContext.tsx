import React, { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import { api } from '../api/client';
import { useAuth } from './AuthContext';

export type EmployeeRole = 'WORKER' | 'COMPANY_ADMIN' | 'CUSTOMER_ADMIN';

export interface CurrentEmployeeDto {
  id: number;
  customerId: number;
  companyId: number;
  employeeRole: EmployeeRole;
  firstName: string;
  lastName: string;
}

interface CurrentEmployeeContextValue {
  /** Set when user is USER and GET /me returned an employee. Null for ADMIN or when not yet loaded / 404. */
  currentEmployee: CurrentEmployeeDto | null;
  loading: boolean;
  error: string | null;
  refetch: () => void;
}

const CurrentEmployeeContext = createContext<CurrentEmployeeContextValue | undefined>(undefined);

export const CurrentEmployeeProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const auth = useAuth();
  const [currentEmployee, setCurrentEmployee] = useState<CurrentEmployeeDto | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchMe = useCallback(() => {
    if (auth.userRole !== 'USER' || !auth.isAuthenticated) {
      setCurrentEmployee(null);
      setError(null);
      return;
    }
    setLoading(true);
    setError(null);
    api
      .get<CurrentEmployeeDto>('/me')
      .then(res => {
        setCurrentEmployee(res.data);
      })
      .catch(err => {
        if (err?.response?.status === 404) {
          setCurrentEmployee(null);
        } else {
          setError(err?.response?.data?.message ?? 'Failed to load your employee profile');
        }
      })
      .finally(() => setLoading(false));
  }, [auth.userRole, auth.isAuthenticated]);

  useEffect(() => {
    if (!auth.isAuthenticated) {
      setCurrentEmployee(null);
      setError(null);
      return;
    }
    fetchMe();
  }, [auth.isAuthenticated, auth.userRole, fetchMe]);

  const value = useMemo<CurrentEmployeeContextValue>(
    () => ({
      currentEmployee,
      loading,
      error,
      refetch: fetchMe
    }),
    [currentEmployee, loading, error, fetchMe]
  );

  return (
    <CurrentEmployeeContext.Provider value={value}>{children}</CurrentEmployeeContext.Provider>
  );
};

export const useCurrentEmployee = (): CurrentEmployeeContextValue => {
  const ctx = useContext(CurrentEmployeeContext);
  if (!ctx) throw new Error('useCurrentEmployee must be used within CurrentEmployeeProvider');
  return ctx;
};
