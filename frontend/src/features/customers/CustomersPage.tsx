import React, { useEffect, useState } from 'react';
import { api } from '../../api/client';
import type { CustomerDto } from '../../api/types';

export const CustomersPage: React.FC = () => {
  const [list, setList] = useState<CustomerDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [editing, setEditing] = useState<CustomerDto | null>(null);
  const [formOpen, setFormOpen] = useState(false);
  const [form, setForm] = useState({ name: '', code: '' });

  const load = () => {
    setLoading(true);
    setError(null);
    api
      .get<CustomerDto[]>('/customers')
      .then(res => setList(res.data))
      .catch(err => setError(err?.response?.data?.message ?? 'Failed to load customers'))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    load();
  }, []);

  const openCreate = () => {
    setEditing(null);
    setFormOpen(true);
    setForm({ name: '', code: '' });
  };

  const openEdit = (row: CustomerDto) => {
    setEditing(row);
    setFormOpen(true);
    setForm({ name: row.name ?? '', code: row.code ?? '' });
  };

  const save = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    try {
      if (editing?.id) {
        await api.put(`/customers/${editing.id}`, { ...form, id: editing.id });
      } else {
        await api.post('/customers', form);
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
    if (!window.confirm('Delete this customer?')) return;
    setError(null);
    try {
      await api.delete(`/customers/${id}`);
      load();
      setEditing(null);
      setFormOpen(false);
    } catch (err: any) {
      setError(err?.response?.data?.message ?? 'Delete failed');
    }
  };

  return (
    <div className="content-card">
      <div className="content-header">
        <div>
          <div className="content-title">Customers</div>
          <div className="content-subtitle">Create and manage customers (admin).</div>
        </div>
        <button type="button" className="btn btn-primary" onClick={openCreate}>
          Add customer
        </button>
      </div>
      {error && <div className="error-text" style={{ marginBottom: 10 }}>{error}</div>}
      {formOpen && (
        <form className="panel" style={{ marginBottom: 16 }} onSubmit={save}>
          <div className="panel-title">{editing ? 'Edit customer' : 'New customer'}</div>
          <div className="form-grid">
            <div className="field">
              <label>Name</label>
              <input
                value={form.name}
                onChange={e => setForm(f => ({ ...f, name: e.target.value }))}
                placeholder="Acme Corp"
                required
              />
            </div>
            <div className="field">
              <label>Code</label>
              <input
                value={form.code}
                onChange={e => setForm(f => ({ ...f, code: e.target.value }))}
                placeholder="ACME"
                required
                disabled={!!editing?.id}
              />
            </div>
          </div>
          <div className="row" style={{ marginTop: 12, gap: 8 }}>
            <button type="submit" className="btn btn-primary">Save</button>
            <button type="button" className="btn btn-ghost" onClick={() => { setEditing(null); setFormOpen(false); setForm({ name: '', code: '' }); }}>
              Cancel
            </button>
          </div>
        </form>
      )}
      {loading ? (
        <div className="muted">Loading…</div>
      ) : (
        <div style={{ overflowX: 'auto' }}>
          <table className="table">
            <thead>
              <tr>
                <th>Code</th>
                <th>Name</th>
                <th></th>
              </tr>
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
