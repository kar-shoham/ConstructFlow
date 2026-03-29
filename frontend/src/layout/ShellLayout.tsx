import React from 'react';
import { NavLink, Outlet, useLocation } from 'react-router-dom';
import {
  LayoutDashboard, Users, Building2, Briefcase,
  UsersRound, CheckSquare, DollarSign, BarChart3, Clock, LogOut, Home
} from 'lucide-react';
import { useAuth } from '../state/AuthContext';
import { useCurrentEmployee } from '../state/CurrentEmployeeContext';
import { useTenant } from '../state/TenantContext';
import { TenantSwitcher } from './TenantSwitcher';

const NAV_ITEMS: { to: string; label: string; icon: React.ReactNode }[] = [
  { to: '/dashboard',      label: 'Dashboard',   icon: <Home size={15} /> },
  { to: '/customers',      label: 'Customers',   icon: <Users size={15} /> },
  { to: '/companies',      label: 'Companies',   icon: <Building2 size={15} /> },
  { to: '/projects',       label: 'Projects',    icon: <Briefcase size={15} /> },
  { to: '/employees',      label: 'Employees',   icon: <UsersRound size={15} /> },
  { to: '/tasks',          label: 'Tasks',       icon: <CheckSquare size={15} /> },
  { to: '/cost-codes',     label: 'Cost Codes',  icon: <DollarSign size={15} /> },
  { to: '/project-budgets',label: 'Budgets',     icon: <BarChart3 size={15} /> },
  { to: '/timesheets',     label: 'Timesheets',  icon: <Clock size={15} /> },
];

export const ShellLayout: React.FC = () => {
  const auth = useAuth();
  const location = useLocation();
  const { currentEmployee } = useCurrentEmployee();
  const { setCustomerId, setCompanyId } = useTenant();

  const isAdmin = auth.userRole === 'ADMIN';
  const employeeRole = currentEmployee?.employeeRole ?? null;

  React.useEffect(() => {
    if (!currentEmployee) return;
    setCustomerId(currentEmployee.customerId);
    setCompanyId(currentEmployee.companyId);
  }, [currentEmployee, setCustomerId, setCompanyId]);

  const visibleNavItems = React.useMemo(() => {
    if (isAdmin) return NAV_ITEMS;
    if (employeeRole === 'WORKER') {
      return NAV_ITEMS.filter(i => i.to === '/dashboard' || i.to === '/timesheets');
    }
    if (employeeRole === 'CUSTOMER_ADMIN') {
      return NAV_ITEMS.filter(i => [
        '/dashboard', '/companies', '/projects', '/employees', 
        '/tasks', '/cost-codes', '/project-budgets', '/timesheets'
      ].includes(i.to));
    }
    if (employeeRole === 'COMPANY_ADMIN') {
      return NAV_ITEMS.filter(i => ['/dashboard', '/employees', '/timesheets'].includes(i.to));
    }
    return NAV_ITEMS.filter(i => i.to === '/dashboard' || i.to === '/timesheets');
  }, [isAdmin, employeeRole]);

  const currentLabel = React.useMemo(() => {
    const item = visibleNavItems.find(i => location.pathname.startsWith(i.to));
    return item?.label ?? 'Dashboard';
  }, [location.pathname, visibleNavItems]);

  const roleLabel = isAdmin
    ? 'System Admin'
    : employeeRole === 'CUSTOMER_ADMIN'
      ? 'Customer Admin'
      : employeeRole === 'COMPANY_ADMIN'
        ? 'Company Admin'
        : 'Worker';

  const avatarInitials = (auth.username ?? 'U').slice(0, 2).toUpperCase();

  return (
    <div className="app-shell">
      {/* ── Sidebar ── */}
      <aside className="sidebar">
        {/* Logo */}
        <div className="sidebar-logo">
          <div className="sidebar-logo-mark" />
          <span>ConstructFlow</span>
        </div>

        <div className="sidebar-divider" />

        {/* Nav */}
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

        {/* Footer: user info + logout */}
        <div className="sidebar-footer">
          <div className="sidebar-user">
            <div className="sidebar-user-avatar">{avatarInitials}</div>
            <div className="sidebar-user-info">
              <div className="sidebar-user-name">{auth.username}</div>
              <div className="sidebar-user-role">{roleLabel}</div>
            </div>
          </div>
          <button
            className="btn btn-ghost full-width"
            onClick={auth.logout}
            style={{
              color: '#6B7280',
              fontSize: 13,
              justifyContent: 'center',
              gap: 6,
              padding: '7px 10px',
              borderColor: '#1F2937',
            }}
          >
            <LogOut size={14} />
            <span className="text">Log out</span>
          </button>
        </div>
      </aside>

      {/* ── Main content ── */}
      <main className="main">
        <header className="topbar">
          <div className="topbar-left">
            <div className="topbar-title">{currentLabel}</div>
            <div className="topbar-subtitle">
              Manage projects, people, budgets and time in one workspace.
            </div>
          </div>
          <div className="topbar-right">
            <TenantSwitcher />
            <div className={isAdmin ? 'pill pill-accent' : 'pill pill-strong'}>
              {roleLabel}
            </div>
          </div>
        </header>
        <Outlet />
      </main>
    </div>
  );
};
