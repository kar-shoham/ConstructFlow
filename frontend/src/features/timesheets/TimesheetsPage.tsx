import React, { useEffect, useState } from 'react';
import { api, timesheetApi } from '../../api/client';
import { useTenant } from '../../state/TenantContext';
import { useAuth } from '../../state/AuthContext';
import { useCurrentEmployee } from '../../state/CurrentEmployeeContext';
import type { TimesheetDto, EmployeeDto, ProjectDto, TaskDto, ProjectBudgetDto } from '../../api/types';
import { secondsToIsoDuration, parseDurationToSeconds } from '../../api/types';

function formatSeconds(s: number): string {
  const h = Math.floor(s / 3600);
  const m = Math.floor((s % 3600) / 60);
  return `${h}h ${m}m`;
}

export const TimesheetsPage: React.FC = () => {
  const auth = useAuth();
  const { customerId, companyId } = useTenant();
  const { currentEmployee } = useCurrentEmployee();
  const [list, setList] = useState<TimesheetDto[]>([]);
  const [employees, setEmployees] = useState<EmployeeDto[]>([]);
  const [projects, setProjects] = useState<ProjectDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [editing, setEditing] = useState<TimesheetDto | null>(null);
  const [formOpen, setFormOpen] = useState(false);
  const [form, setForm] = useState<Partial<TimesheetDto>>({
    employeeId: undefined, projectId: undefined, taskId: undefined, costCodeId: undefined,
    seconds: 0, dateWorked: new Date().toISOString().slice(0, 10), startTime: '09:00:00'
  });
  const [tasks, setTasks] = useState<TaskDto[]>([]);
  const [budgets, setBudgets] = useState<ProjectBudgetDto[]>([]);
  const [filterEmployeeId, setFilterEmployeeId] = useState<string>('');
  const [filterStart, setFilterStart] = useState('');
  const [filterEnd, setFilterEnd] = useState('');

  const isAdmin = auth.userRole === 'ADMIN';
  const isWorker = currentEmployee?.employeeRole === 'WORKER';
  const isCompanyAdmin = currentEmployee?.employeeRole === 'COMPANY_ADMIN';
  const isCustomerAdmin = currentEmployee?.employeeRole === 'CUSTOMER_ADMIN';

  // Load employees: ADMIN = all for customer; COMPANY_ADMIN = only their company; CUSTOMER_ADMIN = all for customer; WORKER = just self (no list needed for selector)
  useEffect(() => {
    if (!customerId) return;
    api.get<ProjectDto[]>(`/customers/${customerId}/projects`).then(res => setProjects(res.data));
    if (isAdmin) {
      api.get<EmployeeDto[]>(`/customers/${customerId}/employees`).then(res => setEmployees(res.data));
    } else if (isCompanyAdmin && companyId) {
      api.get<EmployeeDto[]>(`/customers/${customerId}/companies/${companyId}/employees`).then(res => setEmployees(res.data));
    } else if (isCustomerAdmin || isWorker) {
      api.get<EmployeeDto[]>(`/customers/${customerId}/employees`).then(res => setEmployees(res.data));
    } else {
      setEmployees([]);
    }
  }, [customerId, companyId, isAdmin, isCompanyAdmin, isCustomerAdmin, isWorker]);

  const load = () => {
    if (!customerId) {
      setList([]);
      setLoading(false);
      return;
    }
    setLoading(true);
    setError(null);
    const params = new URLSearchParams();
    // Worker only sees their own timesheets
    const effectiveEmployeeFilter = isWorker && currentEmployee ? String(currentEmployee.id) : filterEmployeeId;
    if (effectiveEmployeeFilter) params.set('employee-id', effectiveEmployeeFilter);
    if (filterStart) params.set('start-date', filterStart);
    if (filterEnd) params.set('end-date', filterEnd);
    timesheetApi
      .get<TimesheetDto[]>(`/customers/${customerId}/timesheets?${params.toString()}`)
      .then(res => setList(res.data))
      .catch(err => setError(err?.response?.data?.message ?? 'Failed to load timesheets'))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    load();
  }, [customerId, filterEmployeeId, filterStart, filterEnd, isWorker, currentEmployee?.id]);

  useEffect(() => {
    if (!customerId || !form.projectId) {
      setTasks([]);
      setBudgets([]);
      return;
    }
    api.get<TaskDto[]>(`/customers/${customerId}/projects/${form.projectId}/tasks`).then(res => setTasks(res.data));
    api.get<ProjectBudgetDto[]>(`/customers/${customerId}/projects/${form.projectId}/project-budgets`).then(res => setBudgets(res.data));
  }, [customerId, form.projectId]);

  const budgetsForTask = form.taskId
    ? budgets.filter(b => b.taskId === form.taskId)
    : [];

  const openCreate = () => {
    setEditing(null);
    setFormOpen(true);
    const defaultEmployeeId = isWorker && currentEmployee ? currentEmployee.id : undefined;
    setForm({
      employeeId: defaultEmployeeId, projectId: undefined, taskId: undefined, costCodeId: undefined,
      seconds: 0, dateWorked: new Date().toISOString().slice(0, 10), startTime: '09:00:00'
    });
  };

  const openEdit = (row: TimesheetDto) => {
    setEditing(row);
    setFormOpen(true);
    const startTimeRaw = typeof row.startTime === 'string' ? String(row.startTime) : '09:00';
    const startTimeForm = startTimeRaw.length >= 8 ? startTimeRaw.slice(0, 8) : startTimeRaw.length >= 5 ? `${startTimeRaw.slice(0, 5)}:00` : '09:00:00';
    setForm({
      employeeId: row.employeeId,
      projectId: row.projectId,
      taskId: row.taskId,
      costCodeId: row.costCodeId,
      seconds: parseDurationToSeconds(row.seconds as string | number),
      dateWorked: typeof row.dateWorked === 'string' ? row.dateWorked.slice(0, 10) : new Date().toISOString().slice(0, 10),
      startTime: startTimeForm
    });
  };

  const save = async (e: React.FormEvent) => {
    e.preventDefault();
    const effectiveEmployeeId = isWorker && currentEmployee ? currentEmployee.id : form.employeeId;
    if (!customerId || effectiveEmployeeId == null || form.projectId == null || form.seconds == null || !form.dateWorked || !form.startTime) {
      setError('Fill required fields: employee, project, seconds, date, start time.');
      return;
    }
    setError(null);
    const startTimeNormalized = form.startTime!.includes(':') && form.startTime!.split(':').length >= 3
      ? form.startTime!
      : `${form.startTime!.padStart(5, '0')}:00`;
    const payload: TimesheetDto = {
      customerId,
      employeeId: effectiveEmployeeId,
      projectId: form.projectId,
      taskId: form.taskId ?? undefined,
      costCodeId: form.costCodeId ?? undefined,
      seconds: secondsToIsoDuration(Number(form.seconds)),
      dateWorked: form.dateWorked!,
      startTime: startTimeNormalized
    };
    try {
      if (editing?.id) {
        await timesheetApi.put(`/customers/${customerId}/timesheets/${editing.id}`, payload);
      } else {
        await timesheetApi.post(`/customers/${customerId}/timesheets`, payload);
      }
      load();
      setEditing(null);
      setFormOpen(false);
      setForm({
        employeeId: undefined, projectId: undefined, taskId: undefined, costCodeId: undefined,
        seconds: 0, dateWorked: new Date().toISOString().slice(0, 10), startTime: '09:00:00'
      });
    } catch (err: any) {
      setError(err?.response?.data?.message ?? 'Save failed');
    }
  };

  const remove = async (id: number) => {
    if (!customerId || !window.confirm('Delete this timesheet entry?')) return;
    setError(null);
    try {
      await timesheetApi.delete(`/customers/${customerId}/timesheets/${id}`);
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
        <div className="content-title">Timesheets</div>
        <div className="content-subtitle muted">
          {currentEmployee ? 'Loading your workspace…' : 'Select a customer in the top bar to view and create timesheets.'}
        </div>
      </div>
    );
  }

  return (
    <div className="content-card">
      <div className="content-header">
        <div>
          <div className="content-title">Timesheets</div>
          <div className="content-subtitle">Task and cost code must belong to the project; cost code must be on the task’s project budget.</div>
        </div>
        <button type="button" className="btn btn-primary" onClick={openCreate}>Add timesheet</button>
      </div>
      <div className="form-grid" style={{ marginBottom: 14 }}>
        {!isWorker && (
          <div className="field">
            <label>Filter: employee</label>
            <select value={filterEmployeeId} onChange={e => setFilterEmployeeId(e.target.value)}>
              <option value="">All</option>
              {employees.map(e => (
                <option key={e.id} value={e.id}>{e.firstName} {e.lastName}</option>
              ))}
            </select>
          </div>
        )}
        <div className="field">
          <label>Start date</label>
          <input type="date" value={filterStart} onChange={e => setFilterStart(e.target.value)} />
        </div>
        <div className="field">
          <label>End date</label>
          <input type="date" value={filterEnd} onChange={e => setFilterEnd(e.target.value)} />
        </div>
      </div>
      {error && <div className="error-text" style={{ marginBottom: 10 }}>{error}</div>}
      {formOpen && (
        <form className="panel" style={{ marginBottom: 16 }} onSubmit={save}>
          <div className="panel-title">{editing ? 'Edit timesheet' : 'New timesheet'}</div>
          <div className="form-grid">
            {isWorker ? (
              <div className="field">
                <label>Employee</label>
                <div className="pill pill-strong">
                  {currentEmployee ? `${currentEmployee.firstName} ${currentEmployee.lastName} (you)` : '—'}
                </div>
              </div>
            ) : (
              <div className="field">
                <label>Employee</label>
                <select value={form.employeeId ?? ''} onChange={e => setForm(f => ({ ...f, employeeId: e.target.value ? Number(e.target.value) : undefined }))} required>
                  <option value="">Select</option>
                  {employees.map(e => (
                    <option key={e.id} value={e.id}>{e.firstName} {e.lastName}</option>
                  ))}
                </select>
              </div>
            )}
            <div className="field">
              <label>Project</label>
              <select value={form.projectId ?? ''} onChange={e => setForm(f => ({ ...f, projectId: e.target.value ? Number(e.target.value) : undefined, taskId: undefined, costCodeId: undefined }))} required>
                <option value="">Select</option>
                {projects.map(p => (
                  <option key={p.id} value={p.id}>{p.name} ({p.code})</option>
                ))}
              </select>
            </div>
            <div className="field">
              <label>Task (optional, from project)</label>
              <select value={form.taskId ?? ''} onChange={e => setForm(f => ({ ...f, taskId: e.target.value ? Number(e.target.value) : undefined, costCodeId: undefined }))}>
                <option value="">None</option>
                {tasks.map(t => (
                  <option key={t.id} value={t.id}>{t.name} ({t.code})</option>
                ))}
              </select>
            </div>
            <div className="field">
              <label>Cost code (from task budget)</label>
              <select value={form.costCodeId ?? ''} onChange={e => setForm(f => ({ ...f, costCodeId: e.target.value ? Number(e.target.value) : undefined }))}>
                <option value="">None</option>
                {budgetsForTask.map(b => (
                  <option key={b.costCodeId} value={b.costCodeId}>{b.costCode ?? b.costCodeId}</option>
                ))}
              </select>
            </div>
            <div className="field">
              <label>Date worked</label>
              <input type="date" value={form.dateWorked ?? ''} onChange={e => setForm(f => ({ ...f, dateWorked: e.target.value }))} required />
            </div>
            <div className="field">
              <label>Start time</label>
              <input type="time" value={form.startTime ?? '09:00:00'} step="1" onChange={e => setForm(f => ({ ...f, startTime: e.target.value.length >= 8 ? e.target.value : `${e.target.value}:00` }))} required />
            </div>
            <div className="field">
              <label>Duration (seconds)</label>
              <input type="number" min={0} value={form.seconds ?? 0} onChange={e => setForm(f => ({ ...f, seconds: Number(e.target.value) }))} required />
            </div>
          </div>
          <div className="row" style={{ marginTop: 12, gap: 8 }}>
            <button type="submit" className="btn btn-primary">Save</button>
            <button type="button" className="btn btn-ghost" onClick={() => { setEditing(null); setFormOpen(false); }}>Cancel</button>
          </div>
        </form>
      )}
      {loading ? (
        <div className="muted">Loading…</div>
      ) : (
        <div style={{ overflowX: 'auto' }}>
          <table className="table">
            <thead>
              <tr><th>Employee</th><th>Project</th><th>Date</th><th>Start</th><th>Duration</th><th></th></tr>
            </thead>
            <tbody>
              {list.map(row => {
                const emp = employees.find(e => e.id === row.employeeId);
                const proj = projects.find(p => p.id === row.projectId);
                const sec = parseDurationToSeconds(row.seconds as string | number);
                return (
                  <tr key={row.id}>
                    <td>{emp ? `${emp.firstName} ${emp.lastName}` : row.employeeId}</td>
                    <td>{proj ? proj.name : row.projectId}</td>
                    <td>{typeof row.dateWorked === 'string' ? row.dateWorked.slice(0, 10) : String(row.dateWorked)}</td>
                    <td>{String(row.startTime).slice(0, 5)}</td>
                    <td>{formatSeconds(sec)}</td>
                    <td>
                      <button type="button" className="btn btn-ghost" onClick={() => openEdit(row)}>Edit</button>
                      <button type="button" className="btn btn-ghost btn-danger" onClick={() => row.id != null && remove(row.id)}>Delete</button>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
          {list.length === 0 && <div className="muted">No timesheets. Use filters or add one above.</div>}
        </div>
      )}
    </div>
  );
};
