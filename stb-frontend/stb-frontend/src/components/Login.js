import React, { useState } from 'react';
import { authService } from '../services/api';
import './Login.css';

function Login({ onLogin, onSwitchToSignup }) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const response = await authService.login(username, password);
      const { token, role, username: userName } = response.data;
      localStorage.setItem('token', token);
      localStorage.setItem('user', JSON.stringify({ username: userName, role }));
      onLogin({ username: userName, role });
    } catch (err) {
      console.error('Login error:', err);
      setError(err.response?.data?.message || 'Login failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <div className="login-card">
        <h1>Login</h1>
        <form onSubmit={handleSubmit}>
          <input
            type="text"
            placeholder="Username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />
          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
          {error && <div className="error">{error}</div>}
          <button type="submit" disabled={loading}>
            {loading ? 'Logging in...' : 'Login'}
          </button>
        </form>
        <p>
          Don't have an account?{' '}
          <span className="link" onClick={onSwitchToSignup}>Sign Up</span>
        </p>
        <div className="demo">
        </div>
      </div>
    </div>
  );
}

export default Login;