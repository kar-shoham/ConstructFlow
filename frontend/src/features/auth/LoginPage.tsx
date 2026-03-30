import React from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../../state/AuthContext';
import { loginClient } from '../../api/client';

interface AuthResponseDto {
  userId: number;
  username: string;
  token: string;
  userRole: 'USER' | 'ADMIN';
}

interface LocationState {
  from?: Location;
}

export const LoginPage: React.FC = () => {
  const [username, setUsername] = React.useState('');
  const [password, setPassword] = React.useState('');
  const [loading, setLoading] = React.useState(false);
  const [error, setError] = React.useState<string | null>(null);
  const navigate = useNavigate();
  const auth = useAuth();
  const location = useLocation();

  const from = (location.state as LocationState | null)?.from?.pathname ?? '/dashboard';

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      const res = await loginClient.post<AuthResponseDto>('/auth/login', { username, password });
      auth.login({
        token: res.data.token,
        userId: res.data.userId,
        username: res.data.username,
        userRole: res.data.userRole,
      });
      navigate(from, { replace: true });
    } catch (err: any) {
      const message = err?.response?.data?.message ?? 'Invalid credentials or server unreachable.';
      setError(message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-split">
      {/* ── Left: Dark brand panel ── */}
      <div className="auth-brand">
        <div className="auth-brand-logo">
          <div className="auth-brand-logo-mark" />
          <span className="auth-brand-name">ConstructFlow</span>
        </div>

        <div className="auth-brand-content">
          <div className="auth-brand-slogan">
            Precision in<br />
            <span className="auth-brand-slogan-accent">Every Build.</span>
          </div>

          <p className="auth-brand-desc">
            The all-in-one platform for managing construction projects, teams, timesheets, and budgets — built for precision at every level.
          </p>

          <div className="auth-brand-features">
            {[
              'Multi-tenant project & customer management',
              'Role-based access for teams of any size',
              'Real-time timesheet tracking & validation',
              'Cost code hierarchy and budget controls',
            ].map(f => (
              <div className="auth-brand-feature" key={f}>
                <div className="auth-brand-feature-dot" />
                {f}
              </div>
            ))}
          </div>
        </div>

        <div className="auth-brand-footer">
          © {new Date().getFullYear()} ConstructFlow · Internal platform · All rights reserved
        </div>
      </div>

      {/* ── Right: White login panel ── */}
      <div className="auth-form-panel">
        <div className="auth-form-inner">
          {/* Header */}
          <div style={{ marginBottom: 32 }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 20 }}>
              <div
                style={{
                  width: 40,
                  height: 40,
                  borderRadius: 10,
                  background: 'linear-gradient(135deg, #FF7A3D, #F26522)',
                  boxShadow: '0 4px 14px rgba(242,101,34,0.35)',
                }}
              />
              <span style={{ fontWeight: 800, fontSize: 15, letterSpacing: '0.05em', textTransform: 'uppercase' as const, color: '#111827' }}>
                ConstructFlow
              </span>
            </div>
            <h1 style={{ margin: 0, fontSize: 28, fontWeight: 800, color: '#111827', letterSpacing: '-0.02em', lineHeight: 1.2 }}>
              Welcome back
            </h1>
            <p style={{ margin: '8px 0 0', fontSize: 14, color: '#6B7280', lineHeight: 1.5 }}>
              Sign in to your workspace. Access is granted by your company admin.
            </p>
          </div>

          {/* Form */}
          <form onSubmit={handleSubmit} className="stack-lg">
            <div className="field">
              <label>Username</label>
              <input
                value={username}
                onChange={e => setUsername(e.target.value)}
                placeholder="your.username"
                autoComplete="off"
                required
                style={{ padding: '10px 12px', fontSize: 15 }}
              />
            </div>

            <div className="field">
              <label>Password</label>
              <input
                type="text"
                value={password}
                onChange={e => setPassword(e.target.value)}
                placeholder="••••••••"
                autoComplete="off"
                className="no-password-manager"
                required
                style={{ padding: '10px 12px', fontSize: 15 }}
              />
              <div className="helper-text">Don&apos;t have access? Ask your company admin to create an account for you.</div>
            </div>

            {error && <div className="error-text">{error}</div>}

            <button
              className="btn btn-primary full-width"
              type="submit"
              disabled={loading}
              style={{ padding: '11px 16px', fontSize: 15, justifyContent: 'center', marginTop: 4 }}
            >
              {loading ? 'Signing in…' : 'Sign in →'}
            </button>
          </form>

          {/* Meta chips */}
          <div className="chip-row" style={{ marginTop: 20 }}>
            <div className="chip">🔒 JWT secured</div>
            <div className="chip">Role-aware workspace</div>
            <div className="chip">No public registration</div>
          </div>
        </div>
      </div>
    </div>
  );
};
