import React, { createContext, useContext, useEffect, useMemo, useState } from 'react';

interface TenantState {
  customerId: number | null;
  companyId: number | null;
  employeeId: number | null;
}

interface TenantContextValue extends TenantState {
  setCustomerId: (id: number | null) => void;
  setCompanyId: (id: number | null) => void;
  setEmployeeId: (id: number | null) => void;
}

const STORAGE_KEY = 'constructflow_tenant';

const TenantContext = createContext<TenantContextValue | undefined>(undefined);

export const TenantProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [state, setState] = useState<TenantState>(() => {
    const raw = window.localStorage.getItem(STORAGE_KEY);
    if (!raw) return { customerId: null, companyId: null, employeeId: null };
    try {
      return JSON.parse(raw) as TenantState;
    } catch {
      return { customerId: null, companyId: null, employeeId: null };
    }
  });

  useEffect(() => {
    window.localStorage.setItem(STORAGE_KEY, JSON.stringify(state));
  }, [state]);

  const value = useMemo<TenantContextValue>(
    () => ({
      ...state,
      setCustomerId(id) {
        setState(prev => ({ ...prev, customerId: id, companyId: null }));
      },
      setCompanyId(id) {
        setState(prev => ({ ...prev, companyId: id }));
      },
      setEmployeeId(id) {
        setState(prev => ({ ...prev, employeeId: id }));
      }
    }),
    [state]
  );

  return <TenantContext.Provider value={value}>{children}</TenantContext.Provider>;
};

export const useTenant = (): TenantContextValue => {
  const ctx = useContext(TenantContext);
  if (!ctx) throw new Error('useTenant must be used within TenantProvider');
  return ctx;
};

