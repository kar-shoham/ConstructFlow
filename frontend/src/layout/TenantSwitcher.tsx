import React from 'react';
import { api } from '../api/client';
import { useTenant } from '../state/TenantContext';
import { useAuth } from '../state/AuthContext';

interface Customer {
  id: number;
  name: string;
  code: string;
}

interface Company {
  id: number;
  name: string;
  code: string;
}

export const TenantSwitcher: React.FC = () => {
  const auth = useAuth();
  const isAdmin = auth.userRole === 'ADMIN';
  const { customerId, companyId, setCustomerId, setCompanyId } = useTenant();
  const [customers, setCustomers] = React.useState<Customer[]>([]);
  const [companies, setCompanies] = React.useState<Company[]>([]);

  React.useEffect(() => {
    if (!isAdmin) return;
    void api.get<Customer[]>('/customers').then(res => setCustomers(res.data));
  }, [isAdmin]);

  React.useEffect(() => {
    if (!isAdmin) return;
    if (!customerId) {
      setCompanies([]);
      return;
    }
    void api.get<Company[]>(`/customers/${customerId}/companies`).then(res => setCompanies(res.data));
  }, [customerId, isAdmin]);

  if (!isAdmin) {
    return (
      <div className="pill">
        <span>Your workspace</span>
      </div>
    );
  }

  return (
    <div className="row">
      <div className="field">
        <label>Customer</label>
        <select
          value={customerId ?? ''}
          onChange={e => setCustomerId(e.target.value ? Number(e.target.value) : null)}
        >
          <option value="">All customers</option>
          {customers.map(c => (
            <option key={c.id} value={c.id}>
              {c.name} ({c.code})
            </option>
          ))}
        </select>
      </div>
      <div className="field">
        <label>Company</label>
        <select
          value={companyId ?? ''}
          onChange={e => setCompanyId(e.target.value ? Number(e.target.value) : null)}
          disabled={!customerId}
        >
          <option value="">All companies</option>
          {companies.map(c => (
            <option key={c.id} value={c.id}>
              {c.name} ({c.code})
            </option>
          ))}
        </select>
      </div>
    </div>
  );
};

