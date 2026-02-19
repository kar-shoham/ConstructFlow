import React from 'react';
import { Link } from 'react-router-dom';
import { useTenant } from '../../state/TenantContext';

const QUICK_LINKS = [
  { to: '/customers', label: 'Customers' },
  { to: '/companies', label: 'Companies' },
  { to: '/projects', label: 'Projects' },
  { to: '/employees', label: 'Employees' },
  { to: '/tasks', label: 'Tasks' },
  { to: '/cost-codes', label: 'Cost Codes' },
  { to: '/project-budgets', label: 'Project Budgets' },
  { to: '/timesheets', label: 'Timesheets' }
] as const;

export const DashboardPage: React.FC = () => {
  const { customerId, companyId } = useTenant();

  return (
    <div className="content-card">
      <div className="content-header">
        <div>
          <div className="content-title">Workspace overview</div>
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
            <span className="panel-title">Quick navigation</span>
          </div>
          <div className="chip-row" style={{ flexDirection: 'column', gap: 10 }}>
            {QUICK_LINKS.map(({ to, label }) => (
              <Link key={to} to={to} className="sidebar-link" style={{ margin: 0 }}>
                {label}
              </Link>
            ))}
          </div>
        </div>
        <div className="panel">
          <div className="panel-header">
            <span className="panel-title">Tips</span>
          </div>
          <ul className="muted" style={{ margin: 0, paddingLeft: 18, fontSize: 13 }}>
            <li>Create customers and companies first, then projects and employees.</li>
            <li>Tasks belong to a project; add cost codes to tasks via Project Budgets.</li>
            <li>Timesheets: pick a project, then a task for that project, then a cost code from that task’s budget.</li>
          </ul>
        </div>
      </div>
    </div>
  );
};
