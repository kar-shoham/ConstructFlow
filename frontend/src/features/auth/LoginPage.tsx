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
        userRole: res.data.userRole
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
    <div className="layout-auth">
      <div className="auth-card">
        <div className="auth-header">
          <div className="row-between">
            <div>
              <div className="badge badge-accent">ConstructFlow</div>
            </div>
            <div className="chip">Internal users only</div>
          </div>
          <div className="auth-title">Sign in to your workspace</div>
          <div className="auth-subtitle">
            Use the account your admin created for your company. No public signup here.
          </div>
        </div>
        <form className="stack-lg" onSubmit={handleSubmit}>
          <div className="field">
            <label>Username</label>
            <input
              value={username}
              onChange={e => setUsername(e.target.value)}
              placeholder="your.name"
              autoComplete="username"
              required
            />
          </div>
          <div className="field">
            <label>Password</label>
            <input
              type="password"
              value={password}
              onChange={e => setPassword(e.target.value)}
              placeholder="••••••••"
              autoComplete="current-password"
              required
            />
            <div className="helper-text">If you don&apos;t have access, ask your company admin to create a user.</div>
          </div>
          {error && <div className="error-text">{error}</div>}
          <button className="btn btn-primary full-width" type="submit" disabled={loading}>
            {loading ? 'Signing in…' : 'Sign in'}
          </button>
        </form>
        <div className="chip-row">
          <div className="chip">JWT via API gateway</div>
          <div className="chip">No public registration</div>
          <div className="chip">Role-aware workspace</div>
        </div>
      </div>
    </div>
  );
};

