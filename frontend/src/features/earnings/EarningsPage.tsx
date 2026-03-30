import React, { useEffect, useState } from 'react';
import { payrollApi } from '../../api/client';
import { useTenant } from '../../state/TenantContext';
import { useCurrentEmployee } from '../../state/CurrentEmployeeContext';
import { useAuth } from '../../state/AuthContext';
import type { EarningDto, EmployeeDto } from '../../api/types';
import { api } from '../../api/client';

export const EarningsPage: React.FC = () => {
  const auth = useAuth();
  const { customerId, companyId } = useTenant();
  const { currentEmployee } = useCurrentEmployee();
  const [earnings, setEarnings] = useState<EarningDto[]>([]);
  const [employees, setEmployees] = useState<EmployeeDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [filterEmployeeId, setFilterEmployeeId] = useState<string>('');

  const isAdmin        = auth.userRole === 'ADMIN';
  const isWorker       = currentEmployee?.employeeRole === 'WORKER';
  const isCompanyAdmin = currentEmployee?.employeeRole === 'COMPANY_ADMIN';
  const isCustomerAdmin = currentEmployee?.employeeRole === 'CUSTOMER_ADMIN';

  useEffect(() => {
    if (!customerId) return;
    if (isAdmin) {
      api.get<EmployeeDto[]>(`/customers/${customerId}/employees`).then(res => setEmployees(res.data));
    } else if (isCompanyAdmin && companyId) {
      api.get<EmployeeDto[]>(`/customers/${customerId}/companies/${companyId}/employees`).then(res => setEmployees(res.data));
    } else if (isCustomerAdmin) {
      api.get<EmployeeDto[]>(`/customers/${customerId}/employees`).then(res => setEmployees(res.data));
    } else {
      setEmployees([]);
    }
  }, [customerId, companyId, isAdmin, isCompanyAdmin, isCustomerAdmin]);

  const load = () => {
    if (!customerId) { setEarnings([]); setLoading(false); return; }
    setLoading(true);
    setError(null);

    const effectiveEmployeeId = isWorker && currentEmployee ? String(currentEmployee.id) : filterEmployeeId;
    const endpoint = effectiveEmployeeId
      ? `/customers/${customerId}/earnings/employees/${effectiveEmployeeId}`
      : `/customers/${customerId}/earnings`;

    payrollApi
      .get<EarningDto[]>(endpoint)
      .then(res => {
        let filtered = res.data;
        // Filter by company if company admin
        if (isCompanyAdmin && companyId && employees.length > 0) {
          const companyEmployeeIds = new Set(
            employees.filter(e => e.companyId === companyId).map(e => e.id)
          );
          filtered = filtered.filter(earning => companyEmployeeIds.has(earning.employeeId));
        }
        setEarnings(filtered);
      })
      .catch(err => setError(err?.response?.data?.message ?? 'Failed to load earnings'))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    load();
  }, [customerId, filterEmployeeId, isWorker, currentEmployee?.id, employees]);

  const formatCurrency = (amount: number): string => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
    }).format(amount);
  };

  const formatHours = (hours: number): string => {
    const h = Math.floor(hours);
    const m = Math.round((hours - h) * 60);
    return m > 0 ? `${h}h ${m}m` : `${h}h`;
  };

  const totalGrossAmount = earnings.reduce((sum, e) => sum + e.grossAmount, 0);

  if (!customerId) {
    return (
      <div className="content-card">
        <div className="content-title">Salary Breakdown</div>
        <div className="content-subtitle muted">
          {currentEmployee ? 'Loading your workspace…' : 'Select a customer in the top bar to view earnings.'}
        </div>
      </div>
    );
  }

  return (
    <div className="content-card">
      <div className="content-header">
        <div>
          <div className="content-title">Salary Breakdown</div>
          <div className="content-subtitle">View earnings from approved timesheets</div>
        </div>
      </div>

      {/* Filters */}
      {!isWorker && (
        <div className="form-grid" style={{ marginBottom: 14 }}>
          <div className="field">
            <label>Filter: employee</label>
            <select value={filterEmployeeId} onChange={e => setFilterEmployeeId(e.target.value)}>
              <option value="">All</option>
              {employees.map(e => (
                <option key={e.id} value={e.id}>{e.firstName} {e.lastName}</option>
              ))}
            </select>
          </div>
        </div>
      )}

      {error && <div className="error-text" style={{ marginBottom: 10 }}>{error}</div>}

      {/* List */}
      {loading ? (
        <div className="muted" style={{ fontSize: 14 }}>Loading…</div>
      ) : (
        <div style={{ overflowX: 'auto' }}>
          <table className="table">
            <thead>
              <tr>
                {!isWorker && <th>Employee</th>}
                <th>Timesheet ID</th>
                <th>Pay Rate</th>
                <th>Hours Worked</th>
                <th>Gross Amount</th>
                <th>Status</th>
                <th>Created</th>
              </tr>
            </thead>
            <tbody>
              {earnings.map(row => {
                const emp = employees.find(e => e.id === row.employeeId);
                const empName = emp
                  ? `${emp.firstName} ${emp.lastName}`
                  : (row.employeeId === currentEmployee?.id)
                    ? `${currentEmployee.firstName} ${currentEmployee.lastName}`
                    : row.employeeId;
                return (
                  <tr key={row.id}>
                    {!isWorker && <td style={{ fontWeight: 500 }}>{empName}</td>}
                    <td className="muted">#{row.timesheetId}</td>
                    <td>{formatCurrency(row.payRate)}</td>
                    <td>{formatHours(row.hoursWorked)}</td>
                    <td><span className="badge badge-green" style={{ fontWeight: 'bold' }}>{formatCurrency(row.grossAmount)}</span></td>
                    <td>
                      <span className={`badge ${row.status === 'COMPLETED' ? 'badge-green' : 'badge-red'}`}>
                        {row.status || 'COMPLETED'}
                      </span>
                    </td>
                    <td className="muted" style={{ fontSize: 12 }}>
                      {row.createdOn ? new Date(row.createdOn).toLocaleDateString() : '—'}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
          {earnings.length === 0 && <div className="muted" style={{ padding: '20px 0', fontSize: 14 }}>No earnings found.</div>}
          {earnings.length > 0 && (
            <div style={{ marginTop: 16, paddingTop: 12, borderTop: '1px solid #e0e0e0' }}>
              <strong>Total Gross Amount: {formatCurrency(totalGrossAmount)}</strong>
            </div>
          )}
        </div>
      )}
    </div>
  );
};
