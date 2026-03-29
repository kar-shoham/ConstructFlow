import React, { useEffect, useState } from 'react';
import { api } from '../../api/client';
import { useTenant } from '../../state/TenantContext';
import { useAuth } from '../../state/AuthContext';
import { useCurrentEmployee } from '../../state/CurrentEmployeeContext';
import type { EmployeeDto } from '../../api/types';

interface CompanyOption { id: number; name: string; code: string; }

export const EmployeesPage: React.FC = () => {
  const auth = useAuth();
  const { customerId, companyId } = useTenant();
  const { currentEmployee } = useCurrentEmployee();
  const [list, setList] = useState<EmployeeDto[]>([]);
  const [companies, setCompanies] = useState<CompanyOption[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [editing, setEditing] = useState<EmployeeDto | null>(null);
  const [formOpen, setFormOpen] = useState(false);
  const [formCompanyId, setFormCompanyId] = useState<number | null>(null);
  const [form, setForm] = useState<Partial<EmployeeDto>>({
    firstName: '', lastName: '', username: '', email: '', password: '',
    payRate: undefined, employeeType: 'HOURLY', employeeRole: 'WORKER'
  });

  const isAdmin = auth.userRole === 'ADMIN';
  const isCustomerAdmin = currentEmployee?.employeeRole === 'CUSTOMER_ADMIN';
  const isCompanyAdmin = currentEmployee?.employeeRole === 'COMPANY_ADMIN';

  // Load companies so admin can pick one in the form
  useEffect(() => {
    if (!(isAdmin || isCustomerAdmin) || !customerId) return;
    api.get<CompanyOption[]>(`/customers/${customerId}/companies`)
      .then(res => setCompanies(res.data))
      .catch(() => {});
  }, [isAdmin, isCustomerAdmin, customerId]);

  const load = () => {
    if (!customerId) {
      setList([]);
      setLoading(false);
      return;
    }
    if (!isAdmin && !isCustomerAdmin && !companyId) {
      setList([]);
      setLoading(false);
      return;
    }
    setLoading(true);
    setError(null);
    const url = (isAdmin || isCustomerAdmin)
      ? `/customers/${customerId}/employees`
      : `/customers/${customerId}/companies/${companyId}/employees`;
    api
      .get<EmployeeDto[]>(url)
      .then(res => setList(res.data))
      .catch(err => setError(err?.response?.data?.message ?? 'Failed to load employees'))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    load();
  }, [customerId, companyId, isAdmin, isCustomerAdmin, isCompanyAdmin]);

  const openCreate = () => {
    setEditing(null);
    setFormOpen(true);
    setFormCompanyId(companyId);
    setForm({ firstName: '', lastName: '', username: '', email: '', password: '', payRate: undefined, employeeType: 'HOURLY', employeeRole: 'WORKER' });
  };

  const openEdit = (row: EmployeeDto) => {
    setEditing(row);
    if (isCompanyAdmin && row.companyId !== companyId) {
      setError("Permission denied: You can only edit employees in your own company.");
      return;
    }
    setFormOpen(true);
    setFormCompanyId(row.companyId ?? companyId);
    setForm({
      firstName: row.firstName ?? '', lastName: row.lastName ?? '', username: row.username ?? '', email: row.email ?? '',
      payRate: row.payRate, employeeType: row.employeeType ?? 'HOURLY', employeeRole: row.employeeRole ?? 'WORKER'
    });
  };

  const save = async (e: React.FormEvent) => {
    e.preventDefault();
    const effectiveCompanyId = (isAdmin || isCustomerAdmin) ? (formCompanyId ?? companyId) : companyId;
    if (!customerId || !effectiveCompanyId) {
      setError('Select a company before saving.');
      return;
    }
    setError(null);
    const payload = { ...form, companyId: effectiveCompanyId };
    if (editing?.id) delete (payload as any).password;
    try {
      if (editing?.id) {
        await api.put(`/customers/${customerId}/companies/${effectiveCompanyId}/employees/${editing.id}`, payload);
      } else {
        if (!form.password) {
          setError('Password is required for new employee');
          return;
        }
        await api.post(`/customers/${customerId}/companies/${effectiveCompanyId}/employees`, payload);
      }
      load();
      setEditing(null);
      setFormOpen(false);
      setForm({ firstName: '', lastName: '', username: '', email: '', password: '', payRate: undefined, employeeType: 'HOURLY', employeeRole: 'WORKER' });
    } catch (err: any) {
      setError(err?.response?.data?.message ?? 'Save failed');
    }
  };

  if (!customerId) {
    return (
      <div className="content-card">
        <div className="content-title">Employees</div>
        <div className="content-subtitle muted">Select a customer and company in the top bar to manage employees.</div>
      </div>
    );
  }
  if (!isAdmin && !isCustomerAdmin && !companyId) {
    return (
      <div className="content-card">
        <div className="content-title">Employees</div>
        <div className="content-subtitle muted">Your workspace is loading…</div>
      </div>
    );
  }

  const canCreateEdit = isAdmin || isCustomerAdmin || isCompanyAdmin;

  return (
    <div className="content-card">
      <div className="content-header">
        <div>
          <div className="content-title">Employees</div>
          <div className="content-subtitle">
            {isCustomerAdmin ? `Customer: ${customerId} (all companies)` : `Customer: ${customerId} · Company: ${companyId}`}
          </div>
        </div>
        {canCreateEdit && (
          <button type="button" className="btn btn-primary" onClick={openCreate}>Add employee</button>
        )}
      </div>
      {error && <div className="error-text" style={{ marginBottom: 10 }}>{error}</div>}
      {formOpen && canCreateEdit && (
        <form className="panel" style={{ marginBottom: 16 }} onSubmit={save} autoComplete="off">
          <div className="panel-title">{editing ? 'Edit employee' : 'New employee'}</div>
          <div className="form-grid">
            {(isAdmin || isCustomerAdmin) && (
              <div className="field">
                <label>Company</label>
                <select
                  value={formCompanyId ?? ''}
                  onChange={e => setFormCompanyId(e.target.value ? Number(e.target.value) : null)}
                  required
                >
                  <option value="">Select company</option>
                  {companies.map(c => (
                    <option key={c.id} value={c.id}>{c.name} ({c.code})</option>
                  ))}
                </select>
              </div>
            )}
            <div className="field">
              <label>First name</label>
              <input value={form.firstName ?? ''} onChange={e => setForm(f => ({ ...f, firstName: e.target.value }))} required />
            </div>
            <div className="field">
              <label>Last name</label>
              <input value={form.lastName ?? ''} onChange={e => setForm(f => ({ ...f, lastName: e.target.value }))} required />
            </div>
            <div className="field">
              <label>Username</label>
              <input value={form.username ?? ''} onChange={e => setForm(f => ({ ...f, username: e.target.value }))} required disabled={!!editing?.id} />
            </div>
            <div className="field">
              <label>Email</label>
              <input type="email" value={form.email ?? ''} onChange={e => setForm(f => ({ ...f, email: e.target.value }))} required />
            </div>
            {!editing?.id && (
              <div className="field">
                <label>Password</label>
                <input 
                  type="text" 
                  className="no-password-manager"
                  value={form.password ?? ''} 
                  onChange={e => setForm(f => ({ ...f, password: e.target.value }))} 
                  required 
                  autoComplete="off" 
                />
              </div>
            )}
            <div className="field">
              <label>Pay rate</label>
              <input type="number" step="0.01" value={form.payRate ?? ''} onChange={e => setForm(f => ({ ...f, payRate: e.target.value ? Number(e.target.value) : undefined }))} />
            </div>
            <div className="field">
              <label>Type</label>
              <select value={form.employeeType ?? 'HOURLY'} onChange={e => setForm(f => ({ ...f, employeeType: e.target.value as EmployeeDto['employeeType'] }))}>
                <option value="HOURLY">Hourly</option>
                <option value="SALARIED">Salaried</option>
              </select>
            </div>
            <div className="field">
              <label>Role</label>
              <select value={form.employeeRole ?? 'WORKER'} onChange={e => setForm(f => ({ ...f, employeeRole: e.target.value as EmployeeDto['employeeRole'] }))}>
                <option value="WORKER">Worker</option>
                <option value="COMPANY_ADMIN">Company admin</option>
                <option value="CUSTOMER_ADMIN">Customer admin</option>
              </select>
            </div>
          </div>
          <div className="row" style={{ marginTop: 12, gap: 8 }}>
            <button type="submit" className="btn btn-primary">Save</button>
            <button type="button" className="btn btn-ghost" onClick={() => { setEditing(null); setFormOpen(false); setForm({}); }}>Cancel</button>
          </div>
        </form>
      )}
      {loading ? (
        <div className="muted">Loading…</div>
      ) : (
        <div style={{ overflowX: 'auto' }}>
          <table className="table">
            <thead>
              <tr><th>Name</th><th>Username</th><th>Email</th><th>Type</th><th>Role</th><th></th></tr>
            </thead>
            <tbody>
              {list.map(row => (
                <tr key={row.id}>
                  <td>{row.firstName} {row.lastName}</td>
                  <td>{row.username}</td>
                  <td>{row.email}</td>
                  <td><span className="badge badge-muted">{row.employeeType ?? 'HOURLY'}</span></td>
                  <td><span className="badge badge-accent">{row.employeeRole ?? 'WORKER'}</span></td>
                  <td>
                    {canCreateEdit && (
                      <div className="row" style={{ gap: 6, justifyContent: 'flex-end' }}>
                        <button type="button" className="btn btn-ghost" onClick={() => openEdit(row)}>Edit</button>
                      </div>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};
