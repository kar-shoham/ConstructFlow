import React, { useEffect, useState } from 'react';
import { api } from '../../api/client';
import { useTenant } from '../../state/TenantContext';
import { useAuth } from '../../state/AuthContext';
import { useCurrentEmployee } from '../../state/CurrentEmployeeContext';
import type { EmployeeDto } from '../../api/types';

export const EmployeesPage: React.FC = () => {
  const auth = useAuth();
  const { customerId, companyId } = useTenant();
  const { currentEmployee } = useCurrentEmployee();
  const [list, setList] = useState<EmployeeDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [editing, setEditing] = useState<EmployeeDto | null>(null);
  const [formOpen, setFormOpen] = useState(false);
  const [form, setForm] = useState<Partial<EmployeeDto>>({
    firstName: '', lastName: '', username: '', email: '', password: '',
    payRate: undefined, employeeType: 'HOURLY', employeeRole: 'WORKER'
  });

  const isAdmin = auth.userRole === 'ADMIN';
  const isCustomerAdmin = currentEmployee?.employeeRole === 'CUSTOMER_ADMIN';
  const isCompanyAdmin = currentEmployee?.employeeRole === 'COMPANY_ADMIN';

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
    const url = isCustomerAdmin
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
    setForm({ firstName: '', lastName: '', username: '', email: '', password: '', payRate: undefined, employeeType: 'HOURLY', employeeRole: 'WORKER' });
  };

  const openEdit = (row: EmployeeDto) => {
    setEditing(row);
    setFormOpen(true);
    setForm({
      firstName: row.firstName ?? '', lastName: row.lastName ?? '', username: row.username ?? '', email: row.email ?? '',
      payRate: row.payRate, employeeType: row.employeeType ?? 'HOURLY', employeeRole: row.employeeRole ?? 'WORKER'
    });
  };

  const save = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!customerId || !companyId) return;
    setError(null);
    const payload = { ...form, companyId };
    if (editing?.id) delete (payload as any).password;
    try {
      if (editing?.id) {
        await api.put(`/customers/${customerId}/companies/${companyId}/employees/${editing.id}`, payload);
      } else {
        if (!form.password) {
          setError('Password is required for new employee');
          return;
        }
        await api.post(`/customers/${customerId}/companies/${companyId}/employees`, payload);
      }
      load();
      setEditing(null);
      setFormOpen(false);
      setForm({ firstName: '', lastName: '', username: '', email: '', password: '', payRate: undefined, employeeType: 'HOURLY', employeeRole: 'WORKER' });
    } catch (err: any) {
      setError(err?.response?.data?.message ?? 'Save failed');
    }
  };

  const remove = async (id: number) => {
    if (!customerId || !companyId || !window.confirm('Delete this employee?')) return;
    setError(null);
    try {
      await api.delete(`/customers/${customerId}/companies/${companyId}/employees/${id}`);
      load();
      setEditing(null);
      setFormOpen(false);
    } catch (err: any) {
      setError(err?.response?.data?.message ?? 'Delete failed');
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

  const canCreateEdit = isAdmin || isCompanyAdmin;

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
        <form className="panel" style={{ marginBottom: 16 }} onSubmit={save}>
          <div className="panel-title">{editing ? 'Edit employee' : 'New employee'}</div>
          <div className="form-grid">
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
                <input type="password" value={form.password ?? ''} onChange={e => setForm(f => ({ ...f, password: e.target.value }))} required />
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
                      <>
                        <button type="button" className="btn btn-ghost" onClick={() => openEdit(row)}>Edit</button>
                        <button type="button" className="btn btn-ghost btn-danger" onClick={() => row.id != null && remove(row.id)}>Delete</button>
                      </>
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
