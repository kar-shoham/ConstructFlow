import React, { useEffect, useState } from 'react';
import { api } from '../../api/client';
import { useTenant } from '../../state/TenantContext';
import type { CostCodeDto } from '../../api/types';

export const CostCodesPage: React.FC = () => {
  const { customerId } = useTenant();
  const [list, setList] = useState<CostCodeDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [editing, setEditing] = useState<CostCodeDto | null>(null);
  const [formOpen, setFormOpen] = useState(false);
  const [form, setForm] = useState({ name: '', code: '', costCodeStatus: 'NOT_STARTED' as CostCodeDto['costCodeStatus'], parentId: '' });

  const load = () => {
    if (!customerId) {
      setList([]);
      setLoading(false);
      return;
    }
    setLoading(true);
    setError(null);
    api
      .get<CostCodeDto[]>(`/customers/${customerId}/cost-codes`)
      .then(res => setList(res.data))
      .catch(err => setError(err?.response?.data?.message ?? 'Failed to load cost codes'))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    load();
  }, [customerId]);

  const openCreate = () => {
    setEditing(null);
    setFormOpen(true);
    setForm({ name: '', code: '', costCodeStatus: 'NOT_STARTED', parentId: '' });
  };

  const openEdit = (row: CostCodeDto) => {
    setEditing(row);
    setFormOpen(true);
    setForm({
      name: row.name ?? '',
      code: row.code ?? '',
      costCodeStatus: row.costCodeStatus ?? 'NOT_STARTED',
      parentId: row.parentId != null ? String(row.parentId) : ''
    });
  };

  const save = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!customerId) return;
    setError(null);
    const payload = {
      name: form.name,
      code: form.code,
      costCodeStatus: form.costCodeStatus,
      customerId,
      parentId: form.parentId ? Number(form.parentId) : undefined
    };
    try {
      if (editing?.id) {
        await api.put(`/customers/${customerId}/cost-codes/${editing.id}`, { ...payload, id: editing.id });
      } else {
        await api.post(`/customers/${customerId}/cost-codes`, payload);
      }
      load();
      setEditing(null);
      setFormOpen(false);
      setForm({ name: '', code: '', costCodeStatus: 'NOT_STARTED', parentId: '' });
    } catch (err: any) {
      setError(err?.response?.data?.message ?? 'Save failed');
    }
  };

  const remove = async (id: number) => {
    if (!customerId || !window.confirm('Delete this cost code?')) return;
    setError(null);
    try {
      await api.delete(`/customers/${customerId}/cost-codes/${id}`);
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
        <div className="content-title">Cost codes</div>
        <div className="content-subtitle muted">Select a customer in the top bar to manage cost codes.</div>
      </div>
    );
  }

  return (
    <div className="content-card">
      <div className="content-header">
        <div>
          <div className="content-title">Cost codes</div>
          <div className="content-subtitle">Customer: {customerId}. Add these to project budgets to use in timesheets.</div>
        </div>
        <button type="button" className="btn btn-primary" onClick={openCreate}>Add cost code</button>
      </div>
      {error && <div className="error-text" style={{ marginBottom: 10 }}>{error}</div>}
      {formOpen && (
        <form className="panel" style={{ marginBottom: 16 }} onSubmit={save}>
          <div className="panel-title">{editing ? 'Edit cost code' : 'New cost code'}</div>
          <div className="form-grid">
            <div className="field">
              <label>Name</label>
              <input value={form.name} onChange={e => setForm(f => ({ ...f, name: e.target.value }))} required />
            </div>
            <div className="field">
              <label>Code</label>
              <input value={form.code} onChange={e => setForm(f => ({ ...f, code: e.target.value }))} required disabled={!!editing?.id} />
            </div>
            <div className="field">
              <label>Status</label>
              <select value={form.costCodeStatus} onChange={e => setForm(f => ({ ...f, costCodeStatus: e.target.value as CostCodeDto['costCodeStatus'] }))}>
                <option value="NOT_STARTED">Not started</option>
                <option value="IN_PROGRESS">In progress</option>
                <option value="COMPLETED">Completed</option>
              </select>
            </div>
            <div className="field">
              <label>Parent ID (optional)</label>
              <input type="number" value={form.parentId} onChange={e => setForm(f => ({ ...f, parentId: e.target.value }))} placeholder="Leave empty" />
            </div>
          </div>
          <div className="row" style={{ marginTop: 12, gap: 8 }}>
            <button type="submit" className="btn btn-primary">Save</button>
            <button type="button" className="btn btn-ghost" onClick={() => { setEditing(null); setFormOpen(false); setForm({ name: '', code: '', costCodeStatus: 'NOT_STARTED', parentId: '' }); }}>Cancel</button>
          </div>
        </form>
      )}
      {loading ? (
        <div className="muted">Loading…</div>
      ) : (
        <div style={{ overflowX: 'auto' }}>
          <table className="table">
            <thead>
              <tr><th>Code</th><th>Name</th><th>Status</th><th></th></tr>
            </thead>
            <tbody>
              {list.map(row => (
                <tr key={row.id}>
                  <td><span className="badge badge-muted">{row.code}</span></td>
                  <td>{row.name}</td>
                  <td><span className="badge badge-accent">{row.costCodeStatus ?? 'NOT_STARTED'}</span></td>
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
