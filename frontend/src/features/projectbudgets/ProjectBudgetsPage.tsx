import React, { useEffect, useState } from 'react';
import { api } from '../../api/client';
import { useTenant } from '../../state/TenantContext';
import type { ProjectBudgetDto, ProjectDto, TaskDto, CostCodeDto } from '../../api/types';

export const ProjectBudgetsPage: React.FC = () => {
  const { customerId } = useTenant();
  const [projectId, setProjectId] = useState<number | null>(null);
  const [projects, setProjects] = useState<ProjectDto[]>([]);
  const [tasks, setTasks] = useState<TaskDto[]>([]);
  const [costCodes, setCostCodes] = useState<CostCodeDto[]>([]);
  const [list, setList] = useState<ProjectBudgetDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [addTaskId, setAddTaskId] = useState<number | null>(null);
  const [addCostCodeId, setAddCostCodeId] = useState<number | null>(null);

  useEffect(() => {
    if (!customerId) return;
    api.get<ProjectDto[]>(`/customers/${customerId}/projects`).then(res => setProjects(res.data));
    api.get<CostCodeDto[]>(`/customers/${customerId}/cost-codes`).then(res => setCostCodes(res.data));
  }, [customerId]);

  useEffect(() => {
    if (!customerId || !projectId) {
      setTasks([]);
      setList([]);
      setLoading(false);
      return;
    }
    api.get<TaskDto[]>(`/customers/${customerId}/projects/${projectId}/tasks`).then(res => setTasks(res.data));
  }, [customerId, projectId]);

  const load = () => {
    if (!customerId || !projectId) {
      setList([]);
      setLoading(false);
      return;
    }
    setLoading(true);
    setError(null);
    api
      .get<ProjectBudgetDto[]>(`/customers/${customerId}/projects/${projectId}/project-budgets`)
      .then(res => setList(res.data))
      .catch(err => setError(err?.response?.data?.message ?? 'Failed to load project budgets'))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    load();
  }, [customerId, projectId]);

  const add = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!customerId || !projectId || !addTaskId || !addCostCodeId) return;
    setError(null);
    try {
      await api.post(`/customers/${customerId}/projects/${projectId}/tasks/${addTaskId}/cost-code/${addCostCodeId}/project-budgets/add`);
      load();
      setAddTaskId(null);
      setAddCostCodeId(null);
    } catch (err: any) {
      setError(err?.response?.data?.message ?? 'Add failed');
    }
  };

  const remove = async (taskId: number, costCodeId: number) => {
    if (!customerId || !projectId || !window.confirm('Remove this cost code from the task budget?')) return;
    setError(null);
    try {
      await api.post(`/customers/${customerId}/projects/${projectId}/tasks/${taskId}/cost-code/${costCodeId}/project-budgets/remove`);
      load();
    } catch (err: any) {
      setError(err?.response?.data?.message ?? 'Remove failed');
    }
  };

  if (!customerId) {
    return (
      <div className="content-card">
        <div className="content-title">Project budgets</div>
        <div className="content-subtitle muted">Select a customer in the top bar.</div>
      </div>
    );
  }

  return (
    <div className="content-card">
      <div className="content-header">
        <div>
          <div className="content-title">Project budgets</div>
          <div className="content-subtitle">Link cost codes to tasks. Only these cost codes can be used in timesheets for the task.</div>
        </div>
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
      {projectId && (
        <form className="panel" style={{ marginBottom: 16 }} onSubmit={add}>
          <div className="panel-title">Add cost code to task</div>
          <div className="form-grid">
            <div className="field">
              <label>Task</label>
              <select value={addTaskId ?? ''} onChange={e => setAddTaskId(e.target.value ? Number(e.target.value) : null)} required>
                <option value="">Select task</option>
                {tasks.map(t => (
                  <option key={t.id} value={t.id}>{t.name} ({t.code})</option>
                ))}
              </select>
            </div>
            <div className="field">
              <label>Cost code</label>
              <select value={addCostCodeId ?? ''} onChange={e => setAddCostCodeId(e.target.value ? Number(e.target.value) : null)} required>
                <option value="">Select cost code</option>
                {costCodes.map(c => (
                  <option key={c.id} value={c.id}>{c.name} ({c.code})</option>
                ))}
              </select>
            </div>
          </div>
          <button type="submit" className="btn btn-primary">Add to budget</button>
        </form>
      )}
      {!projectId ? (
        <div className="muted">Select a project to view and manage budgets.</div>
      ) : loading ? (
        <div className="muted">Loading…</div>
      ) : (
        <div style={{ overflowX: 'auto' }}>
          <table className="table">
            <thead>
              <tr><th>Task</th><th>Cost code</th><th></th></tr>
            </thead>
            <tbody>
              {list.map(row => (
                <tr key={`${row.taskId}-${row.costCodeId}`}>
                  <td><span className="badge badge-muted">{row.taskCode ?? row.taskId}</span></td>
                  <td><span className="badge badge-accent">{row.costCode ?? row.costCodeId}</span></td>
                  <td>
                    {row.taskId != null && row.costCodeId != null && (
                      <button type="button" className="btn btn-ghost btn-danger" onClick={() => remove(row.taskId!, row.costCodeId!)}>Remove</button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          {list.length === 0 && <div className="muted">No budget entries yet. Add a task + cost code above.</div>}
        </div>
      )}
    </div>
  );
};
