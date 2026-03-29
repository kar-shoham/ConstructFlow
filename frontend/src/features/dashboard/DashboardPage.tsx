import React from 'react';
import { Link } from 'react-router-dom';
import { useTenant } from '../../state/TenantContext';

const QUICK_LINKS = [
  { to: '/customers',       label: 'Customers' },
  { to: '/companies',       label: 'Companies' },
  { to: '/projects',        label: 'Projects' },
  { to: '/employees',       label: 'Employees' },
  { to: '/tasks',           label: 'Tasks' },
  { to: '/cost-codes',      label: 'Cost Codes' },
  { to: '/project-budgets', label: 'Project Budgets' },
  { to: '/timesheets',      label: 'Timesheets' },
] as const;

export const DashboardPage: React.FC = () => {
  const { customerId, companyId } = useTenant();

  return (
    <div className="content-card">
      <div className="content-header">
        <div>
          <div className="content-title">Workspace Overview</div>
          <div className="content-subtitle">
            {customerId
              ? `Customer ${customerId}${companyId ? ` · Company ${companyId}` : ''} selected. Use the sidebar or links below.`
              : 'Select a customer (and optionally company) in the top bar to work with projects, employees, and timesheets.'}
          </div>
        </div>
      </div>

      <div className="grid-two">
        <div className="panel">
          <div className="panel-header">
            <span className="panel-title">Quick Navigation</span>
          </div>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 6 }}>
            {QUICK_LINKS.map(({ to, label }) => (
              <Link
                key={to}
                to={to}
                style={{
                  display: 'flex',
                  alignItems: 'center',
                  padding: '9px 12px',
                  borderRadius: 8,
                  fontSize: 14,
                  fontWeight: 500,
                  color: 'var(--color-text)',
                  border: '1px solid var(--color-border)',
                  background: 'var(--color-surface)',
                  transition: 'background 150ms, border-color 150ms',
                }}
                onMouseEnter={e => {
                  (e.currentTarget as HTMLElement).style.background = 'var(--color-accent-light)';
                  (e.currentTarget as HTMLElement).style.borderColor = 'var(--color-accent-border)';
                  (e.currentTarget as HTMLElement).style.color = 'var(--color-accent)';
                }}
                onMouseLeave={e => {
                  (e.currentTarget as HTMLElement).style.background = 'var(--color-surface)';
                  (e.currentTarget as HTMLElement).style.borderColor = 'var(--color-border)';
                  (e.currentTarget as HTMLElement).style.color = 'var(--color-text)';
                }}
              >
                {label}
              </Link>
            ))}
          </div>
        </div>

        <div className="panel">
          <div className="panel-header">
            <span className="panel-title">Tips</span>
          </div>
          <ul style={{ margin: 0, paddingLeft: 18, fontSize: 13, color: 'var(--color-text-muted)', lineHeight: 1.7 }}>
            <li>Create customers and companies first, then projects and employees.</li>
            <li>Tasks belong to a project; add cost codes to tasks via Project Budgets.</li>
            <li>Timesheets: pick a project, then a task for that project, then a cost code from that task's budget.</li>
          </ul>
        </div>
      </div>
    </div>
  );
};
