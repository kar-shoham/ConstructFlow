import React, { useEffect } from 'react';
import { Navigate, Route, Routes, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from './state/AuthContext';
import { useCurrentEmployee } from './state/CurrentEmployeeContext';
import { setAuthState } from './api/client';
import { LoginPage } from './features/auth/LoginPage';
import { ShellLayout } from './layout/ShellLayout';
import { DashboardPage } from './features/dashboard/DashboardPage';
import { CustomersPage } from './features/customers/CustomersPage';
import { CompaniesPage } from './features/companies/CompaniesPage';
import { ProjectsPage } from './features/projects/ProjectsPage';
import { EmployeesPage } from './features/employees/EmployeesPage';
import { TasksPage } from './features/tasks/TasksPage';
import { CostCodesPage } from './features/costcodes/CostCodesPage';
import { ProjectBudgetsPage } from './features/projectbudgets/ProjectBudgetsPage';
import { TimesheetsPage } from './features/timesheets/TimesheetsPage';

const RequireAuth: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { isAuthenticated } = useAuth();
  const location = useLocation();
  if (!isAuthenticated) {
    return <Navigate to="/login" replace state={{ from: location }} />;
  }
  return <>{children}</>;
};

const RequireAdmin: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { userRole } = useAuth();
  if (userRole !== 'ADMIN') {
    return <Navigate to="/dashboard" replace />;
  }
  return <>{children}</>;
};

/** Allow ADMIN or USER with COMPANY_ADMIN / CUSTOMER_ADMIN (for Employees page). */
const RequireEmployeeAdmin: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { userRole } = useAuth();
  const { currentEmployee } = useCurrentEmployee();
  if (userRole === 'ADMIN') return <>{children}</>;
  const role = currentEmployee?.employeeRole;
  if (role === 'COMPANY_ADMIN' || role === 'CUSTOMER_ADMIN') return <>{children}</>;
  return <Navigate to="/dashboard" replace />;
};

export const App: React.FC = () => {
  const auth = useAuth();
  const location = useLocation();
  const navigate = useNavigate();

  useEffect(() => {
    setAuthState(
      auth.isAuthenticated
        ? { token: auth.token, userId: auth.userId, username: auth.username, userRole: auth.userRole }
        : null
    );
  }, [auth.isAuthenticated, auth.token, auth.userId, auth.username, auth.userRole]);

  useEffect(() => {
    if (auth.isAuthenticated && location.pathname === '/') {
      navigate('/dashboard', { replace: true });
    }
  }, [auth.isAuthenticated, location.pathname, navigate]);

  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route
        path="/"
        element={
          <RequireAuth>
            <ShellLayout />
          </RequireAuth>
        }
      >
        <Route path="dashboard" element={<DashboardPage />} />
        <Route
          path="customers"
          element={
            <RequireAdmin>
              <CustomersPage />
            </RequireAdmin>
          }
        />
        <Route
          path="companies"
          element={
            <RequireAdmin>
              <CompaniesPage />
            </RequireAdmin>
          }
        />
        <Route path="projects" element={<ProjectsPage />} />
        <Route
          path="employees"
          element={
            <RequireEmployeeAdmin>
              <EmployeesPage />
            </RequireEmployeeAdmin>
          }
        />
        <Route path="tasks" element={<TasksPage />} />
        <Route
          path="cost-codes"
          element={
            <RequireAdmin>
              <CostCodesPage />
            </RequireAdmin>
          }
        />
        <Route
          path="project-budgets"
          element={
            <RequireAdmin>
              <ProjectBudgetsPage />
            </RequireAdmin>
          }
        />
        <Route path="timesheets" element={<TimesheetsPage />} />
      </Route>
      <Route path="*" element={<Navigate to={auth.isAuthenticated ? '/dashboard' : '/login'} replace />} />
    </Routes>
  );
};

