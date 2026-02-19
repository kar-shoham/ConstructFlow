import React, { useEffect, useState } from 'react';
import { api } from '../../api/client';
import { useTenant } from '../../state/TenantContext';
import type { TaskDto, ProjectDto } from '../../api/types';

export const TasksPage: React.FC = () => {
  const { customerId } = useTenant();
  const [projects, setProjects] = useState<ProjectDto[]>([]);
  const [projectId, setProjectId] = useState<number | null>(null);
  const [list, setList] = useState<TaskDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [editing, setEditing] = useState<TaskDto | null>(null);
  const [formOpen, setFormOpen] = useState(false);
  const [form, setForm] = useState({ name: '', code: '', taskStatus: 'NOT_STARTED' as TaskDto['taskStatus'] });

  useEffect(() => {
    if (!customerId) {
      setProjects([]);
      setProjectId(null);
      return;
    }
    api.get<ProjectDto[]>(`/customers/${customerId}/projects`).then(res => setProjects(res.data));
  }, [customerId]);

  const load = () => {
    if (!customerId || !projectId) {
      setList([]);
      setLoading(false);
      return;
    }
    setLoading(true);
    setError(null);
    api
      .get<TaskDto[]>(`/customers/${customerId}/projects/${projectId}/tasks`)
      .then(res => setList(res.data))
      .catch(err => setError(err?.response?.data?.message ?? 'Failed to load tasks'))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    load();
  }, [customerId, projectId]);

  const openCreate = () => {
    setEditing(null);
    setFormOpen(true);
    setForm({ name: '', code: '', taskStatus: 'NOT_STARTED' });
  };

  const openEdit = (row: TaskDto) => {
    setEditing(row);
    setFormOpen(true);
    setForm({ name: row.name ?? '', code: row.code ?? '', taskStatus: row.taskStatus ?? 'NOT_STARTED' });
  };

  const save = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!customerId || !projectId) return;
    setError(null);
    try {
      if (editing?.id) {
        await api.put(`/customers/${customerId}/projects/${projectId}/tasks/${editing.id}`, { ...form, id: editing.id, projectId });
      } else {
        await api.post(`/customers/${customerId}/projects/${projectId}/tasks`, { ...form, projectId });
      }
      load();
      setEditing(null);
      setFormOpen(false);
      setForm({ name: '', code: '', taskStatus: 'NOT_STARTED' });
    } catch (err: any) {
      setError(err?.response?.data?.message ?? 'Save failed');
    }
  };

  const remove = async (id: number) => {
    if (!customerId || !projectId || !window.confirm('Delete this task?')) return;
    setError(null);
    try {
      await api.delete(`/customers/${customerId}/projects/${projectId}/tasks/${id}`);
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
        <div className="content-title">Tasks</div>
        <div className="content-subtitle muted">Select a customer in the top bar.</div>
      </div>
    );
  }

  return (
    <div className="content-card">
      <div className="content-header">
        <div>
          <div className="content-title">Tasks</div>
          <div className="content-subtitle">Tasks belong to a project. Select a project below.</div>
        </div>
        <button type="button" className="btn btn-primary" onClick={openCreate} disabled={!projectId}>Add task</button>
      </div>
      <div className="field" style={{ maxWidth: 280, marginBottom: 14 }}>
        <label>Project</label>
        <select value={projectId ?? ''} onChange={e => setProjectId(e.target.value ? Number(e.target.value) : null)}>
          <option value="">Select project</option>
          {projects.map(p => (
            <option key={p.id} value={p.id}>{p.name} ({p.code})</option>
          ))}
        </select>
      </div>
      {error && <div className="error-text" style={{ marginBottom: 10 }}>{error}</div>}
      {formOpen && (
        <form className="panel" style={{ marginBottom: 16 }} onSubmit={save}>
          <div className="panel-title">{editing ? 'Edit task' : 'New task'}</div>
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
              <select value={form.taskStatus} onChange={e => setForm(f => ({ ...f, taskStatus: e.target.value as TaskDto['taskStatus'] }))}>
                <option value="NOT_STARTED">Not started</option>
                <option value="IN_PROGRESS">In progress</option>
                <option value="COMPLETED">Completed</option>
              </select>
            </div>
          </div>
          <div className="row" style={{ marginTop: 12, gap: 8 }}>
            <button type="submit" className="btn btn-primary">Save</button>
            <button type="button" className="btn btn-ghost" onClick={() => { setEditing(null); setFormOpen(false); setForm({ name: '', code: '', taskStatus: 'NOT_STARTED' }); }}>Cancel</button>
          </div>
        </form>
      )}
      {!projectId ? (
        <div className="muted">Select a project to list tasks.</div>
      ) : loading ? (
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
                  <td><span className="badge badge-accent">{row.taskStatus ?? 'NOT_STARTED'}</span></td>
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
