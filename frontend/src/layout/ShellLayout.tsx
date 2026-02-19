import React from 'react';
import { NavLink, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../state/AuthContext';
import { useCurrentEmployee } from '../state/CurrentEmployeeContext';
import { useTenant } from '../state/TenantContext';
import { TenantSwitcher } from './TenantSwitcher';

const NAV_ITEMS: { to: string; label: string; icon: string }[] = [
  { to: '/dashboard', label: 'Overview', icon: '◆' },
  { to: '/customers', label: 'Customers', icon: 'C' },
  { to: '/companies', label: 'Companies', icon: 'O' },
  { to: '/projects', label: 'Projects', icon: 'P' },
  { to: '/employees', label: 'Employees', icon: 'E' },
  { to: '/tasks', label: 'Tasks', icon: 'T' },
  { to: '/cost-codes', label: 'Cost Codes', icon: '$' },
  { to: '/project-budgets', label: 'Budgets', icon: 'B' },
  { to: '/timesheets', label: 'Timesheets', icon: '⏱' }
];

export const ShellLayout: React.FC = () => {
  const auth = useAuth();
  const location = useLocation();
  const { currentEmployee } = useCurrentEmployee();
  const { setCustomerId, setCompanyId } = useTenant();

  const isAdmin = auth.userRole === 'ADMIN';
  const employeeRole = currentEmployee?.employeeRole ?? null;

  // When logged-in user has an employee, fix tenant to their customer/company so downstream pages use correct scope.
  React.useEffect(() => {
    if (!currentEmployee) return;
    setCustomerId(currentEmployee.customerId);
    setCompanyId(currentEmployee.companyId);
  }, [currentEmployee, setCustomerId, setCompanyId]);

  const visibleNavItems = React.useMemo(() => {
    if (isAdmin) return NAV_ITEMS;
    // USER with employee: WORKER = only Overview + Timesheets; COMPANY_ADMIN / CUSTOMER_ADMIN = + Employees
    if (employeeRole === 'WORKER') {
      return NAV_ITEMS.filter(i => i.to === '/dashboard' || i.to === '/timesheets');
    }
    if (employeeRole === 'COMPANY_ADMIN' || employeeRole === 'CUSTOMER_ADMIN') {
      return NAV_ITEMS.filter(i =>
        ['/dashboard', '/employees', '/timesheets'].includes(i.to)
      );
    }
    // USER but /me not loaded yet or 404: show minimal
    return NAV_ITEMS.filter(i => i.to === '/dashboard' || i.to === '/timesheets');
  }, [isAdmin, employeeRole]);

  const currentLabel = React.useMemo(() => {
    const item = visibleNavItems.find(i => location.pathname.startsWith(i.to));
    return item?.label ?? 'Overview';
  }, [location.pathname, visibleNavItems]);

  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="sidebar-logo">
          <div className="sidebar-logo-mark" />
          <span>CONSTRUCTFLOW CRM</span>
        </div>
        <div>
          <div className="sidebar-section-title">Workspace</div>
          <nav className="sidebar-nav">
            {visibleNavItems.map(item => (
              <NavLink
                key={item.to}
                to={item.to}
                className={({ isActive }) => ['sidebar-link', isActive ? 'active' : ''].join(' ').trim()}
              >
                <span className="sidebar-link-icon">{item.icon}</span>
                <span className="text">{item.label}</span>
              </NavLink>
            ))}
          </nav>
        </div>
        <div className="sidebar-footer">
          <div className="row-between">
            <span className="muted">Signed in as</span>
            <span className="badge badge-accent">{auth.username}</span>
          </div>
          <button className="btn btn-ghost full-width" onClick={auth.logout}>
            Log out
          </button>
        </div>
      </aside>
      <main className="main">
        <header className="topbar">
          <div className="topbar-left">
            <div className="topbar-title">{currentLabel}</div>
            <div className="topbar-subtitle">Operate your projects, people, budgets and time in one tight workspace.</div>
          </div>
          <div className="topbar-right">
            <TenantSwitcher />
            <div className="pill pill-strong">
              <span>
                {isAdmin
                  ? 'System Admin'
                  : employeeRole === 'CUSTOMER_ADMIN'
                    ? 'Customer Admin'
                    : employeeRole === 'COMPANY_ADMIN'
                      ? 'Company Admin'
                      : 'Worker'}
              </span>
            </div>
          </div>
        </header>
        <Outlet />
      </main>
    </div>
  );
};

