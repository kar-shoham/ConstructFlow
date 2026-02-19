import React, { useEffect, useState } from 'react';
import { api } from '../../api/client';
import { useTenant } from '../../state/TenantContext';
import type { CompanyDto } from '../../api/types';

export const CompaniesPage: React.FC = () => {
  const { customerId } = useTenant();
  const [list, setList] = useState<CompanyDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [editing, setEditing] = useState<CompanyDto | null>(null);
  const [formOpen, setFormOpen] = useState(false);
  const [form, setForm] = useState({ name: '', code: '' });

  const load = () => {
    if (!customerId) {
      setList([]);
      setLoading(false);
      return;
    }
    setLoading(true);
    setError(null);
    api
      .get<CompanyDto[]>(`/customers/${customerId}/companies`)
      .then(res => setList(res.data))
      .catch(err => setError(err?.response?.data?.message ?? 'Failed to load companies'))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    load();
  }, [customerId]);

  const openCreate = () => {
    setEditing(null);
    setFormOpen(true);
    setForm({ name: '', code: '' });
  };

  const openEdit = (row: CompanyDto) => {
    setEditing(row);
    setFormOpen(true);
    setForm({ name: row.name ?? '', code: row.code ?? '' });
  };

  const save = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!customerId) return;
    setError(null);
    try {
      if (editing?.id) {
        await api.put(`/customers/${customerId}/companies/${editing.id}`, { ...form, id: editing.id, customerId });
      } else {
        await api.post(`/customers/${customerId}/companies`, { ...form, customerId });
      }
      load();
      setEditing(null);
      setFormOpen(false);
      setForm({ name: '', code: '' });
    } catch (err: any) {
      setError(err?.response?.data?.message ?? 'Save failed');
    }
  };

  const remove = async (id: number) => {
    if (!customerId || !window.confirm('Delete this company?')) return;
    setError(null);
    try {
      await api.delete(`/customers/${customerId}/companies/${id}`);
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
        <div className="content-title">Companies</div>
        <div className="content-subtitle muted">Select a customer in the top bar to view and manage companies.</div>
      </div>
    );
  }

  return (
    <div className="content-card">
      <div className="content-header">
        <div>
          <div className="content-title">Companies</div>
          <div className="content-subtitle">Customer: {customerId}</div>
        </div>
        <button type="button" className="btn btn-primary" onClick={openCreate}>
          Add company
        </button>
      </div>
      {error && <div className="error-text" style={{ marginBottom: 10 }}>{error}</div>}
      {formOpen && (
        <form className="panel" style={{ marginBottom: 16 }} onSubmit={save}>
          <div className="panel-title">{editing ? 'Edit company' : 'New company'}</div>
          <div className="form-grid">
            <div className="field">
              <label>Name</label>
              <input value={form.name} onChange={e => setForm(f => ({ ...f, name: e.target.value }))} placeholder="Branch A" required />
            </div>
            <div className="field">
              <label>Code</label>
              <input value={form.code} onChange={e => setForm(f => ({ ...f, code: e.target.value }))} placeholder="BR-A" required disabled={!!editing?.id} />
            </div>
          </div>
          <div className="row" style={{ marginTop: 12, gap: 8 }}>
            <button type="submit" className="btn btn-primary">Save</button>
            <button type="button" className="btn btn-ghost" onClick={() => { setEditing(null); setFormOpen(false); setForm({ name: '', code: '' }); }}>Cancel</button>
          </div>
        </form>
      )}
      {loading ? (
        <div className="muted">Loading…</div>
      ) : (
        <div style={{ overflowX: 'auto' }}>
          <table className="table">
            <thead>
              <tr><th>Code</th><th>Name</th><th></th></tr>
            </thead>
            <tbody>
              {list.map(row => (
                <tr key={row.id}>
                  <td><span className="badge badge-muted">{row.code}</span></td>
                  <td>{row.name}</td>
                  <td>
                    <button type="button" className="btn btn-ghost" onClick={() => openEdit(row)}>Edit</button>
                    <button type="button" className="btn btn-ghost btn-danger" onClick={() => row.id != null && remove(row.id)}>Delete</button>
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
