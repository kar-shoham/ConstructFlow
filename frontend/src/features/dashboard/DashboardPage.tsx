import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../state/AuthContext';
import { useCurrentEmployee } from '../../state/CurrentEmployeeContext';
import { LayoutDashboard, Users, Building2, Briefcase, ClipboardList, BookOpen, Calculator, Clock } from 'lucide-react';

export const DashboardPage: React.FC = () => {
  const navigate = useNavigate();
  const auth = useAuth();
  const { currentEmployee } = useCurrentEmployee();
  
  const roleName = auth.userRole === 'ADMIN' 
    ? 'Platform Admin' 
    : currentEmployee?.employeeRole?.replace('_', ' ')?.toLowerCase()?.replace(/\b\w/g, c => c.toUpperCase()) ?? 'Member';

  const firstName = currentEmployee?.firstName || auth.username;

  const getGreeting = () => {
    const hour = new Date().getHours();
    if (hour < 12) return 'Good Morning';
    if (hour < 18) return 'Good Afternoon';
    return 'Good Evening';
  };

  const actions = [
    { 
      label: 'Customers', 
      desc: 'Platform-wide account management', 
      icon: <Users size={24} />, 
      to: '/customers', 
      visible: auth.userRole === 'ADMIN' 
    },
    { 
      label: 'Companies', 
      desc: 'Tenant & subsidiary configuration', 
      icon: <Building2 size={24} />, 
      to: '/companies', 
      visible: auth.userRole === 'ADMIN' || currentEmployee?.employeeRole === 'CUSTOMER_ADMIN' 
    },
    { 
      label: 'Projects', 
      desc: 'Active construction roadmaps', 
      icon: <Briefcase size={24} />, 
      to: '/projects', 
      visible: true 
    },
    { 
      label: 'Employees', 
      desc: 'Workforce & role assignments', 
      icon: <Users size={24} />, 
      to: '/employees', 
      visible: auth.userRole === 'ADMIN' || currentEmployee?.employeeRole === 'CUSTOMER_ADMIN' || currentEmployee?.employeeRole === 'COMPANY_ADMIN'
    },
    { 
      label: 'Timesheets', 
      desc: 'Hours, logs & labor validation', 
      icon: <Clock size={24} />, 
      to: '/timesheets', 
      visible: true 
    },
    { 
      label: 'Budgets', 
      desc: 'Financial controls & allocation', 
      icon: <Calculator size={24} />, 
      to: '/project-budgets', 
      visible: auth.userRole === 'ADMIN' || currentEmployee?.employeeRole === 'CUSTOMER_ADMIN' 
    },
  ].filter(a => a.visible);

  return (
    <div style={{ animation: 'fade-in 0.5s ease-out' }}>
      {/* Hero Section */}
      <div 
        style={{ 
          background: 'linear-gradient(135deg, #111827 0%, #1f2937 100%)', 
          borderRadius: 24, 
          padding: '48px 40px', 
          color: 'white',
          marginBottom: 32,
          position: 'relative',
          overflow: 'hidden',
          boxShadow: '0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04)'
        }}
      >
        <div style={{ position: 'relative', zIndex: 2 }}>
          <div style={{ 
            display: 'inline-flex', 
            alignItems: 'center', 
            gap: 8, 
            background: 'rgba(255, 122, 61, 0.15)', 
            padding: '6px 14px', 
            borderRadius: 100, 
            color: '#FF7A3D', 
            fontSize: 13, 
            fontWeight: 600, 
            marginBottom: 20,
            border: '1px solid rgba(255, 122, 61, 0.2)'
          }}>
            <LayoutDashboard size={14} />
            {roleName}
          </div>
          
          <h1 style={{ fontSize: 42, fontWeight: 800, margin: 0, letterSpacing: '-0.03em', lineHeight: 1.1 }}>
            {getGreeting()},<br />
            <span style={{ color: '#FF7A3D' }}>{firstName}.</span>
          </h1>
          
          <p style={{ marginTop: 16, fontSize: 16, color: '#9CA3AF', maxWidth: 480, lineHeight: 1.6 }}>
            Welcome back to ConstructFlow. Your project data is synced and ready for the shift ahead.
          </p>
        </div>

        {/* Abstract Background Element */}
        <div style={{ 
          position: 'absolute', 
          top: -100, 
          right: -50, 
          width: 400, 
          height: 400, 
          background: 'radial-gradient(circle, rgba(242, 101, 34, 0.1) 0%, transparent 70%)',
          borderRadius: '50%',
          filter: 'blur(40px)'
        }} />
      </div>

      {/* Grid of Actions */}
      <div className="grid-three" style={{ gap: 24 }}>
        {actions.map((action, i) => (
          <div 
            key={action.label}
            className="panel"
            onClick={() => navigate(action.to)}
            style={{ 
              cursor: 'pointer',
              padding: 24,
              transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
              display: 'flex',
              flexDirection: 'column',
              gap: 16,
              border: '1px solid var(--color-border)',
              animation: `slide-up 0.5s ease-out ${i * 0.1}s both`
            }}
            onMouseEnter={e => {
              e.currentTarget.style.transform = 'translateY(-4px)';
              e.currentTarget.style.borderColor = 'var(--color-accent)';
              e.currentTarget.style.boxShadow = '0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05)';
            }}
            onMouseLeave={e => {
              e.currentTarget.style.transform = 'translateY(0)';
              e.currentTarget.style.borderColor = 'var(--color-border)';
              e.currentTarget.style.boxShadow = 'none';
            }}
          >
            <div style={{ 
              width: 48, 
              height: 48, 
              borderRadius: 12, 
              background: 'var(--color-accent-light)', 
              color: 'var(--color-accent)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              marginBottom: 4
            }}>
              {action.icon}
            </div>
            <div>
              <div style={{ fontSize: 18, fontWeight: 700, color: 'var(--color-text)', marginBottom: 4 }}>{action.label}</div>
              <div style={{ fontSize: 13, color: 'var(--color-text-muted)', lineHeight: 1.4 }}>{action.desc}</div>
            </div>
          </div>
        ))}
      </div>

      {/* Footer Info */}
      <div style={{ marginTop: 40, display: 'flex', justifyContent: 'space-between', alignItems: 'center', color: 'var(--color-text-muted)', fontSize: 13 }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
            <div style={{ width: 8, height: 8, borderRadius: '50%', background: '#10B981', boxShadow: '0 0 8px rgba(16, 185, 129, 0.5)' }} />
            System Live
          </div>
          <div>v0.9.4 PRE-RELEASE</div>
        </div>
        <div>Last login: {new Date().toLocaleDateString(undefined, { weekday: 'long', month: 'short', day: 'numeric' })}</div>
      </div>

      <style>{`
        @keyframes fade-in {
          from { opacity: 0; }
          to { opacity: 1; }
        }
        @keyframes slide-up {
          from { opacity: 0; transform: translateY(20px); }
          to { opacity: 1; transform: translateY(0); }
        }
      `}</style>
    </div>
  );
};
